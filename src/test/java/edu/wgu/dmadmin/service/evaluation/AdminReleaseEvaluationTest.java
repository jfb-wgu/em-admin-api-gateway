package edu.wgu.dmadmin.service.evaluation;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.IncompleteScoreReportException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.service.EvaluationAdminService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;

public class AdminReleaseEvaluationTest {
	EvaluationAdminService adminService;
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);
	SubmissionUtilityService submissionUtility = mock(SubmissionUtilityService.class);
	UUID taskId = UUID.randomUUID();
	UUID submissionId = UUID.randomUUID();
	Optional<SubmissionByIdModel> submission;
	Comment comment;
	String userId1 = "eval";
	String userId2 = "admin";
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		adminService = new EvaluationAdminService();
		adminService.setCassandraRepo(cassandraRepo);
		adminService.setSubmissionUtility(submissionUtility);
		
		comment = new Comment();
		comment.setCommentId(UUID.randomUUID());
		comment.setAttempt(1);
		comment.setUserId(userId1);
		comment.setComments("testing");
		comment.setType(CommentTypes.STUDENT);
		
		SubmissionByIdModel sub = new SubmissionByIdModel();
		sub.setSubmissionId(submissionId);
		sub.setEvaluatorId(userId2);
		sub.setAttempt(1);
		sub.setTaskId(taskId);
		submission = Optional.of(sub);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testReleaseSubmission() 
			throws EvaluationStatusException, IncompleteScoreReportException, SubmissionStatusException {
		submission.get().setEvaluationId(UUID.randomUUID());
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		
		adminService.releaseEvaluation(userId1, submissionId, true, comment);
		
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<Boolean> arg3 = ArgumentCaptor.forClass(Boolean.class);
		ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);
		
		verify(submissionUtility).releaseEvaluation(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
		assertEquals(userId1, arg1.getValue());
	}
	
	@Test
	public void testReleaseSubmissionMissing() 
			throws EvaluationStatusException, IncompleteScoreReportException, SubmissionStatusException {
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(Optional.empty());
		
		thrown.expect(SubmissionNotFoundException.class);
		adminService.releaseEvaluation(userId1, submissionId, true, comment);
	}
	
	@Test
	public void testReleaseSubmissionNoEval() 
			throws EvaluationStatusException, IncompleteScoreReportException, SubmissionStatusException {
		
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		submission.get().setEvaluatorId(null);
				
		thrown.expect(SubmissionStatusException.class);
		adminService.releaseEvaluation(userId1, submissionId, true, comment);
	}
}
