package edu.wgu.dmadmin.service.evaluation;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.submission.DashboardSubmission;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.service.EvaluatorService;
import edu.wgu.dmadmin.util.StatusUtil;
import edu.wgu.dmadmin.model.submission.SubmissionModel;

import static org.junit.Assert.assertEquals;

public class EvaluatorQueueTest {
	
	EvaluatorService service = new EvaluatorService();
	
	UUID submissionID1;
	UUID submissionID2;
	UUID submissionID3;
	UUID submissionID4;
	UUID submissionID5;
	UUID submissionID6;
	UUID submissionID7;
	UUID submissionID8;
	UUID submissionID9;
	UUID submissionID10;
	UUID submissionID11;
	UUID submissionID12;
	UUID submissionID13;
	UUID submissionID14;
	UUID submissionID15;
	UUID submissionID16;
	UUID submissionID17;
	UUID submissionID18;
	UUID submissionID19;
	UUID submissionID20;
	UUID submissionID21;
	UUID submissionID22;
	UUID submissionID23;
	UUID submissionID24;
	UUID submissionID25;
	UUID submissionID26;
	UUID submissionID27;
	UUID submissionID28;
	UUID submissionID29;
	UUID submissionID30;
	
	Set<SubmissionModel> submissions = new HashSet<SubmissionModel>();
	
	UUID taskCode1 = UUID.randomUUID();
	UUID taskCode2 = UUID.randomUUID();
	UUID taskCode3 = UUID.randomUUID();
	
	@Before
	public void initialize() {
		Calendar twoDaysAgo = Calendar.getInstance();
		twoDaysAgo.add(Calendar.DATE, -2);
		Date submitted1 = twoDaysAgo.getTime();
		
		twoDaysAgo.add(Calendar.HOUR, 8);
		Date submitted2 = twoDaysAgo.getTime();
		
		twoDaysAgo.add(Calendar.HOUR, 8);
		Date submitted3 = twoDaysAgo.getTime();
		
		twoDaysAgo.add(Calendar.HOUR, 8);
		Date submitted4 = twoDaysAgo.getTime();

		twoDaysAgo.add(Calendar.HOUR, 8);
		Date submitted5 = twoDaysAgo.getTime();
		
		twoDaysAgo.add(Calendar.HOUR, 8);
		Date submitted6 = twoDaysAgo.getTime();
		
		twoDaysAgo.add(Calendar.HOUR, 8);
		Date submitted7 = twoDaysAgo.getTime();
		
		submissionID1 = UUID.randomUUID();
		submissionID2 = UUID.randomUUID();
		submissionID3 = UUID.randomUUID();
		submissionID4 = UUID.randomUUID();
		submissionID5 = UUID.randomUUID();
		submissionID6 = UUID.randomUUID();
		submissionID7 = UUID.randomUUID();
		submissionID8 = UUID.randomUUID();
		submissionID9 = UUID.randomUUID();
		submissionID10 = UUID.randomUUID();
		submissionID11 = UUID.randomUUID();
		submissionID12 = UUID.randomUUID();
		submissionID13 = UUID.randomUUID();
		submissionID14 = UUID.randomUUID();
		submissionID15 = UUID.randomUUID();
		submissionID16 = UUID.randomUUID();
		submissionID17 = UUID.randomUUID();
		submissionID18 = UUID.randomUUID();
		submissionID19 = UUID.randomUUID();
		submissionID20 = UUID.randomUUID();
		submissionID21 = UUID.randomUUID();
		submissionID22 = UUID.randomUUID();
		submissionID23 = UUID.randomUUID();
		submissionID24 = UUID.randomUUID();
		submissionID25 = UUID.randomUUID();
		submissionID26 = UUID.randomUUID();
		submissionID27 = UUID.randomUUID();
		submissionID28 = UUID.randomUUID();
		submissionID29 = UUID.randomUUID();
		submissionID30 = UUID.randomUUID();

		SubmissionModel submission1 = new SubmissionModel();
		submission1.setSubmissionId(submissionID1);
		submission1.setTaskId(taskCode1);
		submission1.setDateSubmitted(submitted1);
		submission1.setStatus(StatusUtil.ARTICULATION_HOLD);
		
		SubmissionModel submission2 = new SubmissionModel();
		submission2.setSubmissionId(submissionID2);
		submission2.setTaskId(taskCode2);
		submission2.setDateSubmitted(submitted6);
		submission2.setStatus(StatusUtil.ARTICULATION_HOLD);
		
		SubmissionModel submission3 = new SubmissionModel();
		submission3.setSubmissionId(submissionID3);
		submission3.setTaskId(taskCode3);
		submission3.setDateSubmitted(submitted5);
		submission3.setStatus(StatusUtil.ARTICULATION_HOLD);
		
		SubmissionModel submission4 = new SubmissionModel();
		submission4.setSubmissionId(submissionID4);
		submission4.setTaskId(taskCode2);
		submission4.setDateSubmitted(submitted3);
		submission4.setStatus(StatusUtil.ARTICULATION_HOLD);
		
		SubmissionModel submission5 = new SubmissionModel();
		submission5.setSubmissionId(submissionID5);
		submission5.setTaskId(taskCode1);
		submission5.setDateSubmitted(submitted6);
		submission5.setStatus(StatusUtil.ORIGINALITY_HOLD);
		
		SubmissionModel submission6 = new SubmissionModel();
		submission6.setSubmissionId(submissionID6);
		submission6.setTaskId(taskCode2);
		submission6.setDateSubmitted(submitted3);
		submission6.setStatus(StatusUtil.ORIGINALITY_HOLD);

		SubmissionModel submission7 = new SubmissionModel();
		submission7.setSubmissionId(submissionID7);
		submission7.setTaskId(taskCode2);
		submission7.setDateSubmitted(submitted4);
		submission7.setStatus(StatusUtil.ORIGINALITY_HOLD);
		
		SubmissionModel submission8 = new SubmissionModel();
		submission8.setSubmissionId(submissionID8);
		submission8.setTaskId(taskCode3);
		submission8.setDateSubmitted(submitted2);
		submission8.setStatus(StatusUtil.ORIGINALITY_HOLD);
		
		SubmissionModel submission9 = new SubmissionModel();
		submission9.setSubmissionId(submissionID9);
		submission9.setTaskId(taskCode1);
		submission9.setDateSubmitted(submitted3);
		submission9.setStatus(StatusUtil.ORIGINALITY_HOLD);
		
		SubmissionModel submission10 = new SubmissionModel();
		submission10.setSubmissionId(submissionID10);
		submission10.setTaskId(taskCode1);
		submission10.setDateSubmitted(submitted2);
		submission10.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		
		SubmissionModel submission11 = new SubmissionModel();
		submission11.setSubmissionId(submissionID11);
		submission11.setTaskId(taskCode1);
		submission11.setDateSubmitted(submitted4);
		submission11.setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
		
		SubmissionModel submission12 = new SubmissionModel();
		submission12.setSubmissionId(submissionID12);
		submission12.setTaskId(taskCode1);
		submission12.setDateSubmitted(submitted1);
		submission12.setStatus(StatusUtil.LEAD_HOLD);
		
		SubmissionModel submission13 = new SubmissionModel();
		submission13.setSubmissionId(submissionID13);
		submission13.setTaskId(taskCode1);
		submission13.setDateSubmitted(submitted7);
		submission13.setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
		
		SubmissionModel submission14 = new SubmissionModel();
		submission14.setSubmissionId(submissionID14);
		submission14.setTaskId(taskCode1);
		submission14.setDateSubmitted(submitted5);
		submission14.setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
		
		SubmissionModel submission15 = new SubmissionModel();
		submission15.setSubmissionId(submissionID15);
		submission15.setTaskId(taskCode1);
		submission15.setDateSubmitted(submitted6);
		submission15.setStatus(StatusUtil.LEAD_HOLD);
		
		SubmissionModel submission16 = new SubmissionModel();
		submission16.setSubmissionId(submissionID16);
		submission16.setTaskId(taskCode1);
		submission16.setDateSubmitted(submitted3);
		submission16.setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
		
		SubmissionModel submission17 = new SubmissionModel();
		submission17.setSubmissionId(submissionID17);
		submission17.setTaskId(taskCode2);
		submission17.setDateSubmitted(submitted3);
		submission17.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		
		SubmissionModel submission18 = new SubmissionModel();
		submission18.setSubmissionId(submissionID18);
		submission18.setTaskId(taskCode2);
		submission18.setDateSubmitted(submitted5);
		submission18.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		
		SubmissionModel submission19 = new SubmissionModel();
		submission19.setSubmissionId(submissionID19);
		submission19.setTaskId(taskCode2);
		submission19.setDateSubmitted(submitted2);
		submission19.setStatus(StatusUtil.LEAD_HOLD);
		
		SubmissionModel submission20 = new SubmissionModel();
		submission20.setSubmissionId(submissionID20);
		submission20.setTaskId(taskCode2);
		submission20.setDateSubmitted(submitted7);
		submission20.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		
		SubmissionModel submission21 = new SubmissionModel();
		submission21.setSubmissionId(submissionID21);
		submission21.setTaskId(taskCode2);
		submission21.setDateSubmitted(submitted1);
		submission21.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		
		SubmissionModel submission22 = new SubmissionModel();
		submission22.setSubmissionId(submissionID22);
		submission22.setTaskId(taskCode2);
		submission22.setDateSubmitted(submitted4);
		submission22.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		
		SubmissionModel submission23 = new SubmissionModel();
		submission23.setSubmissionId(submissionID23);
		submission23.setTaskId(taskCode2);
		submission23.setDateSubmitted(submitted6);
		submission23.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);
		
		SubmissionModel submission24 = new SubmissionModel();
		submission24.setSubmissionId(submissionID24);
		submission24.setTaskId(taskCode3);
		submission24.setDateSubmitted(submitted5);
		submission24.setStatus(StatusUtil.LEAD_HOLD);
		
		SubmissionModel submission25 = new SubmissionModel();
		submission25.setSubmissionId(submissionID25);
		submission25.setTaskId(taskCode3);
		submission25.setDateSubmitted(submitted7);
		submission25.setStatus(StatusUtil.LEAD_HOLD);
		
		SubmissionModel submission26 = new SubmissionModel();
		submission26.setSubmissionId(submissionID26);
		submission26.setTaskId(taskCode3);
		submission26.setDateSubmitted(submitted1);
		submission26.setStatus(StatusUtil.LEAD_HOLD);
		
		SubmissionModel submission27 = new SubmissionModel();
		submission27.setSubmissionId(submissionID27);
		submission27.setTaskId(taskCode3);
		submission27.setDateSubmitted(submitted3);
		submission27.setStatus(StatusUtil.LEAD_HOLD);
		
		SubmissionModel submission28 = new SubmissionModel();
		submission28.setSubmissionId(submissionID28);
		submission28.setTaskId(taskCode3);
		submission28.setDateSubmitted(submitted4);
		submission28.setStatus(StatusUtil.LEAD_HOLD);

		SubmissionModel submission29 = new SubmissionModel();
		submission29.setSubmissionId(submissionID29);
		submission29.setTaskId(taskCode3);
		submission29.setDateSubmitted(submitted2);
		submission29.setStatus(StatusUtil.AUTHOR_WORK_RESUBMITTED);

		SubmissionModel submission30 = new SubmissionModel();
		submission30.setSubmissionId(submissionID30);
		submission30.setTaskId(taskCode3);
		submission30.setDateSubmitted(submitted6);
		submission30.setStatus(StatusUtil.LEAD_HOLD);

		submissions.add(submission1);
		submissions.add(submission2);
		submissions.add(submission3);
		submissions.add(submission4);
		submissions.add(submission5);
		submissions.add(submission6);		
		submissions.add(submission7);
		submissions.add(submission8);
		submissions.add(submission9);
		submissions.add(submission10);
		submissions.add(submission11);
		submissions.add(submission12);		
		submissions.add(submission13);
		submissions.add(submission14);
		submissions.add(submission15);
		submissions.add(submission16);
		submissions.add(submission17);
		submissions.add(submission18);		
		submissions.add(submission19);
		submissions.add(submission20);
		submissions.add(submission21);
		submissions.add(submission22);
		submissions.add(submission23);
		submissions.add(submission24);		
		submissions.add(submission25);
		submissions.add(submission26);
		submissions.add(submission27);
		submissions.add(submission28);
		submissions.add(submission29);		
		submissions.add(submission30);		
	}
	
	@Test
	public void testTask1Queue() {

		UserModel onTask1 = new UserModel();
		onTask1.getTasks().add(taskCode1);
		onTask1.getPermissions().add(Permissions.TASK_QUEUE);
		
		List<SubmissionModel> subs = submissions.stream().filter(s -> service.isQualified(onTask1, s.getStatus(), s.getTaskId())).collect(Collectors.toList());
		List<DashboardSubmission> queue = subs.stream().map(s -> new DashboardSubmission(s)).collect(Collectors.toList());
		Collections.sort(queue);
		assertEquals(5, queue.size());
		assertEquals(submissionID10, queue.get(0).getSubmissionId());
		assertEquals(submissionID16, queue.get(1).getSubmissionId());
		assertEquals(submissionID11, queue.get(2).getSubmissionId());
		assertEquals(submissionID14, queue.get(3).getSubmissionId());
		assertEquals(submissionID13, queue.get(4).getSubmissionId());
	}
	
	@Test
	public void testTask2Queue() {

		UserModel onTask2 = new UserModel();
		onTask2.getTasks().add(taskCode2);
		onTask2.getPermissions().add(Permissions.TASK_QUEUE);

		List<SubmissionModel> subs = submissions.stream().filter(s -> service.isQualified(onTask2, s.getStatus(), s.getTaskId())).collect(Collectors.toList());
		List<DashboardSubmission> queue = subs.stream().map(s -> new DashboardSubmission(s)).collect(Collectors.toList());
		Collections.sort(queue);
		assertEquals(6, queue.size());
		assertEquals(submissionID21, queue.get(0).getSubmissionId());
		assertEquals(submissionID17, queue.get(1).getSubmissionId());
		assertEquals(submissionID22, queue.get(2).getSubmissionId());
		assertEquals(submissionID18, queue.get(3).getSubmissionId());
		assertEquals(submissionID23, queue.get(4).getSubmissionId());
		assertEquals(submissionID20, queue.get(5).getSubmissionId());
	}
	
	@Test
	public void testTask2and3Queue() {

		UserModel onTask2and3 = new UserModel();
		onTask2and3.getTasks().add(taskCode2);
		onTask2and3.getTasks().add(taskCode3);
		onTask2and3.getPermissions().add(Permissions.TASK_QUEUE);

		List<SubmissionModel> subs = submissions.stream().filter(s -> service.isQualified(onTask2and3, s.getStatus(), s.getTaskId())).collect(Collectors.toList());
		List<DashboardSubmission> queue = subs.stream().map(s -> new DashboardSubmission(s)).collect(Collectors.toList());
		Collections.sort(queue);
		assertEquals(7, queue.size());
		assertEquals(submissionID21, queue.get(0).getSubmissionId());
		assertEquals(submissionID29, queue.get(1).getSubmissionId());
		assertEquals(submissionID17, queue.get(2).getSubmissionId());
		assertEquals(submissionID22, queue.get(3).getSubmissionId());
		assertEquals(submissionID18, queue.get(4).getSubmissionId());
		assertEquals(submissionID23, queue.get(5).getSubmissionId());
		assertEquals(submissionID20, queue.get(6).getSubmissionId());
	}
	
	@Test
	public void testArticulationQueue() {
		
		UserModel onTask2and3 = new UserModel();
		onTask2and3.getTasks().add(taskCode2);
		onTask2and3.getTasks().add(taskCode3);
		onTask2and3.getPermissions().add(Permissions.ARTICULATION_QUEUE);

		List<SubmissionModel> queue = submissions.stream().filter(s -> service.isQualified(onTask2and3, s.getStatus(), s.getTaskId())).collect(Collectors.toList());
		assertEquals(3, queue.size());
	}
	
	@Test
	public void testOriginalityQueue() {

		UserModel onTask2and3 = new UserModel();
		onTask2and3.getTasks().add(taskCode2);
		onTask2and3.getTasks().add(taskCode3);
		onTask2and3.getPermissions().add(Permissions.ORIGINALITY_QUEUE);

		List<SubmissionModel> queue = submissions.stream().filter(s -> service.isQualified(onTask2and3, s.getStatus(), s.getTaskId())).collect(Collectors.toList());
		assertEquals(3, queue.size());
	}
	
	@Test
	public void testLeadQueue() {

		UserModel onTask1 = new UserModel();
		onTask1.getTasks().add(taskCode1);
		onTask1.getPermissions().add(Permissions.LEAD_QUEUE);

		List<SubmissionModel> queue = submissions.stream().filter(s -> service.isQualified(onTask1, s.getStatus(), s.getTaskId())).collect(Collectors.toList());
		assertEquals(2, queue.size());
	}
}
