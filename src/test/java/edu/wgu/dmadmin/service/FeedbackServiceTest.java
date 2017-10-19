package edu.wgu.dmadmin.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.wgu.dmadmin.domain.feedback.StudentFeedback;
import edu.wgu.dmadmin.model.feedback.StudentFeedbackModel;
import edu.wgu.dmadmin.repo.CassandraRepo;

public class FeedbackServiceTest {
	FeedbackService service = new FeedbackService();
	
	@Mock
	CassandraRepo repo;
	
	String studentId = "student";
	StudentFeedback feedback;
	
	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		this.service.setCassandraRepo(repo);
		
		this.feedback = new StudentFeedback();
		feedback.setAssessmentCode("ASSMNT");
		feedback.setAttempt(1);
		feedback.setComments("this is a test");
		feedback.setRating(2);
		feedback.setTaskId(UUID.randomUUID());
		
		when(this.repo.getFeedbackFromStudent(studentId)).thenReturn(Arrays.asList(new StudentFeedbackModel(feedback)));
	}
	
	@Test
	public void testSaveStudentFeedback() {
		this.service.saveStudentFeedback(studentId, feedback);
		
		ArgumentCaptor<StudentFeedbackModel> argument = ArgumentCaptor.forClass(StudentFeedbackModel.class);
		verify(this.repo).saveStudentFeedback(argument.capture());
		assertEquals(2, argument.getValue().getRating());
		assertEquals(feedback.getTaskId(), argument.getValue().getTaskId());
	}
	
	@Test
	public void testGetStudentFeedback() {
		List<StudentFeedback> result = this.service.getStudentFeedback(studentId);
		assertEquals("ASSMNT", result.get(0).getAssessmentCode());
	}
	
	@Test
	public void testGetStudentFeedbackNone() {
		when(this.repo.getFeedbackFromStudent(studentId)).thenReturn(Collections.emptyList());
		List<StudentFeedback> result = this.service.getStudentFeedback(studentId);
		assertEquals(0, result.size());
	}
	
	@Test
	public void testHasStudentFeedback() {
		boolean result = this.service.hasStudentFeedback(studentId);
		assertTrue(result);
	}
	
	@Test
	public void testHasStudentFeedbackNo() {
		when(this.repo.getFeedbackFromStudent(studentId)).thenReturn(Collections.emptyList());
		boolean result = this.service.hasStudentFeedback(studentId);
		assertFalse(result);
	}
}
