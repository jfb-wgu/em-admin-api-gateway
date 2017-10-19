package edu.wgu.dmadmin.service.evaluation;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import edu.wgu.dmadmin.service.EvaluationAdminService;
import edu.wgu.dmadmin.service.EvaluatorService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.StatusUtil;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.exception.EvaluatorNotQualifiedException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.AspectModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;

@SuppressWarnings("boxing")
public class AdminAssignEvaluationTest {
	EvaluationAdminService adminService;
	EvaluatorService evaluatorService = mock(EvaluatorService.class);
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);
	SubmissionUtilityService submissionUtility = mock(SubmissionUtilityService.class);
	UUID taskId = UUID.randomUUID();
	UUID submissionId = UUID.randomUUID();
	UUID evaluationId = UUID.randomUUID();
	Optional<EvaluationByIdModel> evaluation;
	Optional<SubmissionByIdModel> submission;
	Optional<TaskByIdModel> task;
	RubricModel rubric;
	CommentModel comment;
	Optional<UserByIdModel> evaluator;
	Optional<UserByIdModel> admin;
	String userId1 = "eval";
	String userId2 = "admin";
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		adminService = new EvaluationAdminService();
		adminService.setCassandraRepo(cassandraRepo);
		adminService.setEvaluatorService(evaluatorService);
		adminService.setSubmissionUtility(submissionUtility);
		
		comment = new CommentModel();
		comment.setCommentId(UUID.randomUUID());
		comment.setAttempt(1);
		comment.setUserId(userId1);
		comment.setComments("testing");
		comment.setType(CommentTypes.STUDENT);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -3);
				
		EvaluationByIdModel eval = new EvaluationByIdModel();
		eval.setDateStarted(calendar.getTime());
		eval.setEvaluationId(evaluationId);
		eval.setSubmissionId(submissionId);
		eval.setStatus(StatusUtil.WORKING);
		eval.setEvaluatorId(userId1);
		evaluation = Optional.of(eval);

		SubmissionByIdModel sub = new SubmissionByIdModel();
		sub.setSubmissionId(submissionId);
		sub.setEvaluationId(evaluationId);
		sub.setAttempt(1);
		sub.setTaskId(taskId);
		submission = Optional.of(sub);
		
		UserByIdModel user = new UserByIdModel();
		user.setUserId(userId1);
		evaluator = Optional.of(user);
		
		UserByIdModel adminuser = new UserByIdModel();
		adminuser.setUserId(userId2);
		admin = Optional.of(adminuser);
		
		TaskByIdModel testtask = new TaskByIdModel();
		testtask.setAssessmentName("test");
		testtask.setTaskName("test");
		testtask.setTaskId(taskId);
		
		rubric = new RubricModel();
		rubric.setName("test");
		
		List<AspectModel> aspects = new ArrayList<AspectModel>();
		rubric.setAspects(aspects);

		testtask.setRubric(rubric);
		task = Optional.of(testtask);
	}

	@Test
	public void testAssignSubmission() 
			throws EvaluatorNotQualifiedException, SubmissionStatusException {
		
		when(cassandraRepo.getUsersById(Arrays.asList(userId2, userId1))).thenReturn(Arrays.asList(evaluator.get(), admin.get()));
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getTaskRubric(submission.get().getTaskId())).thenReturn(task);
		
		evaluator.get().getTasks().add(taskId);
		
		adminService.assignEvaluation(userId2, userId1, submissionId, new Comment(comment));

		ArgumentCaptor<SubmissionByIdModel> arg1 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<EvaluationByIdModel> arg2 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		verify(cassandraRepo).saveSubmission(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(comment.getComments(), arg1.getValue().getInternalCommentsNS().values().iterator().next().getComments());
		assertEquals(userId2, arg1.getValue().getInternalCommentsNS().values().iterator().next().getUserId());
	}
	
	@Test
	public void testAssignSubmissionExisting() 
			throws EvaluatorNotQualifiedException, SubmissionStatusException {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		
		EvaluationBySubmissionModel eval1 = new EvaluationBySubmissionModel();
		eval1.setDateStarted(calendar.getTime());
		eval1.setDateUpdated(calendar.getTime());
		eval1.setEvaluationId(UUID.randomUUID());
		eval1.setSubmissionId(submissionId);
		eval1.setStatus(StatusUtil.CANCELLED);
		eval1.setEvaluatorId("asdf");
		
		calendar.add(Calendar.DATE, 2);
		
		EvaluationBySubmissionModel eval2 = new EvaluationBySubmissionModel();
		eval2.setDateStarted(calendar.getTime());
		eval2.setDateUpdated(calendar.getTime());
		eval2.setEvaluationId(UUID.randomUUID());
		eval2.setSubmissionId(submissionId);
		eval2.setStatus(StatusUtil.WORKING);
		eval2.setEvaluatorId("frda");
		
		List<EvaluationBySubmissionModel> evaluations = new ArrayList<EvaluationBySubmissionModel>();
		evaluations.add(eval1);
		evaluations.add(eval2);
		
		when(cassandraRepo.getUsersById(Arrays.asList(userId2, userId1))).thenReturn(Arrays.asList(evaluator.get(), admin.get()));
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(evaluation);
		when(cassandraRepo.getEvaluationsBySubmission(submissionId)).thenReturn(evaluations);
		
		evaluator.get().getTasks().add(taskId);
		
		adminService.assignEvaluation(userId2, userId1, submissionId, new Comment(comment));
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		verify(cassandraRepo).saveEvaluation(arg1.capture());
		assertTrue(arg1.getAllValues().get(0).getMinutesSpent() > 0);
		assertEquals(StatusUtil.CANCELLED, arg1.getAllValues().get(0).getStatus());
		
		verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(comment.getComments(), arg2.getValue().getInternalCommentsNS().values().iterator().next().getComments());
		assertEquals(userId2, arg2.getValue().getInternalCommentsNS().values().iterator().next().getUserId());
		assertTrue(arg1.getAllValues().get(1).getMinutesSpent() == 0);
		assertEquals(StatusUtil.WORKING, arg1.getAllValues().get(1).getStatus());
	}

	@Test
	public void testAssignSubmissionExistingAllCancelled() 
			throws EvaluatorNotQualifiedException, SubmissionStatusException {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		
		EvaluationBySubmissionModel eval1 = new EvaluationBySubmissionModel();
		eval1.setDateStarted(calendar.getTime());
		eval1.setDateUpdated(calendar.getTime());
		eval1.setEvaluationId(UUID.randomUUID());
		eval1.setSubmissionId(submissionId);
		eval1.setStatus(StatusUtil.CANCELLED);
		eval1.setEvaluatorId("asdf");
		
		calendar.add(Calendar.DATE, 2);
		
		CommentModel scorecomment = new CommentModel();
		scorecomment.setCommentId(UUID.randomUUID());
		scorecomment.setAttempt(1);
		scorecomment.setComments("first comment");
		scorecomment.setUserId("123");
		
		ScoreModel score1 = new ScoreModel();
		score1.setName("first");
		score1.setPassingScore(2);
		score1.setAssignedScore(2);
		score1.getComments().put(scorecomment.getCommentId(), scorecomment);
		
		ScoreModel score2 = new ScoreModel();
		score2.setName("second");
		score2.setPassingScore(2);
		score2.setAssignedScore(1);
		
		ScoreReportModel scoreReport = new ScoreReportModel();
		scoreReport.setName("test report 1");
		scoreReport.getScores().put(score1.getName(), score1);
		scoreReport.getScores().put(score2.getName(), score2);
		
		EvaluationBySubmissionModel eval2 = new EvaluationBySubmissionModel();
		eval2.setDateStarted(calendar.getTime());
		eval2.setDateUpdated(calendar.getTime());
		eval2.setStatus(StatusUtil.CANCELLED);
		eval2.setScoreReport(scoreReport);
		eval2.setEvaluatorId("123");
		eval2.setEvaluationId(evaluationId);
		eval2.setScoreReport(scoreReport);

		calendar.add(Calendar.DATE, -4);
		
		EvaluationBySubmissionModel eval3 = new EvaluationBySubmissionModel();
		eval3.setDateStarted(calendar.getTime());
		eval3.setDateUpdated(calendar.getTime());
		eval3.setEvaluationId(UUID.randomUUID());
		eval3.setSubmissionId(submissionId);
		eval3.setStatus(StatusUtil.CANCELLED);
		eval3.setEvaluatorId("qwerty");
		
		List<EvaluationBySubmissionModel> evaluations = new ArrayList<EvaluationBySubmissionModel>();
		evaluations.add(eval1);
		evaluations.add(eval2);
		evaluations.add(eval3);
		
		when(cassandraRepo.getUsersById(Arrays.asList(userId2, userId1))).thenReturn(Arrays.asList(evaluator.get(), admin.get()));
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(evaluation);
		when(cassandraRepo.getEvaluationsBySubmission(submissionId)).thenReturn(evaluations);
		
		evaluator.get().getTasks().add(taskId);
		
		adminService.assignEvaluation(userId2, userId1, submissionId, new Comment(comment));
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(comment.getComments(), arg2.getValue().getInternalCommentsNS().values().iterator().next().getComments());
		assertEquals(userId2, arg2.getValue().getInternalCommentsNS().values().iterator().next().getUserId());
		assertTrue(arg1.getAllValues().get(0).getMinutesSpent() == 0);
		assertEquals(StatusUtil.WORKING, arg1.getAllValues().get(0).getStatus());
		assertEquals(2, arg1.getAllValues().get(0).getScoreReport().getScores().size());
		assertTrue(eval1.getDateStarted().before(eval2.getDateStarted()));
		assertTrue(eval3.getDateStarted().before(eval2.getDateStarted()));
		assertTrue(arg1.getAllValues().get(0).getScoreReport().getScores().get("first").getComments().containsKey(scorecomment.getCommentId()));
		assertEquals(2, arg1.getAllValues().get(0).getScoreReport().getScores().get("first").getComments().size());
	}
	
	@Test
	public void testAssignSubmissionExistingOneCancelled() 
			throws EvaluatorNotQualifiedException, SubmissionStatusException {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		
		CommentModel scorecomment = new CommentModel();
		scorecomment.setCommentId(UUID.randomUUID());
		scorecomment.setAttempt(1);
		scorecomment.setComments("first comment");
		scorecomment.setUserId("123");
		
		ScoreModel score1 = new ScoreModel();
		score1.setName("first");
		score1.setPassingScore(2);
		score1.setAssignedScore(2);
		score1.getComments().put(scorecomment.getCommentId(), scorecomment);
		
		ScoreModel score2 = new ScoreModel();
		score2.setName("second");
		score2.setPassingScore(2);
		score2.setAssignedScore(1);
		
		ScoreReportModel scoreReport = new ScoreReportModel();
		scoreReport.setName("test report 1");
		scoreReport.getScores().put(score1.getName(), score1);
		scoreReport.getScores().put(score2.getName(), score2);
		
		EvaluationBySubmissionModel eval2 = new EvaluationBySubmissionModel();
		eval2.setDateStarted(calendar.getTime());
		eval2.setStatus(StatusUtil.CANCELLED);
		eval2.setScoreReport(scoreReport);
		eval2.setEvaluatorId("123");
		eval2.setEvaluationId(evaluationId);
		eval2.setScoreReport(scoreReport);
		
		List<EvaluationBySubmissionModel> evaluations = new ArrayList<EvaluationBySubmissionModel>();
		evaluations.add(eval2);
		
		when(cassandraRepo.getUsersById(Arrays.asList(userId2, userId1))).thenReturn(Arrays.asList(evaluator.get(), admin.get()));
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(evaluation);
		when(cassandraRepo.getEvaluationsBySubmission(submissionId)).thenReturn(evaluations);
		
		evaluator.get().getTasks().add(taskId);
		
		adminService.assignEvaluation(userId2, userId1, submissionId, new Comment(comment));
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);

		verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(comment.getComments(), arg2.getValue().getInternalCommentsNS().values().iterator().next().getComments());
		assertEquals(userId2, arg2.getValue().getInternalCommentsNS().values().iterator().next().getUserId());
		assertTrue(arg1.getAllValues().get(0).getMinutesSpent() == 0);
		assertEquals(StatusUtil.WORKING, arg1.getAllValues().get(0).getStatus());
		assertEquals(2, arg1.getAllValues().get(0).getScoreReport().getScores().size());
	}
	
	@Test
	public void testAssignSubmissionExistingByUser() 
			throws EvaluatorNotQualifiedException, SubmissionStatusException {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		
		EvaluationBySubmissionModel eval1 = new EvaluationBySubmissionModel();
		eval1.setDateStarted(calendar.getTime());
		eval1.setDateUpdated(calendar.getTime());
		eval1.setEvaluationId(UUID.randomUUID());
		eval1.setSubmissionId(submissionId);
		eval1.setStatus(StatusUtil.CANCELLED);
		eval1.setEvaluatorId(userId1);
		
		calendar.add(Calendar.DATE, 2);
		
		EvaluationBySubmissionModel eval2 = new EvaluationBySubmissionModel();
		eval2.setDateStarted(calendar.getTime());
		eval2.setDateUpdated(calendar.getTime());
		eval2.setEvaluationId(UUID.randomUUID());
		eval2.setSubmissionId(submissionId);
		eval2.setStatus(StatusUtil.CANCELLED);
		eval2.setEvaluatorId("frda");
		
		List<EvaluationBySubmissionModel> evaluations = new ArrayList<EvaluationBySubmissionModel>();
		evaluations.add(eval1);
		evaluations.add(eval2);
		
		when(cassandraRepo.getUsersById(Arrays.asList(userId2, userId1))).thenReturn(Arrays.asList(evaluator.get(), admin.get()));
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(evaluation);
		when(cassandraRepo.getEvaluationsBySubmission(submissionId)).thenReturn(evaluations);
		
		evaluator.get().getTasks().add(taskId);
		
		adminService.assignEvaluation(userId2, userId1, submissionId, new Comment(comment));
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(comment.getComments(), arg2.getValue().getInternalCommentsNS().values().iterator().next().getComments());
		assertEquals(userId2, arg2.getValue().getInternalCommentsNS().values().iterator().next().getUserId());
		assertTrue(arg1.getAllValues().get(0).getMinutesSpent() == 0);
		assertEquals(StatusUtil.WORKING, arg1.getAllValues().get(0).getStatus());
		assertEquals(eval1.getEvaluationId(), arg1.getAllValues().get(0).getEvaluationId());
	}
	
	@Test
	public void testAssignSubmissionSame() 
			throws EvaluatorNotQualifiedException, SubmissionStatusException {
		
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getUsersById(Arrays.asList(userId1, userId2))).thenReturn(Arrays.asList(evaluator.get(), admin.get()));
		submission.get().setEvaluatorId(userId1);
		evaluator.get().getTasks().add(taskId);
		
		adminService.assignEvaluation(userId2, userId1, submissionId, new Comment(comment));
		
		verify(cassandraRepo, never()).getUser(userId1);
	}
}
