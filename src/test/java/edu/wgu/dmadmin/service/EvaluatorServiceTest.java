package edu.wgu.dmadmin.service;

import static edu.wgu.dmadmin.util.StatusUtil.*;

import edu.wgu.common.exception.AuthorizationException;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorConfirmation;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorDashboard;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorWorkspace;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.submission.DashboardSubmission;
import edu.wgu.dmadmin.domain.submission.Referral;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.IncompleteScoreReportException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.exception.WorkingEvaluationException;
import edu.wgu.dmadmin.model.assessment.EvaluationByEvaluatorModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStatusAndTaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.StatusUtil;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
@RunWith(MockitoJUnitRunner.class)
public class EvaluatorServiceTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private EvaluatorService evaluatorService;
    
    @Mock
    private SubmissionUtilityService suService;
    
    @Mock
    PublishAcademicActivityService aaService;

    @Mock
    private CassandraRepo cassandraRepo;
    
	protected UUID task1 = UUID.randomUUID();
	protected UUID task2 = UUID.randomUUID();
	protected UUID task3 = UUID.randomUUID();
	
	UserByIdModel user = TestObjectFactory.getUserModel();
	
	protected List<String> studentIds = Arrays.asList("student1", "student2", "student3");
	protected List<String> firstNames = Arrays.asList("first1", "first2", "first3");
	protected List<String> lastNames = Arrays.asList("last1", "last2", "last3");
	protected List<String> evalIds = Arrays.asList("E00485967", "E00485686", "E00348585", user.getUserId());
	protected List<String> statuses = Arrays.asList(AUTHOR_WORK_SUBMITTED, AUTHOR_WORK_RESUBMITTED, EVALUATION_CANCELLED, OPEN_HOLD);
	protected List<UUID> tasks = Arrays.asList(task1, task2, task3);
	
	protected List<SubmissionModel> pendingSubs = TestObjectFactory.getSubmissions(studentIds, firstNames, lastNames, statuses, tasks, evalIds, 100);
	
    EvaluationByEvaluatorModel eval1 = TestObjectFactory.getEvaluationByEvaluatorModel();
    EvaluationByEvaluatorModel eval2 = TestObjectFactory.getEvaluationByEvaluatorModel();
    EvaluationByEvaluatorModel eval3 = TestObjectFactory.getEvaluationByEvaluatorModel();
    
    List<EvaluationByEvaluatorModel> evals = Arrays.asList(eval1, eval2, eval3);
    
    SubmissionByIdModel working1 = TestObjectFactory.getSubmissionByIdModel(StatusUtil.EVALUATION_BEGUN);
    SubmissionByIdModel working2 = TestObjectFactory.getSubmissionByIdModel(StatusUtil.EVALUATION_BEGUN);
    SubmissionByIdModel working3 = TestObjectFactory.getSubmissionByIdModel(StatusUtil.EVALUATION_BEGUN);
    
    List<SubmissionByIdModel> workingSubs = Arrays.asList(working1, working2, working3);
    
    List<String> pendingStatuses;
    List<String> attemptHold;
    
    TaskByIdModel task = TestObjectFactory.getTaskByIdModel(UUID.randomUUID(), working1.getTaskId(), new Long(23423), 1, 1, "testing");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.evaluatorService.setCassandraRepo(cassandraRepo);
        this.evaluatorService.setSubmissionUtilityService(suService);
        
        user.getTasks().add(task1);
        user.getTasks().add(task2);

        when(this.cassandraRepo.getUser(user.getUserId())).thenReturn(Optional.of(user));
        when(this.cassandraRepo.getSubmissionsByStatusAndEvaluator(user.getUserId(), StatusUtil.WORKING)).thenReturn(evals);
        when(this.cassandraRepo.getSubmissionsById(any())).thenReturn(workingSubs);
        
        this.pendingStatuses = Arrays.asList(AUTHOR_WORK_SUBMITTED, AUTHOR_WORK_RESUBMITTED, EVALUATION_CANCELLED);
        this.attemptHold = Arrays.asList(AUTHOR_WORK_EVALUATED);
        
    	working1.setEvaluationId(eval1.getEvaluationId());
    	working1.setEvaluatorId(user.getUserId());
    	when(cassandraRepo.getSubmissionById(working1.getSubmissionId())).thenReturn(Optional.of(working1));
    	
    	when(cassandraRepo.getTaskBasics(working1.getTaskId())).thenReturn(Optional.of(task));
    	when(cassandraRepo.getEvaluationById(working1.getEvaluationId())).thenReturn(Optional.of(new EvaluationByIdModel(eval1)));
    }
    
    @Test
    public void testGetEvaluatorDashboard() {
    	List<SubmissionByStatusAndTaskModel> qualified = this.pendingSubs.stream()
    			.filter(s -> this.evaluatorService.isQualified(this.user, s.getStatus(), s.getTaskId()))
    			.map(s -> new SubmissionByStatusAndTaskModel(s))
    			.collect(Collectors.toList());
    	
    	DashboardSubmission first = qualified.stream()
    			.map(s -> new DashboardSubmission(s))
    			.min(Comparator.naturalOrder())
    			.orElse(null);
    	
    	List<UUID> userTasks = Arrays.asList(task1, task2);
    	
    	when(this.cassandraRepo.getSubmissionsByStatusesAndTasks(any(), any())).thenReturn(qualified);
    	EvaluatorDashboard dashboard = this.evaluatorService.getEvaluatorDashboard(user.getUserId());
    	assertEquals(user.getFirstName(), dashboard.getFirstName());
    	assertEquals(first.getSubmissionId(), dashboard.getPendingQueue().get(0).getSubmissionId());

		ArgumentCaptor<List<String>> statusCaptor = ArgumentCaptor.forClass((Class) List.class);
    	ArgumentCaptor<List<UUID>> taskCaptor = ArgumentCaptor.forClass((Class) List.class);
    	
    	verify(this.cassandraRepo).getSubmissionsByStatusesAndTasks(statusCaptor.capture(), taskCaptor.capture());
    	assertTrue(CollectionUtils.isEqualCollection(this.pendingStatuses, statusCaptor.getValue()));
    	assertTrue(CollectionUtils.isEqualCollection(userTasks, taskCaptor.getValue()));
    }
    
    @Test
    public void testGetEvaluatorDashboardOpenQueue() {
    	this.user.getPermissions().add(Permissions.OPEN_QUEUE);
    	List<UUID> userTasks = Arrays.asList(task1, task2);
    	List<String> userStatuses = Arrays.asList(OPEN_HOLD, AUTHOR_WORK_RESUBMITTED, EVALUATION_CANCELLED, AUTHOR_WORK_SUBMITTED);
    	
    	List<SubmissionByStatusAndTaskModel> qualified = this.pendingSubs.stream()
    			.filter(s -> this.evaluatorService.isQualified(this.user, s.getStatus(), s.getTaskId()))
    			.map(s -> new SubmissionByStatusAndTaskModel(s))
    			.collect(Collectors.toList());
    	
    	DashboardSubmission first = qualified.stream()
    			.map(s -> new DashboardSubmission(s))
    			.min(Comparator.naturalOrder())
    			.orElse(null);
    	
    	when(this.cassandraRepo.getSubmissionsByStatusesAndTasks(any(), any())).thenReturn(qualified);
    	EvaluatorDashboard dashboard = this.evaluatorService.getEvaluatorDashboard(user.getUserId());
    	assertEquals(user.getFirstName(), dashboard.getFirstName());
    	assertEquals(first.getSubmissionId(), dashboard.getPendingQueue().get(0).getSubmissionId());

		ArgumentCaptor<List<String>> statusCaptor = ArgumentCaptor.forClass((Class) List.class);
    	ArgumentCaptor<List<UUID>> taskCaptor = ArgumentCaptor.forClass((Class) List.class);
    	
    	verify(this.cassandraRepo).getSubmissionsByStatusesAndTasks(statusCaptor.capture(), taskCaptor.capture());
    	assertTrue(CollectionUtils.isEqualCollection(userStatuses, statusCaptor.getValue()));
    	assertTrue(CollectionUtils.isEqualCollection(userTasks, taskCaptor.getValue()));
    }
    
    @Test
    public void testGetEvaluatorDashboardOpenOnlyQueue() {
    	this.user.getPermissions().add(Permissions.OPEN_QUEUE);
    	this.user.getPermissions().remove(Permissions.TASK_QUEUE);
    	this.user.setTasks(Collections.emptySet());
    	List<String> userStatuses = Arrays.asList(OPEN_HOLD);
    	
    	List<SubmissionByStatusAndTaskModel> qualified = this.pendingSubs.stream()
    			.filter(s -> this.evaluatorService.isQualified(this.user, s.getStatus(), s.getTaskId()))
    			.map(s -> new SubmissionByStatusAndTaskModel(s))
    			.collect(Collectors.toList());
    	
    	DashboardSubmission first = qualified.stream()
    			.map(s -> new DashboardSubmission(s))
    			.min(Comparator.naturalOrder())
    			.orElse(null);
    	
    	when(this.cassandraRepo.getSubmissionsByStatuses(any())).thenReturn(qualified);
    	EvaluatorDashboard dashboard = this.evaluatorService.getEvaluatorDashboard(user.getUserId());
    	assertEquals(user.getFirstName(), dashboard.getFirstName());
    	assertEquals(first.getSubmissionId(), dashboard.getPendingQueue().get(0).getSubmissionId());

		ArgumentCaptor<List<String>> statusCaptor = ArgumentCaptor.forClass((Class) List.class);
    	
    	verify(this.cassandraRepo).getSubmissionsByStatuses(statusCaptor.capture());
    	assertTrue(CollectionUtils.isEqualCollection(userStatuses, statusCaptor.getValue()));
    }
    
    @Test
    public void testGetEvaluatorDashboardNoQueue() {
    	this.user.getPermissions().remove(Permissions.TASK_QUEUE);

    	verify(this.cassandraRepo, never()).getSubmissionsByStatuses(any());
    	verify(this.cassandraRepo, never()).getSubmissionsByStatusesAndTasks(any(), any());
    }
    
    @Test
    public void testGetEvaluatorDashboardNoUser() {
    	thrown.expect(UserNotFoundException.class);
    	when(this.cassandraRepo.getUser(user.getUserId())).thenReturn(Optional.empty());
    	this.evaluatorService.getEvaluatorDashboard(user.getUserId());
    }
    
    @Test
    public void testGetEvaluatorDashboardNoWorking() {
    	when(this.cassandraRepo.getSubmissionsByStatusAndEvaluator(user.getUserId(), StatusUtil.WORKING)).thenReturn(Collections.emptyList());
    	
    	List<SubmissionByStatusAndTaskModel> qualified = this.pendingSubs.stream()
    			.filter(s -> this.evaluatorService.isQualified(this.user, s.getStatus(), s.getTaskId()))
    			.map(s -> new SubmissionByStatusAndTaskModel(s))
    			.collect(Collectors.toList());
    	
    	DashboardSubmission first = qualified.stream()
    			.map(s -> new DashboardSubmission(s))
    			.min(Comparator.naturalOrder())
    			.orElse(null);
    	
    	List<UUID> userTasks = Arrays.asList(task1, task2);
    	
    	when(this.cassandraRepo.getSubmissionsByStatusesAndTasks(any(), any())).thenReturn(qualified);
    	EvaluatorDashboard dashboard = this.evaluatorService.getEvaluatorDashboard(user.getUserId());
    	assertEquals(user.getFirstName(), dashboard.getFirstName());
    	assertEquals(first.getSubmissionId(), dashboard.getPendingQueue().get(0).getSubmissionId());
    	
    	verify(this.cassandraRepo, never()).getSubmissionsById(any());

		ArgumentCaptor<List<String>> statusCaptor = ArgumentCaptor.forClass((Class) List.class);
    	ArgumentCaptor<List<UUID>> taskCaptor = ArgumentCaptor.forClass((Class) List.class);
    	
    	verify(this.cassandraRepo).getSubmissionsByStatusesAndTasks(statusCaptor.capture(), taskCaptor.capture());
    	assertTrue(CollectionUtils.isEqualCollection(this.pendingStatuses, statusCaptor.getValue()));
    	assertTrue(CollectionUtils.isEqualCollection(userTasks, taskCaptor.getValue()));
    }
    
    @Test
    public void testEvaluatorWorkspace() {

    	List<SubmissionByStudentAndTaskModel> previous = this.workingSubs.stream()
    			.map(s -> new SubmissionByStudentAndTaskModel(s)).collect(Collectors.toList());
    	when(cassandraRepo.getSubmissionByStudentByTask(working1.getStudentId(), working1.getTaskId())).thenReturn(previous);
    	
    	EvaluatorWorkspace workspace = this.evaluatorService.getEvaluatorWorkspace(working1.getSubmissionId());
    	assertEquals(workspace.getSubmissionId(), working1.getSubmissionId());
    	assertEquals(workspace.getTaskId(), task.getTaskId());
    	assertEquals(workspace.getEvaluatorId(), working1.getEvaluatorId());
    	assertEquals(workspace.getDateEvaluationStarted(), eval1.getDateStarted());
    }

    @Test
    public void testEvaluatorWorkspaceNoSubmission() {

    	when(this.cassandraRepo.getSubmissionById(any(UUID.class))).thenReturn(Optional.empty());
    	
    	thrown.expect(SubmissionNotFoundException.class);
    	this.evaluatorService.getEvaluatorWorkspace(working1.getSubmissionId());
    }
    
    @Test
    public void testEvaluatorWorkspaceNoTask() {

    	when(this.cassandraRepo.getTaskBasics(any(UUID.class))).thenReturn(Optional.empty());
    	
    	thrown.expect(TaskNotFoundException.class);
    	this.evaluatorService.getEvaluatorWorkspace(working1.getSubmissionId());
    }
    
    @Test
    public void testEvaluatorWorkspaceNoEvaluation() {

    	when(this.cassandraRepo.getEvaluationById(any(UUID.class))).thenReturn(Optional.empty());
    	
    	thrown.expect(EvaluationNotFoundException.class);
    	this.evaluatorService.getEvaluatorWorkspace(working1.getSubmissionId());
    }
    
    @Test
    public void testGetEvaluatorWorkspaceNoPrevious() {
    	when(this.cassandraRepo.getSubmissionByStudentByTask(anyString(), any(UUID.class))).thenReturn(Collections.emptyList());
 
    	EvaluatorWorkspace workspace = this.evaluatorService.getEvaluatorWorkspace(working1.getSubmissionId());
    	assertEquals(workspace.getSubmissionId(), working1.getSubmissionId());
    	assertEquals(workspace.getTaskId(), task.getTaskId());
    	assertEquals(workspace.getEvaluatorId(), working1.getEvaluatorId());
    	assertEquals(workspace.getDateEvaluationStarted(), eval1.getDateStarted());
    	assertEquals(workspace.getPreviousSubmissions().size(), 0);
    }

    @Test
    public void testEvaluatorConfirmation() {
    	when(this.cassandraRepo.getConfirmationSubmission(working1.getSubmissionId())).thenReturn(Optional.of(working1));
    	when(this.cassandraRepo.getTaskRubric(working1.getTaskId())).thenReturn(Optional.of(task));

    	EvaluatorConfirmation conf = this.evaluatorService.getEvaluatorConfirmation(working1.getSubmissionId());
    	assertEquals(conf.getSubmissionId(), working1.getSubmissionId());
    	assertEquals(conf.getAspects().size(), task.getRubric().getAspects().size());
    	assertEquals(conf.getEvaluatorId(), working1.getEvaluatorId());
    }

    @Test
    public void testEvaluatorConfirmationNoSubmission() {

    	when(this.cassandraRepo.getConfirmationSubmission(any(UUID.class))).thenReturn(Optional.empty());
    	
    	thrown.expect(SubmissionNotFoundException.class);
    	this.evaluatorService.getEvaluatorConfirmation(working1.getSubmissionId());
    }
    
    @Test
    public void testEvaluatorConfirmationNoTask() {
    	when(this.cassandraRepo.getConfirmationSubmission(working1.getSubmissionId())).thenReturn(Optional.of(working1));
    	when(this.cassandraRepo.getTaskRubric(any(UUID.class))).thenReturn(Optional.empty());
    	
    	thrown.expect(TaskNotFoundException.class);
    	this.evaluatorService.getEvaluatorConfirmation(working1.getSubmissionId());
    }
    
    @Test
    public void testEvaluatorConfirmationNoEvaluation() {
    	when(this.cassandraRepo.getConfirmationSubmission(working1.getSubmissionId())).thenReturn(Optional.of(working1));
    	when(this.cassandraRepo.getTaskRubric(working1.getTaskId())).thenReturn(Optional.of(task));
    	when(this.cassandraRepo.getEvaluationById(any(UUID.class))).thenReturn(Optional.empty());
    	
    	thrown.expect(EvaluationNotFoundException.class);
    	this.evaluatorService.getEvaluatorConfirmation(working1.getSubmissionId());
    }
    
    @Test
    public void testReleaseEvaluation() throws EvaluationNotFoundException, IncompleteScoreReportException, EvaluationStatusException {
    	Comment comment = new Comment(TestObjectFactory.getCommentModel(user.getUserId(), user.getFirstName(), user.getLastName(), CommentTypes.STUDENT, 1, 2));

    	ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
    	ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
    	ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);
    	
    	this.evaluatorService.releaseEvaluation(user.getUserId(), working1.getSubmissionId(), true, comment);
    	verify(this.suService).releaseEvaluation(arg1.capture(), arg2.capture(), eq(true), arg4.capture());
    	assertEquals(user.getUserId(), arg1.getValue());
    	assertEquals(working1.getSubmissionId(), arg2.getValue().getSubmissionId());
    	assertEquals(comment.getCommentId(), arg4.getValue().getCommentId());
    }
    
    @Test
    public void testReleaseEvaluationWrongEvaluator() throws EvaluationNotFoundException, IncompleteScoreReportException, EvaluationStatusException {
    	Comment comment = new Comment(TestObjectFactory.getCommentModel(user.getUserId(), user.getFirstName(), user.getLastName(), CommentTypes.STUDENT, 1, 2));
    	working1.setEvaluatorId("testing");
    	
    	thrown.expect(AuthorizationException.class);
    	this.evaluatorService.releaseEvaluation(user.getUserId(), working1.getSubmissionId(), true, comment);
    }
    
    @Test
    public void testReleaseEvaluationNoSub() throws EvaluationNotFoundException, IncompleteScoreReportException, EvaluationStatusException {
    	Comment comment = new Comment(TestObjectFactory.getCommentModel(user.getUserId(), user.getFirstName(), user.getLastName(), CommentTypes.STUDENT, 1, 2));
    	when(this.cassandraRepo.getSubmissionById(working1.getSubmissionId())).thenReturn(Optional.empty());
    	
    	thrown.expect(SubmissionNotFoundException.class);
    	this.evaluatorService.releaseEvaluation(user.getUserId(), working1.getSubmissionId(), true, comment);
    }
    
    @Test
    public void testCancelEvaluation() throws EvaluationNotFoundException, EvaluationStatusException {
    	ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
    	ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
    	ArgumentCaptor<Comment> arg3 = ArgumentCaptor.forClass(Comment.class);
    	
    	this.evaluatorService.cancelEvaluation(user.getUserId(), working1.getSubmissionId(), "testing");
    	verify(this.suService).cancelEvaluation(arg1.capture(), arg2.capture(), arg3.capture());
    	assertEquals(user.getUserId(), arg1.getValue());
    	assertEquals(working1.getSubmissionId(), arg2.getValue().getSubmissionId());
    	assertEquals("testing", arg3.getValue().getComments());
    }
    
    @Test
    public void testCancelEvaluationWrongEvaluator() throws EvaluationNotFoundException, EvaluationStatusException {
    	working1.setEvaluatorId("testing");
    	
    	thrown.expect(AuthorizationException.class);
    	this.evaluatorService.cancelEvaluation(user.getUserId(), working1.getSubmissionId(), "testing");
    }
    
    @Test
    public void testCancelEvaluationNoSub() throws EvaluationNotFoundException, EvaluationStatusException {
    	when(this.cassandraRepo.getSubmissionById(working1.getSubmissionId())).thenReturn(Optional.empty());
    	
    	thrown.expect(SubmissionNotFoundException.class);
    	this.evaluatorService.cancelEvaluation(user.getUserId(), working1.getSubmissionId(), "testing");
    }
    
    @Test
    public void testSaveReportComment() throws WorkingEvaluationException {
    	Comment comment = new Comment(TestObjectFactory.getCommentModel(user.getUserId(), user.getFirstName(), user.getLastName(), CommentTypes.STUDENT, 1, 2));
    	comment.setCommentId(null);
    	when(this.suService.getWorkingEvaluation(eval1.getEvaluatorId(), eval1.getSubmissionId())).thenReturn(eval1);
    	
    	Comment result = this.evaluatorService.saveReportComment(eval1.getEvaluatorId(), eval1.getSubmissionId(), comment);
    	ArgumentCaptor<EvaluationModel> arg1 = ArgumentCaptor.forClass(EvaluationModel.class);
    	verify(this.cassandraRepo).saveScoreReport(arg1.capture());
    	assertEquals(eval1.getEvaluatorFirstName(), arg1.getValue().getEvaluatorFirstName());
    	assertEquals(result.getLastName(), eval1.getEvaluatorLastName());
    }
    
    @Test
    public void testSaveReportCommentNoEvaluation() throws WorkingEvaluationException {
    	Comment comment = new Comment(TestObjectFactory.getCommentModel(user.getUserId(), user.getFirstName(), user.getLastName(), CommentTypes.STUDENT, 1, 2));
    	comment.setCommentId(null);
    	when(this.suService.getWorkingEvaluation(eval1.getEvaluatorId(), eval1.getSubmissionId())).thenThrow(new WorkingEvaluationException(eval1.getSubmissionId()));
    	
    	thrown.expect(WorkingEvaluationException.class);
    	this.evaluatorService.saveReportComment(eval1.getEvaluatorId(), eval1.getSubmissionId(), comment);
    }
    
    @Test
    public void testSaveReferral() throws SubmissionStatusException {
    	Referral referral = new Referral(TestObjectFactory.getReferralModel());
    	
    	List<Referral> referrals = this.evaluatorService.saveReferral(user.getUserId(), working1.getSubmissionId(), referral);
    	assertEquals(referrals.get(0), referral);
    }
    
    @Test
    public void testSaveReferralNoSubmission() throws SubmissionStatusException {
    	Referral referral = new Referral(TestObjectFactory.getReferralModel());
    	when(this.cassandraRepo.getSubmissionById(working1.getSubmissionId())).thenReturn(Optional.empty());
    	
    	thrown.expect(SubmissionNotFoundException.class);
    	this.evaluatorService.saveReferral(user.getUserId(), working1.getSubmissionId(), referral);
    }
    
    
    @Test
    public void testSaveReferralBadStatus() throws SubmissionStatusException {
    	working1.setStatus(AUTHOR_WORK_SUBMITTED);
    	Referral referral = new Referral(TestObjectFactory.getReferralModel());
    	
    	thrown.expect(SubmissionStatusException.class);
    	this.evaluatorService.saveReferral(user.getUserId(), working1.getSubmissionId(), referral);
    }
}
