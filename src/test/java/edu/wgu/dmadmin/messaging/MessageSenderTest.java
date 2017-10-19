package edu.wgu.dmadmin.messaging;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import edu.wgu.dreamcatcher.domain.model.AssessmentModel;
import edu.wgu.dreamcatcher.domain.model.TaskModel;

public class MessageSenderTest {
	
	MessageSender sender = new MessageSender();
	
	RabbitTemplate template = mock(RabbitTemplate.class);
	AssessmentModel model = new AssessmentModel();
	UUID assessmentId = UUID.randomUUID();
	String assessmentCode = "ASSMNT";
	String studentId = "student";
	
	TaskModel task1 = new TaskModel();
	TaskModel task2 = new TaskModel();
	List<TaskModel> tasks;
	
	@Before
	public void initialize() {
		this.sender.setRabbitTemplate(template);
		this.tasks = Arrays.asList(task1, task2);
		
		model.setAssessmentCode(this.assessmentCode);
		model.setAssessmentId(this.assessmentId.toString());
		model.setStudentId(this.studentId);
		model.setTasks(this.tasks);
	}
	
	@Test
	public void testSendUpdate() {
		this.sender.sendUpdate(this.model);
		
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<AssessmentModel> arg2 = ArgumentCaptor.forClass(AssessmentModel.class);
		
		verify(this.template).convertAndSend(arg1.capture(), arg2.capture());
		assertEquals(QueueNames.SEND_UPDATES_QUEUE, arg1.getValue());
		assertEquals(this.assessmentCode, arg2.getValue().getAssessmentCode());
	}
	
	@Test
	public void testSendError() {
		this.sender.sendError(this.model);
		
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<AssessmentModel> arg2 = ArgumentCaptor.forClass(AssessmentModel.class);
		
		verify(this.template).convertAndSend(arg1.capture(), arg2.capture());
		assertEquals(QueueNames.ERROR_QUEUE, arg1.getValue());
		assertEquals(this.assessmentCode, arg2.getValue().getAssessmentCode());
	}
}
