package edu.wgu.dmadmin.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import edu.wgu.dreamcatcher.client.DreamCatcherClient;
import edu.wgu.dreamcatcher.domain.model.AssessmentModel;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.config.ApplicationContextHolder;
import edu.wgu.dmadmin.messaging.MessageSender;
import edu.wgu.dmadmin.model.publish.TaskByAssessmentModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;

@SuppressWarnings("boxing")
public class SubmissionUpdateServiceTest {
	SubmissionUpdateService service = new SubmissionUpdateService();
	
	@Mock
	MessageSender messageSender;
	
	@Mock
	DreamCatcherClient arpIntegrationService;
	
	@Mock
	CassandraRepo repo;
	
	@Mock
	ApplicationContext context;
	
	TaskByAssessmentModel task1 = new TaskByAssessmentModel(TestObjectFactory.getTaskModel());
	TaskByAssessmentModel task2 = new TaskByAssessmentModel(TestObjectFactory.getTaskModel());
	List<TaskByAssessmentModel> basicTasks = Arrays.asList(task1, task2);
	AssessmentModel model;
	
	String assessmentCode = "ASSMNT";
	UUID assessmentId = UUID.randomUUID();
	UUID taskId = UUID.randomUUID();
	String taskName = "Task 1";
	String studentId = "student";
	String evaluatorId = "evaluator";
	Date dateUpdated = DateUtil.getZonedNow();
	String oldStatus = "32";
	String newStatus = "64";
	UUID submissionId = UUID.randomUUID();
	
	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		this.service.setMessageSender(messageSender);
		this.service.setDreamCatcherClient(arpIntegrationService);
		ApplicationContextHolder holder = new ApplicationContextHolder();
		holder.setApplicationContext(context);
		
		when(this.context.getBean(CassandraRepo.class)).thenReturn(this.repo);
		when(this.repo.getCountSubmissionsByStudentAndAssessment(studentId, assessmentId)).thenReturn(0);
		when(this.repo.getBasicTasksByAssessment(assessmentId)).thenReturn(basicTasks);
		
		model = new AssessmentModel();
		model.setAssessmentCode(assessmentCode);
		model.setAssessmentId(assessmentId.toString());
		model.setStudentId(studentId);
	}
	
	@Test
	public void testNotify() {
		this.service.notify(assessmentCode, assessmentId, taskId, taskName, 
				studentId, evaluatorId, dateUpdated, oldStatus, newStatus, 
				submissionId);
		
		ArgumentCaptor<AssessmentModel> argument = ArgumentCaptor.forClass(AssessmentModel.class);
		verify(this.messageSender).sendUpdate(argument.capture());
		assertEquals(1, argument.getValue().getTasks().size());
		assertEquals(this.assessmentId.toString(), argument.getValue().getAssessmentId());
		assertEquals(this.submissionId.toString(), argument.getValue().getTasks().get(0).getSubmissionId());
	}
	
	@Test
	public void testNotifyStarted() {
		this.service.notify(assessmentCode, assessmentId, taskId, taskName, 
				studentId, evaluatorId, dateUpdated, "0", "-4", 
				submissionId);
		
		ArgumentCaptor<AssessmentModel> argument = ArgumentCaptor.forClass(AssessmentModel.class);
		verify(this.messageSender).sendUpdate(argument.capture());
		assertEquals(3, argument.getValue().getTasks().size());
		assertEquals(this.assessmentId.toString(), argument.getValue().getAssessmentId());
		
		List<String> taskIds = argument.getValue().getTasks().stream().map(t -> t.getTaskId()).collect(Collectors.toList());
		List<String> inputIds = Arrays.asList(task1.getTaskId().toString(), task2.getTaskId().toString(), taskId.toString());
		assertTrue(CollectionUtils.isEqualCollection(taskIds, inputIds));
	}
	
	@Test
	public void testNotifyStartedPrevious() {
		when(this.repo.getCountSubmissionsByStudentAndAssessment(anyString(), any(UUID.class))).thenReturn(1);
		
		this.service.notify(assessmentCode, assessmentId, taskId, taskName, 
				studentId, evaluatorId, dateUpdated, "0", "-4", 
				submissionId);
		
		ArgumentCaptor<AssessmentModel> argument = ArgumentCaptor.forClass(AssessmentModel.class);
		verify(this.messageSender).sendUpdate(argument.capture());
		assertEquals(1, argument.getValue().getTasks().size());
		assertEquals(this.assessmentId.toString(), argument.getValue().getAssessmentId());
		
		verify(this.repo, never()).getBasicTasksByAssessment(any());
	}
	
	@Test
	public void testNotifyNoChange() {
		this.service.notify(assessmentCode, assessmentId, taskId, taskName, 
				studentId, evaluatorId, dateUpdated, "2", "2", 
				submissionId);
		
		verify(this.messageSender, never()).sendUpdate(any());
	}
	
	@Test
	public void testSendUpdates() {
		ArgumentCaptor<AssessmentModel> argument = ArgumentCaptor.forClass(AssessmentModel.class);
		
		this.service.sendUpdates(model);
		verify(this.arpIntegrationService).submit(argument.capture());
		assertEquals(assessmentCode, argument.getValue().getAssessmentCode());
	}
	
	@Test
	public void testRecover() {
		ArgumentCaptor<AssessmentModel> argument = ArgumentCaptor.forClass(AssessmentModel.class);
		
		this.service.recover(model);
		verify(this.messageSender).sendUpdate(argument.capture());
		assertEquals(assessmentCode, argument.getValue().getAssessmentCode());
	}
	
	@Test
	public void testOnArpUpdateRequest() {
		this.service.onArpUpdateRequest(model);
		
		ArgumentCaptor<AssessmentModel> argument = ArgumentCaptor.forClass(AssessmentModel.class);
		
		verify(this.arpIntegrationService).submit(argument.capture());
		assertEquals(assessmentCode, argument.getValue().getAssessmentCode());
	}
	
	@Test
	public void testOnArpUpdateRequestFails() {
		doThrow(Exception.class).when(this.arpIntegrationService).submit(model);
		this.service.onArpUpdateRequest(model);
		
		ArgumentCaptor<AssessmentModel> argument = ArgumentCaptor.forClass(AssessmentModel.class);
		
		verify(this.messageSender).sendError(argument.capture());
		assertEquals(assessmentCode, argument.getValue().getAssessmentCode());
	}
	
	@Test
	public void testOnErrorRequest() {
		this.service.onErrorRequest(model);
		
		ArgumentCaptor<AssessmentModel> argument = ArgumentCaptor.forClass(AssessmentModel.class);
		
		verify(this.arpIntegrationService).submit(argument.capture());
		assertEquals(assessmentCode, argument.getValue().getAssessmentCode());
	}
}
