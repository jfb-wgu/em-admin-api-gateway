package edu.wgu.dmadmin.service.evaluation;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluatorNotQualifiedException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.WorkingEvaluationException;
import edu.wgu.dmadmin.factory.SubmissionLockFactory;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionLockModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.service.EvaluatorService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

@SuppressWarnings("boxing")
public class ImportEvaluationTest {
	EvaluatorService evaluatorService;
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);
	SubmissionLockFactory factory = mock(SubmissionLockFactory.class);
	UUID submissionId1 = UUID.randomUUID();
	UUID submissionId2 = UUID.randomUUID();
	UUID evaluationId = UUID.randomUUID();
	UUID taskId = UUID.randomUUID();
	UUID commentId = UUID.randomUUID();
	SubmissionByStudentAndTaskModel submission1;
	Optional<SubmissionByIdModel> submission2;
	Optional<UserByIdModel> evaluator;
	Optional<EvaluationByIdModel> evaluation;
	TaskByIdModel task;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		evaluatorService = new EvaluatorService();
		evaluatorService.setCassandraRepo(cassandraRepo);
		evaluatorService.setSubmissionLockFactory(factory);
		SubmissionUtilityService suService = new SubmissionUtilityService();
		suService.setCassandraRepo(cassandraRepo);
		evaluatorService.setSubmissionUtilityService(suService);
		
		UserByIdModel user = new UserByIdModel();
		user.setUserId("123");
		user.getPermissions().add(Permissions.TASK_QUEUE);
		user.getTasks().add(taskId);
		evaluator = Optional.of(user);
		
		CommentModel comment = new CommentModel();
		comment.setCommentId(commentId);
		comment.setAttempt(1);
		comment.setComments("first comment");
		comment.setUserId("123");
		
		ScoreModel score1 = new ScoreModel();
		score1.setName("first");
		score1.setPassingScore(2);
		score1.setAssignedScore(2);
		score1.getComments().put(comment.getCommentId(), comment);
		
		ScoreModel score2 = new ScoreModel();
		score2.setName("second");
		score2.setPassingScore(2);
		score2.setAssignedScore(1);
		
		ScoreReportModel scoreReport = new ScoreReportModel();
		scoreReport.setName("test report 1");
		scoreReport.getScores().put(score1.getName(), score1);
		scoreReport.getScores().put(score2.getName(), score2);
		
		EvaluationByIdModel eval = new EvaluationByIdModel();
		eval.setStatus(StatusUtil.COMPLETED);
		eval.setScoreReport(scoreReport);
		eval.setEvaluatorId("123");
		eval.setEvaluationId(evaluationId);

		submission1 = new SubmissionByStudentAndTaskModel();
		submission1.setSubmissionId(submissionId1);
		submission1.setStudentId("321");
		submission1.setCourseCode("course");
		submission1.setAssessmentCode("assessment");
		submission1.setTaskId(taskId);
		submission1.setAttempt(1);
		submission1.setStatus(StatusUtil.AUTHOR_WORK_NEEDS_REVISION);
		submission1.setEvaluatorId("321");
		submission1.setEvaluationId(evaluationId);
		
		SubmissionByIdModel sub2 = new SubmissionByIdModel();
		sub2.setSubmissionId(submissionId2);
		sub2.setStudentId("321");
		sub2.setCourseCode("course");
		sub2.setAssessmentCode("assessment");
		sub2.setTaskId(taskId);
		sub2.setAttempt(2);
		sub2.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		sub2.setPreviousEvaluationId(evaluationId);
		submission2 = Optional.of(sub2);
		
		when(cassandraRepo.getUserQualifications("123")).thenReturn(evaluator);
		when(cassandraRepo.getSubmissionById(submissionId2)).thenReturn(submission2);
		when(cassandraRepo.getEvaluationsBySubmission(submissionId2)).thenReturn(Collections.emptyList());
		when(cassandraRepo.getEvaluationById(evaluationId)).thenReturn(Optional.of(eval));
	}
	
	@Test
	public void testImportComments() throws EvaluatorNotQualifiedException, WorkingEvaluationException, EvaluationNotFoundException, SubmissionStatusException {
		SubmissionLockModel lock = new SubmissionLockModel(submissionId2, "123", DateUtil.getZonedNow(), UUID.randomUUID());
		when(factory.getSubmissionLock(submissionId2, "123")).thenReturn(lock);
		when(cassandraRepo.getSubmissionLocks(submissionId2)).thenReturn(Arrays.asList(lock));
		
		evaluatorService.claimSubmission(evaluator.get().getUserId(), submissionId2);
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		ScoreModel score1 = arg1.getValue().getScoreReport().getScores().get("first");
		assertEquals(1, score1.getComments().size());
		assertEquals("first comment", score1.getComments().get(commentId).getComments());
		
		ScoreModel score2 = arg1.getValue().getScoreReport().getScores().get("second");
		assertEquals(0, score2.getComments().size());
	}
	
	@Test
	public void testImportScores() throws EvaluatorNotQualifiedException, WorkingEvaluationException, EvaluationNotFoundException, SubmissionStatusException {
		SubmissionLockModel lock = new SubmissionLockModel(submissionId2, "123", DateUtil.getZonedNow(), UUID.randomUUID());
		when(factory.getSubmissionLock(submissionId2, "123")).thenReturn(lock);
		when(cassandraRepo.getSubmissionLocks(submissionId2)).thenReturn(Arrays.asList(lock));
		
		evaluatorService.claimSubmission(evaluator.get().getUserId(), submission2.get().getSubmissionId());
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		ScoreModel score1 = arg1.getValue().getScoreReport().getScores().get("first");
		assertEquals(2, score1.getPreviousScore());
		
		ScoreModel score2 = arg1.getValue().getScoreReport().getScores().get("second");
		assertEquals(-1, score2.getPreviousScore());
	}
}
