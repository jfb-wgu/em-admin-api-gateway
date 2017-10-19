package edu.wgu.dmadmin.service.evaluation;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.StatusUtil;

public class ClearEvaluationTest {
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);
	SubmissionUtilityService submissionService;
	UUID submissionId = UUID.randomUUID();
	UUID taskId = UUID.randomUUID();
	UUID workingId = UUID.randomUUID();
	Optional<SubmissionByIdModel> submission;
	Optional<UserByIdModel> evaluator;
	String evaluatorId = "123";
	Comment comment;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		submissionService = new SubmissionUtilityService();
		submissionService.setCassandraRepo(cassandraRepo);

		SubmissionByIdModel sub = new SubmissionByIdModel();
		sub.setSubmissionId(submissionId);
		sub.setStudentId("student");
		sub.setTaskId(taskId);
		sub.setPreviousEvaluationId(workingId);
		submission = Optional.of(sub);
				
		UserByIdModel eval = new UserByIdModel();
		eval.setUserId(evaluatorId);
		evaluator = Optional.of(eval);
		
		comment = new Comment();
		comment.setCommentId(UUID.randomUUID());
		comment.setAttempt(1);
		comment.setUserId(evaluatorId);
		comment.setComments("testing");
		comment.setType(CommentTypes.STUDENT);
	}
	
	@Test
	public void testClearSubmissionMentor() throws SubmissionStatusException {
		
		submission.get().setStatus(StatusUtil.AUTHOR_WORK_EVALUATED);
		evaluator.get().getPermissions().add(Permissions.ALL_CLEAR);
		
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getUser(evaluatorId)).thenReturn(evaluator);
		
		ArgumentCaptor<SubmissionByIdModel> arg1 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);

		submissionService.clearSubmissionHold(evaluatorId, submissionId, comment);

		Mockito.verify(cassandraRepo).saveSubmission(arg1.capture(), arg2.capture(), arg3.capture());
		assertEquals(StatusUtil.AUTHOR_WORK_NEEDS_REVISION, arg1.getValue().getStatus());
		assertEquals("testing", arg1.getValue().getInternalCommentsNS().values().iterator().next().getComments());
	}
	
	@Test
	public void testClearSubmissionLoop() throws SubmissionStatusException {
		
		Set<String> submissionStatuses = new HashSet<String>();
		submissionStatuses.add(StatusUtil.ARTICULATION_HOLD);
		submissionStatuses.add(StatusUtil.LEAD_HOLD);
		submissionStatuses.add(StatusUtil.OPEN_HOLD);
		submissionStatuses.add(StatusUtil.ORIGINALITY_HOLD);
		submissionStatuses.add(StatusUtil.AUTHOR_WORK_EVALUATED);
		
		Set<String> evaluatorPermissions = new HashSet<String>();
		evaluatorPermissions.add(Permissions.ALL_CLEAR);
		evaluatorPermissions.add(Permissions.ARTICULATION_CLEAR);
		evaluatorPermissions.add(Permissions.LEAD_CLEAR);
		evaluatorPermissions.add(Permissions.OPEN_CLEAR);
		evaluatorPermissions.add(Permissions.ORIGINALITY_CLEAR);
		evaluatorPermissions.add(Permissions.ATTEMPTS_CLEAR);
		
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getUser(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getLastStatusForSubmission(submission.get().getStudentId(), submission.get().getSubmissionId())).thenReturn(StatusUtil.EVALUATION_BEGUN);
		
		ArgumentCaptor<SubmissionByIdModel> arg1 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);

		for (String status : submissionStatuses) {
			for (String permission : evaluatorPermissions) {
				submission.get().setStatus(status);
				Set<String> permissions = new HashSet<String>();
				permissions.add(permission);
				evaluator.get().setPermissions(permissions);
				
				if (evaluator.get().getPermissions().contains(Permissions.ALL_CLEAR) || evaluator.get().getPermissions().contains(Permissions.getPermissionforStatus(status))) {
					submissionService.clearSubmissionHold(evaluatorId, submissionId, comment);
					Mockito.verify(cassandraRepo).saveSubmission(arg1.capture(), arg2.capture(), arg3.capture());
					assertEquals(StatusUtil.EVALUATION_BEGUN, arg1.getValue().getStatus());
					assertEquals("testing", arg1.getValue().getInternalCommentsNS().values().iterator().next().getComments());
				} else {
					thrown.expect(SubmissionStatusException.class);
					submissionService.clearSubmissionHold(evaluatorId, submissionId, comment);
				}
			}
		}
	}
}
