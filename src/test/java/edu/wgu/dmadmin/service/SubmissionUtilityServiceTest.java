package edu.wgu.dmadmin.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.WorkingEvaluationException;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByEvaluatorModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.StatusUtil;

/**
 * Created by joshua.barnett on 6/15/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SubmissionUtilityServiceTest {
    @InjectMocks
    private SubmissionUtilityService submissionUtilityService;

    @Mock
    CassandraRepo repo;

    @Mock
    PublishAcademicActivityService publishAcademicActivityService;

    EvaluationByEvaluatorModel evalModel1;
    EvaluationByEvaluatorModel evalModel2;
    EvaluationByEvaluatorModel evalModel3;

    private String titleValue = "Test.Title";
    private String returnValue = "Test.Return";
    private String passedValue = "Test.Passed";
    
    CommentModel comment1 = TestObjectFactory.getCommentModel("userId1", "Bruce", "Wayne", CommentTypes.INTERNAL, 1, 2);
    CommentModel comment2 = TestObjectFactory.getCommentModel("userId2", "Lois", "Lane", CommentTypes.INTERNAL, 2, 1);
    SubmissionByIdModel sub = TestObjectFactory.getSubmissionByIdModel(StatusUtil.EVALUATION_BEGUN);
    UserByIdModel user1 = TestObjectFactory.getUserModel();
    
	@Rule
	public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.submissionUtilityService.setCassandraRepo(repo);

        Field academicActivityTitle = findField(submissionUtilityService.getClass(), "academicActivityTitle");
        Field academicActivityReturnForRevision = findField(submissionUtilityService.getClass(), "academicActivityReturnForRevision");
        Field academicActivityPassed = findField(submissionUtilityService.getClass(), "academicActivityPassed");

        makeAccessible(academicActivityPassed);
        makeAccessible(academicActivityTitle);
        makeAccessible(academicActivityReturnForRevision);

        setField(academicActivityTitle, submissionUtilityService, titleValue);
        setField(academicActivityReturnForRevision, submissionUtilityService, returnValue);
        setField(academicActivityPassed, submissionUtilityService, passedValue);
        
        evalModel1 = TestObjectFactory.getEvaluationByEvaluatorModel();
        evalModel2 = TestObjectFactory.getEvaluationByEvaluatorModel();
        evalModel3 = TestObjectFactory.getEvaluationByEvaluatorModel();
        
        this.sub.getInternalCommentsNS().put(comment1.getCommentId(), comment1);
        this.sub.getInternalCommentsNS().put(comment2.getCommentId(), comment2);
        
        when(this.repo.getSubmissionById(sub.getSubmissionId())).thenReturn(Optional.of(sub));
        when(this.repo.getInternalComments(sub.getSubmissionId())).thenReturn(Optional.of(sub));
        when(this.repo.getUser(user1.getUserId())).thenReturn(Optional.of(user1));
    }
    
    @Test
    public void testGetInternalComments() {
    	List<Comment> result = this.submissionUtilityService.getInternalComments(this.sub.getSubmissionId());
    	assertEquals(2, result.size());
    	assertTrue(result.stream().map(c -> c.getCommentId()).collect(Collectors.toList()).contains(comment1.getCommentId()));
    }
    
    @Test
    public void testGetInternalCommentsNotFound() {
    	when(this.repo.getInternalComments(sub.getSubmissionId())).thenReturn(Optional.empty());
    	
    	thrown.expect(SubmissionNotFoundException.class);
    	this.submissionUtilityService.getInternalComments(this.sub.getSubmissionId());
    }
    
    @Test
    public void testUpdateInternalComments() {
    	this.sub.setInternalComments(new HashMap<UUID, CommentModel>());
    	
        CommentModel comment3 = TestObjectFactory.getCommentModel("userId3", "Super", "Man", CommentTypes.INTERNAL, 1, 2);
        CommentModel comment4 = TestObjectFactory.getCommentModel("userId4", "Darth", "Vader", CommentTypes.INTERNAL, 2, 1);
        
    	List<Comment> newComments = Arrays.asList(comment3, comment4).stream().map(c -> new Comment(c)).collect(Collectors.toList());
    	
    	List<Comment> result = this.submissionUtilityService.updateInternalComments(user1.getUserId(), this.sub.getSubmissionId(), newComments);
    	assertTrue(CollectionUtils.isEqualCollection(newComments, result));
    }
    
    @Test
    public void testUpdateInternalCommentsNoSub() {
    	when(this.repo.getSubmissionById(sub.getSubmissionId())).thenReturn(Optional.empty());
    	
    	thrown.expect(SubmissionNotFoundException.class);
    	this.submissionUtilityService.updateInternalComments(user1.getUserId(), this.sub.getSubmissionId(), Collections.emptyList());
    }
    
    @Test
    public void testUpdateInternalCommentsNew() {
    	this.sub.setInternalComments(new HashMap<UUID, CommentModel>());
    	
        Comment newComment = new Comment();
        newComment.setAttempt(2);
        newComment.setUserId("test");
        newComment.setComments("testing new comment");
        
    	List<Comment> newComments = Arrays.asList(newComment);
    	
    	List<Comment> result = this.submissionUtilityService.updateInternalComments(user1.getUserId(), this.sub.getSubmissionId(), newComments);
    	assertEquals(newComment.getComments(), result.get(0).getComments());
    	assertNotNull(result.get(0).getCommentId());
    }
    
    @Test
    public void testUpdateInternalCommentsUpdate() {
        this.comment1.setComments("new comments for unit test");
        
    	List<Comment> newComments = Arrays.asList(comment1, comment2).stream().map(c -> new Comment(c)).collect(Collectors.toList());
    	
    	List<Comment> result = this.submissionUtilityService.updateInternalComments(user1.getUserId(), this.sub.getSubmissionId(), newComments);
    	Comment saved = result.stream().filter(c -> c.getCommentId().equals(comment1.getCommentId())).findAny().orElse(null);
    	assertEquals("new comments for unit test", saved.getComments());
    }

    @Test
    public void testGetWorkingEvaluation() throws Exception{
        when(repo.getEvaluationByEvaluaatorAndSubmission(anyString(), anyString(), any(UUID.class))).thenReturn(Arrays.asList(evalModel1));

        EvaluationModel testModel = submissionUtilityService.getWorkingEvaluation(evalModel1.getEvaluatorId(), evalModel1.getEvaluationId());
        assertEquals(evalModel1, testModel);
    }

    @Test
    public void testGetWorkingEvaluationNone() throws Exception{
        when(repo.getEvaluationByEvaluaatorAndSubmission(anyString(), anyString(), any(UUID.class))).thenReturn(Collections.emptyList());

        thrown.expect(WorkingEvaluationException.class);
        submissionUtilityService.getWorkingEvaluation(evalModel1.getEvaluatorId(), evalModel1.getEvaluationId());
    }

    @Test
    public void testGetWorkingEvaluationTwo() throws Exception{
        when(repo.getEvaluationByEvaluaatorAndSubmission(anyString(), anyString(), any(UUID.class))).thenReturn(Arrays.asList(evalModel1, evalModel2));

        thrown.expect(WorkingEvaluationException.class);
        submissionUtilityService.getWorkingEvaluation(evalModel1.getEvaluatorId(), evalModel1.getEvaluationId());
    }

    @Test
    public void testReleaseEvaluationAcademicActivityPassing() throws Exception {
        SubmissionByIdModel byId = TestObjectFactory.getSubmissionByIdModel(StatusUtil.EVALUATION_RELEASED);
        EvaluationByIdModel evaluationByIdModel = TestObjectFactory.getEvaluationByIdModelPassing();
        evaluationByIdModel.setStatus(StatusUtil.WORKING);

        Comment comment = new Comment("this is a comment", CommentTypes.INTERNAL);

        Set<String> permissions = new HashSet<>();
        permissions.add(Permissions.SYSTEM);

        UserByIdModel user = TestObjectFactory.getUserModel();

        doNothing().when(publishAcademicActivityService).publishAcademicActivity(any(SubmissionModel.class), anyString());
        doNothing().when(repo).saveSubmission(any(SubmissionByIdModel.class), any(EvaluationByIdModel.class), anyString(), anyString(), anyBoolean());

        when(repo.getUserQualifications(anyString())).thenReturn(Optional.of(user));
        when(repo.getEvaluationById(any(UUID.class))).thenReturn(Optional.of(evaluationByIdModel));
        when(repo.getSubmissionStatus(any(UUID.class))).thenReturn(Optional.of(byId));

        submissionUtilityService.releaseEvaluation("1234567890", byId, true, comment);

        verify(publishAcademicActivityService, times(1)).publishAcademicActivity(any(SubmissionModel.class), eq(titleValue + passedValue));
    }

    @Test
    public void testReleaseEvaluationAcademicActivityFailing() throws Exception {
        SubmissionByIdModel idModel = TestObjectFactory.getSubmissionByIdModel(StatusUtil.AUTHOR_WORK_NEEDS_REVISION);
        EvaluationByIdModel evaluationByIdModel = TestObjectFactory.getEvaluationByIdModelFailing();
        evaluationByIdModel.setStatus(StatusUtil.WORKING);

        Comment comment = new Comment("this is a comment", CommentTypes.INTERNAL);

        Set<String> permissions = new HashSet<>();
        permissions.add(Permissions.SYSTEM);

        UserByIdModel user = TestObjectFactory.getUserModel();

        doNothing().when(publishAcademicActivityService).publishAcademicActivity(any(SubmissionModel.class), anyString());
        doNothing().when(repo).saveSubmission(any(SubmissionByIdModel.class), any(EvaluationByIdModel.class), anyString(), anyString(), anyBoolean());

        when(repo.getUserQualifications(anyString())).thenReturn(Optional.of(user));
        when(repo.getEvaluationById(any(UUID.class))).thenReturn(Optional.of(evaluationByIdModel));
        when(repo.getSubmissionStatus(any(UUID.class))).thenReturn(Optional.of(idModel));

        submissionUtilityService.releaseEvaluation("1234567890", idModel, true, comment);

        verify(publishAcademicActivityService, times(1)).publishAcademicActivity(any(SubmissionModel.class), eq(titleValue + returnValue));
    }

    @Test
    public void releaseReviewEvaluation() throws Exception {

        String userId = "QA00012";
        EvaluationByIdModel evaluation = TestObjectFactory.getEvaluationByIdModel(false, false);

        Comment comment = new Comment();

        when(repo.getSubmissionById(any(UUID.class))).thenReturn(Optional.of(TestObjectFactory.getSubmissionByIdModel(StatusUtil.EVALUATION_EDITED)));
        when(repo.getUser(anyString())).thenReturn(Optional.of(TestObjectFactory.getUserModel()));

        doNothing().when(repo).saveEvaluation(any(EvaluationByIdModel.class));
        doNothing().when(repo).saveSubmission(any(SubmissionByIdModel.class), anyString(), anyString());

        when(repo.getSubmissionStatus(any(UUID.class))).thenReturn(Optional.of(TestObjectFactory.getSubmissionByIdModel(StatusUtil.EVALUATION_RELEASED)));

        String result = submissionUtilityService.releaseReviewEvaluation(userId, evaluation, false, comment);
        assertEquals(StatusUtil.EVALUATION_RELEASED, result);
    }
}
