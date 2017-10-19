package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.publish.Rubric;
import edu.wgu.dmadmin.domain.publish.Task;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.model.publish.TaskByAssessmentModel;
import edu.wgu.dmadmin.model.publish.TaskByCourseModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;

public class PublicationServiceTest {
	PublicationService publicationService;
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);
	
	List<TaskByIdModel> taskModels;
	List<TaskByCourseModel> courseTasks = new ArrayList<TaskByCourseModel>();
	List<TaskByAssessmentModel> assessmentTasks = new ArrayList<TaskByAssessmentModel>();
	
	UUID taskId1 = UUID.randomUUID();
	UUID taskId2 = UUID.randomUUID();
	UUID taskId3 = UUID.randomUUID();
	UUID taskId4 = UUID.randomUUID();
	UUID assessmentId1 = UUID.randomUUID();
	UUID assessmentId2 = UUID.randomUUID();
	TaskByIdModel taskModel1;
	TaskByIdModel taskModel2;
	TaskByIdModel taskModel3;
	TaskByIdModel taskModel4;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		publicationService = new PublicationService();
		publicationService.setCassandraRepo(cassandraRepo);
		
		taskModel1 = TestObjectFactory.getTaskByIdModel(this.assessmentId1, this.taskId2, new Long(12345), 1, 2, "testing2");
		taskModel2 = TestObjectFactory.getTaskByIdModel(this.assessmentId2, this.taskId3, new Long(12345), 2, 1, "testing3");
		taskModel3 = TestObjectFactory.getTaskByIdModel(this.assessmentId1, this.taskId1, new Long(12345), 1, 1, "testing1");
		taskModel4 = TestObjectFactory.getTaskByIdModel(this.assessmentId2, this.taskId4, new Long(12345), 2, 2, "testing4");
		
		taskModels = Arrays.asList(taskModel1, taskModel2, taskModel3, taskModel4);
		taskModels.forEach(model -> {
			courseTasks.add(new TaskByCourseModel(model));
			assessmentTasks.add( new TaskByAssessmentModel(model));
		});
	}
	
	@Test
	public void testGetAllTasks() {
		when(cassandraRepo.getTasks()).thenReturn(courseTasks);
		List<Task> tasks = publicationService.getAllTasks();
		Mockito.verify(cassandraRepo).getTasks();
		
		assertEquals(4, tasks.size());
		assertEquals("testing1", tasks.get(0).getTaskName());
	}
	
	@Test
	public void testGetTasksForCourse() {
		when(cassandraRepo.getTasksByCourseId(new Long(123))).thenReturn(courseTasks);
		List<Task> tasks = publicationService.getTasksForCourse(new Long(123));
		Mockito.verify(cassandraRepo).getTasksByCourseId(new Long(123));
		
		assertEquals(4, tasks.size());
		assertEquals("testing1", tasks.get(0).getTaskName());
	}
	
	@Test
	public void testGetTasksForAssessment() {
		when(cassandraRepo.getTasksByAssessment(assessmentId2)).thenReturn(assessmentTasks.stream()
				.filter(task -> task.getAssessmentId().equals(assessmentId2))
				.collect(Collectors.toList()));
		List<Task> tasks = publicationService.getTasksForAssessment(assessmentId2);
		Mockito.verify(cassandraRepo).getTasksByAssessment(assessmentId2);
		
		assertEquals(2, tasks.size());
		assertEquals("testing3", tasks.get(0).getTaskName());
	}
	
	@Test
	public void testGetTask() {		
		when(cassandraRepo.getTaskById(taskId4)).thenReturn(Optional.of(taskModel4));
		Task task = publicationService.getTask(taskId4);
		Mockito.verify(cassandraRepo).getTaskById(taskId4);
		
		assertEquals("testing4", task.getTaskName());
	}
	
	@Test
	public void testGetTaskFails() {
		when(cassandraRepo.getTaskById(taskId2)).thenReturn(Optional.empty());
		thrown.expect(TaskNotFoundException.class);
		publicationService.getTask(taskId2);
	}
	
	@Test
	public void testAddTask() {
		Task task = new Task();
		task.setTaskName("testing");
		task.setRubric(new Rubric());
		
		ArgumentCaptor<TaskByCourseModel> argument = ArgumentCaptor.forClass(TaskByCourseModel.class);
		publicationService.addTask(task);
		Mockito.verify(cassandraRepo).saveTask(argument.capture());
		assertEquals("testing", argument.getValue().getTaskName());	
	}
	
	@Test
	public void testDeleteTask() {
		ArgumentCaptor<UUID> argument1 = ArgumentCaptor.forClass(UUID.class);
		publicationService.deleteTask(taskId1);
		Mockito.verify(cassandraRepo).deleteTask(argument1.capture());
		assertEquals(taskId1, argument1.getValue());	
	}
}
