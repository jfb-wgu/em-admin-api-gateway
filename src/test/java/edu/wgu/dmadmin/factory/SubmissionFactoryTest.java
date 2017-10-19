package edu.wgu.dmadmin.factory;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

public class SubmissionFactoryTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	SubmissionFactory factory = new SubmissionFactory();
	
	String taskName = "Test Task";
	String studentId = "student";
	Long pidm = new Long(123456);
	
	TaskByIdModel task = TestObjectFactory.getTaskByIdModel(UUID.randomUUID(), UUID.randomUUID(), new Long(987654), 1, 1, taskName);
	
	SubmissionModel sub1 = TestObjectFactory.getSubmissionModel(StatusUtil.AUTHOR_WORK_NEEDS_REVISION);
	SubmissionModel sub2 = TestObjectFactory.getSubmissionModel(StatusUtil.AUTHOR_WORK_NEEDS_REVISION);
		
	@Before
	public void initialize() {
	}
	
	@Test
	public void testNewSubmission() throws SubmissionStatusException {
		SubmissionModel newSubmission = factory.getSubmission(this.studentId, this.task, Collections.emptyList(), this.pidm);
		assertEquals(1, newSubmission.getAttempt());
		assertEquals(StatusUtil.AUTHOR_SUBMISSION_STARTED, newSubmission.getStatus());
		assertEquals(this.taskName, newSubmission.getTaskName());
	}
	
	@Test
	public void testNewSubmissionResume() throws SubmissionStatusException {
		UUID submissionId = UUID.randomUUID();
		sub1.setSubmissionId(submissionId);
		sub1.setStatus(StatusUtil.SUBMISSION_CANCELLED);
		sub1.setDateEstimated(DateUtil.getZonedNow());
		List<SubmissionModel> previous = Arrays.asList(sub1);
		
		SubmissionModel newSubmission = factory.getSubmission(this.studentId, this.task, previous, this.pidm);
		assertEquals(1, newSubmission.getAttempt());
		assertEquals(StatusUtil.AUTHOR_SUBMISSION_STARTED, newSubmission.getStatus());
		assertEquals(submissionId, newSubmission.getSubmissionId());
		assertEquals(sub1.getAttempt(), newSubmission.getAttempt());
		assertEquals(null, newSubmission.getDateEstimated());
	}
	
	@Test
	public void testNewSubmissionResumeResub() throws SubmissionStatusException {
		UUID submissionId = UUID.randomUUID();
		sub1.setSubmissionId(submissionId);
		sub1.setAttempt(2);
		sub1.setStatus(StatusUtil.SUBMISSION_CANCELLED);
		sub1.setDateEstimated(DateUtil.getZonedNow());
		List<SubmissionModel> previous = Arrays.asList(sub1);
		
		SubmissionModel newSubmission = factory.getSubmission(this.studentId, this.task, previous, this.pidm);
		assertEquals(sub1.getAttempt(), newSubmission.getAttempt());
		assertEquals(StatusUtil.AUTHOR_RESUBMISSION_STARTED, newSubmission.getStatus());
		assertEquals(submissionId, newSubmission.getSubmissionId());
		assertEquals(sub1.getAttempt(), newSubmission.getAttempt());
		assertEquals(null, newSubmission.getDateEstimated());
	}
	
	@Test
	public void testNewSubmissionResub() throws SubmissionStatusException {
		List<SubmissionModel> previous = Arrays.asList(sub1);
		
		SubmissionModel newSubmission = factory.getSubmission(this.studentId, this.task, previous, this.pidm);
		assertEquals(sub1.getAttempt() + 1, newSubmission.getAttempt());
		assertEquals(StatusUtil.AUTHOR_RESUBMISSION_STARTED, newSubmission.getStatus());
		assertEquals(sub1.getAttempt() + 1, newSubmission.getAttempt());
	}
	
	@Test
	public void testNewSubmissionUnfinished() throws SubmissionStatusException {
		sub1.setStatus(StatusUtil.EVALUATION_BEGUN);
		List<SubmissionModel> previous = Arrays.asList(sub1);
		
		thrown.expect(SubmissionStatusException.class);
		factory.getSubmission(this.studentId, this.task, previous, this.pidm);
	}
	
	@Test
	public void testNewSubmissionThird() throws SubmissionStatusException {
		sub2.setAttempt(2);
		List<SubmissionModel> previous = Arrays.asList(sub1, sub2);
		
		SubmissionModel newSubmission = factory.getSubmission(this.studentId, this.task, previous, this.pidm);
		assertEquals(StatusUtil.AUTHOR_RESUBMISSION_STARTED, newSubmission.getStatus());
		assertEquals(sub2.getAttempt() + 1, newSubmission.getAttempt());
	}
}
