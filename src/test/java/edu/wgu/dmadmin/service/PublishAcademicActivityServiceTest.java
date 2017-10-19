package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.submission.AcademicActivitySubmission;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;

public class PublishAcademicActivityServiceTest {
	
	PublishAcademicActivityService service = new PublishAcademicActivityService();
	
	EventPublisher publisher = mock(EventPublisher.class);
	SubmissionModel submission = TestObjectFactory.getSubmissionModel(StatusUtil.AUTHOR_WORK_SUBMITTED);
	String type = "testing";
	UUID submissionId = UUID.randomUUID();
	
	@Before
	public void initialize() {
		this.service.setEventPublisher(publisher);
		this.submission.setSubmissionId(this.submissionId);
	}

	@Test
	public void testPublishAcademicActivity() {
		this.service.publishAcademicActivity(this.submission, this.type);
		
		ArgumentCaptor<AcademicActivitySubmission> arg1 = ArgumentCaptor.forClass(AcademicActivitySubmission.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		
		verify(this.publisher).publish(arg1.capture(), arg2.capture(), arg3.capture());
		assertEquals(this.submissionId.toString(), arg1.getValue().getSubmissionId());
		assertEquals(this.type, arg2.getValue());
		assertEquals("Dream Machine", arg3.getValue());
	}
	
	@Test
	public void testPublishAcademicActivity2() {
		this.service.publishAcademicActivity(new Submission(this.submission), this.type);
		
		ArgumentCaptor<AcademicActivitySubmission> arg1 = ArgumentCaptor.forClass(AcademicActivitySubmission.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
		
		verify(this.publisher).publish(arg1.capture(), arg2.capture(), arg3.capture());
		assertEquals(this.submissionId.toString(), arg1.getValue().getSubmissionId());
		assertEquals(this.type, arg2.getValue());
		assertEquals("Dream Machine", arg3.getValue());
	}
}
