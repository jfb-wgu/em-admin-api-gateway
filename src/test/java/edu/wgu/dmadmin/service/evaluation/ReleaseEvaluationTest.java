package edu.wgu.dmadmin.service.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.IncompleteScoreReportException;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.service.PublishAcademicActivityService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.StatusUtil;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("boxing")
public class ReleaseEvaluationTest {
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);

	SubmissionUtilityService submissionService;


	PublishAcademicActivityService publishAcademicActivityService;

	UUID submissionId = UUID.randomUUID();
	UUID completeId = UUID.randomUUID();
	UUID taskId = UUID.randomUUID();
	Optional<SubmissionByIdModel> submission;
	Optional<EvaluationByIdModel> working;
	Optional<UserByIdModel> evaluator;
	Comment comment;
	String evaluatorId = "123";
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
        submissionService = new SubmissionUtilityService();
		submissionService.setCassandraRepo(cassandraRepo);
		publishAcademicActivityService = Mockito.mock(PublishAcademicActivityService.class);
		submissionService.setPublishAcademicActivityService(publishAcademicActivityService);

		SubmissionByIdModel sub = new SubmissionByIdModel();
		sub.setSubmissionId(submissionId);
		sub.setEvaluationId(completeId);
		sub.setEvaluatorId(evaluatorId);
		submission = Optional.of(sub);
		
		UserByIdModel user = new UserByIdModel();
		user.setUserId(evaluatorId);
		evaluator = Optional.of(user);
		
		ScoreModel score1 = new ScoreModel();
		score1.setName("first");
		score1.setPassingScore(2);
		
		ScoreModel score2 = new ScoreModel();
		score2.setName("second");
		score2.setPassingScore(2);
		
		ScoreReportModel scoreReport = new ScoreReportModel();
		scoreReport.setName("test report 1");
		scoreReport.getScores().put(score1.getName(), score1);
		scoreReport.getScores().put(score2.getName(), score2);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -3);
		
		EvaluationByIdModel work = new EvaluationByIdModel();
		work.setStatus(StatusUtil.WORKING);
		work.setScoreReport(scoreReport);
		work.setEvaluatorId(evaluatorId);
		work.setEvaluationId(completeId);
		work.setSubmissionId(submissionId);
		work.setDateStarted(calendar.getTime());
		working = Optional.of(work);
		
		comment = new Comment();
		comment.setComments("testing");
		comment.setType(CommentTypes.STUDENT);
		
		when(cassandraRepo.getSubmissionStatus(submission.get().getSubmissionId())).thenReturn(submission);
	}
	
	@Test
	public void testReleaseEvaluationPassed() throws IncompleteScoreReportException, EvaluationStatusException {

		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(working);
		
		working.get().getScoreReport().getScores().get("first").setAssignedScore(2);
		working.get().getScoreReport().getScores().get("second").setAssignedScore(2);

		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		submissionService.releaseEvaluation(evaluatorId, submission.get(), true, comment);

		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(StatusUtil.EVALUATION_RELEASED, arg2.getValue().getStatus());
		assertEquals(working.get().getDateCompleted(), arg2.getValue().getDateCompleted());
		assertTrue(arg1.getValue().getMinutesSpent() > 0);
		assertEquals(StatusUtil.COMPLETED, arg1.getValue().getStatus());
		assertEquals("testing", arg1.getValue().getScoreReport().getComments().values().iterator().next().getComments());
	}
	
	@Test
	public void testReleaseEvaluationMentor() throws IncompleteScoreReportException, EvaluationStatusException {

		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(working);
		doNothing().when(publishAcademicActivityService).publishAcademicActivity(any(SubmissionModel.class), anyString());
		
		working.get().getScoreReport().getScores().get("first").setAssignedScore(2);
		working.get().getScoreReport().getScores().get("second").setAssignedScore(1);

		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		submissionService.releaseEvaluation(evaluatorId, submission.get(), false, comment);

		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(StatusUtil.AUTHOR_WORK_EVALUATED, arg2.getValue().getStatus());
		assertEquals(working.get().getDateCompleted(), arg2.getValue().getDateCompleted());
		assertTrue(arg1.getValue().getMinutesSpent() > 0);
		assertEquals(StatusUtil.COMPLETED, arg1.getValue().getStatus());
	}
	
	@Test
	public void testReleaseEvaluationRevision() throws IncompleteScoreReportException, EvaluationStatusException {

		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(working);
		
		working.get().getScoreReport().getScores().get("first").setAssignedScore(2);
		working.get().getScoreReport().getScores().get("second").setAssignedScore(1);

		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		submissionService.releaseEvaluation(evaluatorId, submission.get(), true, comment);

		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(StatusUtil.AUTHOR_WORK_NEEDS_REVISION, arg2.getValue().getStatus());
		assertEquals(working.get().getDateCompleted(), arg2.getValue().getDateCompleted());
		assertTrue(arg1.getValue().getMinutesSpent() > 0);
		assertEquals(StatusUtil.COMPLETED, arg1.getValue().getStatus());
	}
	
	@Test
	public void testReleaseEvaluationIncomplete() throws IncompleteScoreReportException, EvaluationStatusException {

		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(working);

		thrown.expect(IncompleteScoreReportException.class);
		submissionService.releaseEvaluation(evaluatorId, submission.get(), true, comment);
	}
	
	@Test
	public void testReleaseEvaluationStatus() throws IncompleteScoreReportException, EvaluationStatusException {

		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(working);
		
		working.get().setStatus(StatusUtil.CANCELLED);

		thrown.expect(EvaluationStatusException.class);
		submissionService.releaseEvaluation(evaluatorId, submission.get(), true, comment);
	}
	
	@Test
	public void testReleaseEvaluationMissing() throws IncompleteScoreReportException, EvaluationStatusException {

		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenThrow(new EvaluationNotFoundException(submission.get().getEvaluationId()));
		
		thrown.expect(EvaluationNotFoundException.class);
		submissionService.releaseEvaluation(evaluatorId, submission.get(), true, comment);
	}
	
	@Test
	public void testReleaseEvaluationNoUser() throws IncompleteScoreReportException, EvaluationStatusException {

		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(working);
		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(Optional.empty());

		thrown.expect(UserNotFoundException.class);
		submissionService.releaseEvaluation(evaluatorId, submission.get(), true, comment);
	}
}
