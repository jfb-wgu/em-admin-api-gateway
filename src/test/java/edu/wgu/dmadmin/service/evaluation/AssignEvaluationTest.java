package edu.wgu.dmadmin.service.evaluation;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluatorNotQualifiedException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.exception.WorkingEvaluationException;
import edu.wgu.dmadmin.factory.SubmissionLockFactory;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.AspectModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionLockModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.service.EvaluatorService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.StatusUtil;

@SuppressWarnings("boxing")
public class AssignEvaluationTest {
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);
	SubmissionLockFactory factory = mock(SubmissionLockFactory.class);
	SubmissionUtilityService suService = new SubmissionUtilityService();
	EvaluatorService evaluatorService;
	UUID submissionId = UUID.randomUUID();
	UUID taskId = UUID.randomUUID();
	UUID completeId = UUID.randomUUID();
	UUID cancelledId = UUID.randomUUID();
	Optional<SubmissionByIdModel> submission;
	Optional<EvaluationByIdModel> complete;
	EvaluationBySubmissionModel cancelled;
	Optional<UserByIdModel> evaluator;
	Optional<TaskByIdModel> task;
	RubricModel rubric;
	String evaluatorId = "123";
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		evaluatorService = new EvaluatorService();
		evaluatorService.setCassandraRepo(cassandraRepo);
		suService.setCassandraRepo(cassandraRepo);
		evaluatorService.setSubmissionLockFactory(factory);
		evaluatorService.setSubmissionUtilityService(suService);

		SubmissionByIdModel sub = new SubmissionByIdModel();
		sub.setSubmissionId(submissionId);
		sub.setTaskId(taskId);
		sub.setPreviousEvaluationId(completeId);
		submission = Optional.of(sub);
		
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
		
		UserByIdModel eval = new UserByIdModel();
		eval.setUserId(evaluatorId);
		eval.setFirstName("Test");
		eval.setLastName("User");
		eval.getPermissions().add(Permissions.TASK_QUEUE);
		eval.getTasks().add(taskId);
		evaluator = Optional.of(eval);
		
		ScoreModel score1 = new ScoreModel();
		score1.setName("first");
		score1.setPassingScore(2);
		score1.setAssignedScore(2);
		
		ScoreModel score2 = new ScoreModel();
		score2.setName("second");
		score2.setPassingScore(2);
		score2.setAssignedScore(1);
		
		ScoreReportModel scoreReport = new ScoreReportModel();
		scoreReport.setName("test report 1");
		scoreReport.getScores().put(score1.getName(), score1);
		scoreReport.getScores().put(score2.getName(), score2);
		
		EvaluationByIdModel comp = new EvaluationByIdModel();
		comp.setStatus(StatusUtil.COMPLETED);
		comp.setScoreReport(scoreReport);
		comp.setEvaluatorId(evaluatorId);
		comp.setEvaluationId(completeId);
		complete = Optional.of(comp);
		
		cancelled = new EvaluationBySubmissionModel();
		cancelled.setStatus(StatusUtil.CANCELLED);
		cancelled.setScoreReport(scoreReport);
		cancelled.setEvaluatorId(evaluatorId);
		cancelled.setEvaluationId(cancelledId);
		cancelled.setSubmissionId(submissionId);
		
		SubmissionLockModel lock = new SubmissionLockModel(submissionId, "123", UUID.randomUUID());
		when(factory.getSubmissionLock(submissionId, "123")).thenReturn(lock);
		when(cassandraRepo.getSubmissionLocks(submissionId)).thenReturn(Arrays.asList(lock));
	}
	
	@Test
	public void testClaimSubmission() throws EvaluatorNotQualifiedException, WorkingEvaluationException, EvaluationNotFoundException, SubmissionStatusException {
		
		submission.get().setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
		submission.get().setAttempt(1);
		submission.get().setPreviousEvaluationId(null);
		
		when(cassandraRepo.getEvaluationsBySubmission(submissionId)).thenReturn(Collections.emptyList());
		when(cassandraRepo.getTaskRubric(submission.get().getTaskId())).thenReturn(task);
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		evaluatorService.claimSubmission(evaluator.get().getUserId(), submission.get().getSubmissionId());

		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(evaluatorId, arg2.getValue().getEvaluatorId());
		assertEquals(StatusUtil.EVALUATION_BEGUN, arg2.getValue().getStatus());
		assertEquals(evaluator.get().getFirstName(), arg2.getValue().getEvaluatorFirstName());
		assertEquals(evaluatorId, arg1.getValue().getEvaluatorId());
		assertEquals(submissionId, arg1.getValue().getSubmissionId());
		assertEquals(StatusUtil.WORKING, arg1.getValue().getStatus());
	}
	
	@Test
	public void testClaimSubmissionNoTask() throws EvaluatorNotQualifiedException, WorkingEvaluationException, EvaluationNotFoundException, SubmissionStatusException {
		
		submission.get().setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
		submission.get().setAttempt(1);
		submission.get().setPreviousEvaluationId(null);
		
		when(cassandraRepo.getEvaluation(evaluatorId, submissionId)).thenReturn(Collections.emptyList());
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getTaskRubric(submission.get().getTaskId())).thenReturn(Optional.empty());
		
		thrown.expect(TaskNotFoundException.class);
		evaluatorService.claimSubmission(evaluator.get().getUserId(), submission.get().getSubmissionId());
	}

	@Test
	public void testClaimResubmission() throws EvaluatorNotQualifiedException, WorkingEvaluationException, EvaluationNotFoundException, SubmissionStatusException {
		
		submission.get().setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		submission.get().setAttempt(2);
		
		when(cassandraRepo.getEvaluationsBySubmission(submissionId)).thenReturn(Collections.emptyList());
		when(cassandraRepo.getEvaluationById(submission.get().getPreviousEvaluationId())).thenReturn(complete);
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		evaluatorService.claimSubmission(evaluator.get().getUserId(), submission.get().getSubmissionId());

		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(evaluatorId, arg2.getValue().getEvaluatorId());
		assertEquals(StatusUtil.EVALUATION_BEGUN, arg2.getValue().getStatus());
		assertEquals(evaluator.get().getFirstName(), arg2.getValue().getEvaluatorFirstName());
		assertEquals(evaluatorId, arg1.getValue().getEvaluatorId());
		assertEquals(submissionId, arg1.getValue().getSubmissionId());
		assertEquals(StatusUtil.WORKING, arg1.getValue().getStatus());
	}
	
	@Test
	public void testClaimResubmissionNoPrevious() throws EvaluatorNotQualifiedException, WorkingEvaluationException, EvaluationNotFoundException, SubmissionStatusException {
		
		submission.get().setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		submission.get().setAttempt(2);
		submission.get().setPreviousEvaluationId(UUID.randomUUID());
		
		when(cassandraRepo.getEvaluationsBySubmission(submissionId)).thenReturn(Collections.emptyList());
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		when(cassandraRepo.getEvaluationById(Matchers.any(UUID.class))).thenReturn(Optional.empty());
		
		thrown.expect(EvaluationNotFoundException.class);
		evaluatorService.claimSubmission(evaluator.get().getUserId(), submission.get().getSubmissionId());
	}
	
	@Test
	public void testClaimSubmissionResume() throws EvaluatorNotQualifiedException, WorkingEvaluationException, EvaluationNotFoundException, SubmissionStatusException {
		
		submission.get().setStatus(StatusUtil.EVALUATION_CANCELLED);
		submission.get().setPreviousEvaluationId(null);
		
		when(cassandraRepo.getEvaluationsBySubmission(submissionId)).thenReturn(Arrays.asList(cancelled));
		when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
		when(cassandraRepo.getUserQualifications(evaluatorId)).thenReturn(evaluator);
		
		ArgumentCaptor<EvaluationByIdModel> arg1 = ArgumentCaptor.forClass(EvaluationByIdModel.class);
		ArgumentCaptor<SubmissionByIdModel> arg2 = ArgumentCaptor.forClass(SubmissionByIdModel.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg4 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> arg5 = ArgumentCaptor.forClass(Boolean.class);
		
		evaluatorService.claimSubmission(evaluatorId, submissionId);

		Mockito.verify(cassandraRepo).saveSubmission(arg2.capture(), arg1.capture(), arg3.capture(), arg4.capture(), arg5.capture());
		assertEquals(evaluatorId, arg2.getValue().getEvaluatorId());
		assertEquals(StatusUtil.EVALUATION_BEGUN, arg2.getValue().getStatus());
		assertEquals(evaluator.get().getFirstName(), arg2.getValue().getEvaluatorFirstName());
		assertEquals(evaluatorId, arg1.getValue().getEvaluatorId());
		assertEquals(submissionId, arg1.getValue().getSubmissionId());
		assertEquals(StatusUtil.WORKING, arg1.getValue().getStatus());
	}
}
