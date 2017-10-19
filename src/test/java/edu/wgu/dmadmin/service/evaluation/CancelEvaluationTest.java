package edu.wgu.dmadmin.service.evaluation;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.Calendar;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.StatusUtil;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;

@SuppressWarnings("boxing")
public class CancelEvaluationTest {
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);
	SubmissionUtilityService submissionService;
	UUID submissionId = UUID.randomUUID();
	UUID completeId = UUID.randomUUID();
	UUID taskId = UUID.randomUUID();
	Optional<SubmissionByIdModel> submission;
	Optional<EvaluationByIdModel> working;
	UserByIdModel evaluator;
	Comment comment;
	String evaluatorId = "123";
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		submissionService = new SubmissionUtilityService();
		submissionService.setCassandraRepo(cassandraRepo);

		SubmissionByIdModel sub = new SubmissionByIdModel();
		sub.setSubmissionId(submissionId);
		sub.setEvaluationId(completeId);
		sub.setEvaluatorId(evaluatorId);
		submission = Optional.of(sub);
		
		evaluator = new UserByIdModel();
		evaluator.setUserId(evaluatorId);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -3);
		
		EvaluationByIdModel work = new EvaluationByIdModel();
		work.setStatus(StatusUtil.WORKING);
		work.setEvaluatorId(evaluatorId);
		work.setEvaluationId(completeId);
		work.setSubmissionId(submissionId);
		work.setDateStarted(calendar.getTime());
		working = Optional.of(work);
		
		comment = new Comment();
		comment.setComments("testing");
		comment.setType(CommentTypes.STUDENT);
	}

	@Test
	public void testCancelEvaluation() throws EvaluationStatusException {

		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(working);
		when(cassandraRepo.getSubmissionStatus(submission.get().getSubmissionId())).thenReturn(submission);
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		submissionService.cancelEvaluation(evaluatorId, submission.get(), comment);

		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(StatusUtil.EVALUATION_CANCELLED, arg2.getValue().getStatus());
		assertEquals(null, arg2.getValue().getDateCompleted());
		assertEquals("testing", arg2.getValue().getInternalCommentsNS().values().iterator().next().getComments());
		assertTrue(arg1.getValue().getMinutesSpent() > 0);
		assertEquals(StatusUtil.CANCELLED, arg1.getValue().getStatus());
	}
	
	@Test
	public void testCancelEvaluationMissing() throws EvaluationStatusException {
		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenThrow(new EvaluationNotFoundException(submission.get().getEvaluationId()));
		
		thrown.expect(EvaluationNotFoundException.class);
		submissionService.cancelEvaluation(evaluatorId, submission.get(), comment);
	}
	
	@Test
	public void testCancelEvaluationStatus() throws EvaluationStatusException {

		when(cassandraRepo.getEvaluationById(submission.get().getEvaluationId())).thenReturn(working);
		working.get().setStatus(StatusUtil.COMPLETED);
		
		thrown.expect(EvaluationStatusException.class);
		submissionService.cancelEvaluation(evaluatorId, submission.get(), comment);
	}
}
