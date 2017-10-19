package edu.wgu.dmadmin.service;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.exception.AttachmentSizeException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.factory.SubmissionFactory;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.publish.AspectModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

@SuppressWarnings("boxing")
public class StudentWorkServiceTest {
    StudentWorkService studentWorkService;
    PublishAcademicActivityService publishAcademicActivityService;
    EmailService emailService;
    CassandraRepo cassandraRepo = mock(CassandraRepo.class);
    SubmissionFactory factory = new SubmissionFactory();
    
    String studentId;
    UUID submissionId;
    UUID taskId;
    String courseCode;
    String assessmentCode;
    
    Optional<SubmissionByIdModel> submission;
    Optional<TaskByIdModel> task;

    public ExpectedException thrown = ExpectedException.none();
    private String titleValue = "Test.Title";
    private String submitValue = "Test.Submit";

    @Before
    public void initialize() {
        studentWorkService = new StudentWorkService();
        studentWorkService.setCassandraRepo(cassandraRepo);
        publishAcademicActivityService = Mockito.mock(PublishAcademicActivityService.class);
        studentWorkService.setPublishAcademicActivityService(publishAcademicActivityService);
        emailService = Mockito.mock(EmailService.class);
        studentWorkService.setEmailService(emailService);
        studentWorkService.setSubmissionFactory(factory);
        submissionId = UUID.fromString("d250da63-a28c-4f8f-afc8-e0f3c1b7c671");
        studentId = "106679";
        taskId = UUID.randomUUID();
        courseCode = "ORA1";
        assessmentCode = "ABC";
        
        SubmissionByIdModel sub = new SubmissionByIdModel();
        sub.setSubmissionId(submissionId);
        sub.setStudentId(studentId);
        sub.setTaskId(taskId);
        sub.setCourseCode(courseCode);
        sub.setAssessmentCode(assessmentCode);
        sub.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);
        submission = Optional.of(sub);
        
        TaskByIdModel taskModel = new TaskByIdModel();
        taskModel.setAssessmentName("test");
        taskModel.setTaskName("test");
        
		
		RubricModel rubric = new RubricModel();
		rubric.setName("test");
		
		List<AspectModel> aspects = new ArrayList<AspectModel>();
		rubric.setAspects(aspects);

		taskModel.setRubric(rubric);
		task = Optional.of(taskModel);

        when(cassandraRepo.getTaskForSubmission(taskId)).thenReturn(task);
        when(cassandraRepo.getSubmissionById(submissionId)).thenReturn(submission);
        when(cassandraRepo.getSubmissionsByStudentId(studentId)).thenReturn(Arrays.asList(new SubmissionByStudentAndTaskModel()));
        when(cassandraRepo.getSubmissionByStudentById(studentId, submissionId)).thenReturn(submission);

        Field academicActivityTitle = findField(studentWorkService.getClass(), "academicActivityTitle");
        Field academicActivitySubmitForEvaluation = findField(studentWorkService.getClass(), "academicActivitySubmitForEvaluation");

        makeAccessible(academicActivityTitle);
        makeAccessible(academicActivitySubmitForEvaluation);

        setField(academicActivityTitle, studentWorkService, titleValue);
        setField(academicActivitySubmitForEvaluation, studentWorkService, submitValue);
    }
    
	@Test
    public void testBeginSubmission() throws SubmissionStatusException {        
        studentWorkService.beginSubmission(studentId, taskId, 123456L);

        ArgumentCaptor<SubmissionByIdModel> sub = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(sub.capture(), user.capture(), status.capture());
        assertEquals(StatusUtil.AUTHOR_SUBMISSION_STARTED, sub.getValue().getStatus());
        assertEquals(1, sub.getValue().getAttempt());
    }
        
    @Test
    public void testBeginSubmissionNextAttempt() throws SubmissionStatusException {
		EvaluationBySubmissionModel evaluation = new EvaluationBySubmissionModel();
		evaluation.setEvaluatorId("123");
		evaluation.setStatus(StatusUtil.COMPLETED);
		evaluation.setDateStarted(new Date());
		
        SubmissionByStudentAndTaskModel sub = new SubmissionByStudentAndTaskModel();
        sub.setStatus(StatusUtil.AUTHOR_WORK_NEEDS_REVISION);
        sub.setStudentId(studentId);
        sub.setTaskId(taskId);
        sub.setSubmissionId(submissionId);
        sub.setAssessmentCode(assessmentCode);
        sub.setCourseCode(courseCode);
        sub.setAttempt(2);
        
        when(cassandraRepo.getEvaluation("123", submissionId)).thenReturn(Arrays.asList(evaluation));
        when(cassandraRepo.getSubmissionByStudentByTask(studentId, taskId)).thenReturn(Arrays.asList(sub));
        when(cassandraRepo.getSubmissionById(any(UUID.class))).thenReturn(submission);
        when(cassandraRepo.getSubmissionStatus(any(UUID.class))).thenReturn(submission);
        
        studentWorkService.beginSubmission(studentId, taskId, 123456L);
        
        ArgumentCaptor<SubmissionByIdModel> subArg = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(subArg.capture(), user.capture(), status.capture());
        assertEquals(StatusUtil.AUTHOR_RESUBMISSION_STARTED, subArg.getValue().getStatus());
        assertEquals(3, subArg.getValue().getAttempt());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testBeginSubmissionNullStudent() throws SubmissionStatusException {
        Submission sub = new Submission();
        sub.setTaskId(taskId);
        sub.setCourseCode(courseCode);
        sub.setAssessmentCode(assessmentCode);
        
        studentWorkService.beginSubmission(null, taskId, 123456L);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testBeginSubmissionNullTaskCode() throws SubmissionStatusException {
        Submission sub = new Submission();
        sub.setStudentId(studentId);
        sub.setCourseCode(courseCode);
        sub.setAssessmentCode(assessmentCode);
        
        studentWorkService.beginSubmission(studentId, null, 123456L);
    }
    
    @Test
    public void testAddComments() throws SubmissionStatusException {
        Submission result = studentWorkService.addComments(studentId, submissionId, "comment");
        assertEquals("comment", result.getComments());
    }
    
    @Test(expected=SubmissionNotFoundException.class)
    public void testAddCommentsSubmissionNotFound() throws SubmissionStatusException {
    	UUID test = UUID.randomUUID();
    	when(cassandraRepo.getSubmissionByStudentById(studentId, test)).thenReturn(Optional.empty());
        studentWorkService.addComments(studentId, test, "comment");
    }
    
    @Test
    public void testGetSubmission() {
        Submission result = studentWorkService.getSubmission(studentId, submissionId);
        assertNotNull(result);
        assertEquals(result, new Submission(submission.get()));
    }
    
    @Test(expected=SubmissionNotFoundException.class)
    public void testGetSubmissionNotFound() {
        when(cassandraRepo.getSubmissionByStudentById(studentId, submissionId)).thenReturn(Optional.empty());
        studentWorkService.getSubmission(studentId, submissionId);
    }
    
    @Test
    public void testGetSubmissions() {
        List<Submission> result = studentWorkService.getSubmissions(studentId);
        assertNotNull(result);
    }

    @Test
    public void testSubmitForEvaluation() throws SubmissionStatusException {
        SubmissionByIdModel idModel = new SubmissionByIdModel();
        idModel.setAttachments(createTestAttachmentMapGood());
        idModel.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);

        when(cassandraRepo.getSubmissionByStudentById(anyString(), any(UUID.class))).thenReturn(Optional.ofNullable(idModel));
        doNothing().when(publishAcademicActivityService).publishAcademicActivity(any(SubmissionModel.class), anyString());
        doNothing().when(emailService).sendEmail(any(SubmissionModel.class));

        studentWorkService.submitForEvaluation(studentId, submissionId, null);

        ArgumentCaptor<SubmissionByIdModel> sub = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(sub.capture(), user.capture(), status.capture());
        assertEquals(StatusUtil.AUTHOR_WORK_SUBMITTED, sub.getValue().getStatus());
        assertNotNull(sub.getValue().getDateSubmitted());
        assertNotNull(sub.getValue().getDateEstimated());
        verify(publishAcademicActivityService, times(1)).publishAcademicActivity(any(SubmissionModel.class), eq(titleValue + submitValue));
    }

    @Test (expected = AttachmentSizeException.class)
    public void testSubmitForEvaluationAttachmentSizeException() throws SubmissionStatusException {
        SubmissionByIdModel idModel = new SubmissionByIdModel();
        idModel.setAttachments(createTestAttachmentMapNoGood());
        idModel.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);

        when(cassandraRepo.getSubmissionByStudentById(anyString(), any(UUID.class))).thenReturn(Optional.ofNullable(idModel));

        studentWorkService.submitForEvaluation(studentId, submissionId, null);

    }

    @Test (expected = AttachmentSizeException.class)
    public void testSubmitForEvaluationAttachmentSizeExceptionNullAttachments() throws SubmissionStatusException {
        SubmissionByIdModel idModel = new SubmissionByIdModel();
        idModel.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);

        when(cassandraRepo.getSubmissionByStudentById(anyString(), any(UUID.class))).thenReturn(Optional.ofNullable(idModel));

        studentWorkService.submitForEvaluation(studentId, submissionId, null);

    }

    @Test (expected = AttachmentSizeException.class)
    public void testSubmitForEvaluationAttachmentSizeExceptionNoAttachments() throws SubmissionStatusException {
        SubmissionByIdModel idModel = new SubmissionByIdModel();
        idModel.setAttachments(new HashMap<>());
        idModel.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);

        when(cassandraRepo.getSubmissionByStudentById(anyString(), any(UUID.class))).thenReturn(Optional.ofNullable(idModel));

        studentWorkService.submitForEvaluation(studentId, submissionId, null);

    }

    @Test
    public void testSubmitForEvaluationAttachmentSizeExceptionUrlOnly() throws SubmissionStatusException {
        Map<String, AttachmentModel> attachmentMap = new HashMap<>();

        AttachmentModel attachment1 = new AttachmentModel();
        attachment1.setSize(0L);
        attachment1.setTitle("Attachment1");
        attachment1.setIsTaskDocument(true);
        attachment1.setMimeType("document");
        attachment1.setIsUrl(true);

        attachmentMap.put(attachment1.getTitle(), attachment1);

        SubmissionByIdModel idModel = new SubmissionByIdModel();
        idModel.setAttachments(attachmentMap);
        idModel.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);

        when(cassandraRepo.getSubmissionByStudentById(anyString(), any(UUID.class))).thenReturn(Optional.ofNullable(idModel));

        studentWorkService.submitForEvaluation(studentId, submissionId, null);

        ArgumentCaptor<SubmissionByIdModel> sub = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(sub.capture(), user.capture(), status.capture());
        assertEquals(StatusUtil.AUTHOR_WORK_SUBMITTED, sub.getValue().getStatus());
        assertNotNull(sub.getValue().getDateSubmitted());
        assertNotNull(sub.getValue().getDateEstimated());
        verify(publishAcademicActivityService, times(1)).publishAcademicActivity(any(SubmissionModel.class), eq(titleValue + submitValue));

    }

    @Test
    public void testSubmitForEvaluationAttachmentSizeExceptionFileOnly() throws SubmissionStatusException {
        Map<String, AttachmentModel> attachmentMap = new HashMap<>();

        AttachmentModel attachment1 = new AttachmentModel();
        attachment1.setSize(1234L);
        attachment1.setTitle("Attachment1");
        attachment1.setIsTaskDocument(true);
        attachment1.setMimeType("document");
        attachment1.setIsUrl(false);

        attachmentMap.put(attachment1.getTitle(), attachment1);

        SubmissionByIdModel idModel = new SubmissionByIdModel();
        idModel.setAttachments(attachmentMap);
        idModel.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);

        when(cassandraRepo.getSubmissionByStudentById(anyString(), any(UUID.class))).thenReturn(Optional.ofNullable(idModel));

        studentWorkService.submitForEvaluation(studentId, submissionId, null);

        ArgumentCaptor<SubmissionByIdModel> sub = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(sub.capture(), user.capture(), status.capture());
        assertEquals(StatusUtil.AUTHOR_WORK_SUBMITTED, sub.getValue().getStatus());
        assertNotNull(sub.getValue().getDateSubmitted());
        assertNotNull(sub.getValue().getDateEstimated());
        verify(publishAcademicActivityService, times(1)).publishAcademicActivity(any(SubmissionModel.class), eq(titleValue + submitValue));

    }

    @Test(expected=SubmissionNotFoundException.class)
    public void testSubmitForEvaluationSubmissionNotFound() throws SubmissionStatusException {
    	UUID test = UUID.randomUUID();
    	when(cassandraRepo.getSubmissionByStudentById(studentId, test)).thenReturn(Optional.empty());
        studentWorkService.submitForEvaluation(studentId, test, null);
    }
    
    @Test(expected=SubmissionNotFoundException.class)
    public void testSubmitForEvaluationNullId() throws SubmissionStatusException {
    	when(cassandraRepo.getSubmissionByStudentById(studentId, null)).thenReturn(Optional.empty());
        studentWorkService.submitForEvaluation(studentId, null, null);
    }
    
    @Test
    public void testCancelSubmission() throws SubmissionStatusException {
        Optional<SubmissionByIdModel> cancelled;
        SubmissionByIdModel canc = new SubmissionByIdModel();
        canc.setStudentId(studentId);
        canc.setSubmissionId(submissionId);
        canc.setStatus(StatusUtil.SUBMISSION_CANCELLED);
        cancelled = Optional.of(canc);
        when(cassandraRepo.getSubmissionStatus(any(UUID.class))).thenReturn(cancelled);
        
        submission.get().setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
        studentWorkService.cancelSubmission(studentId, submissionId);
        
        ArgumentCaptor<SubmissionByIdModel> sub = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(sub.capture(), user.capture(), status.capture());

        assertEquals(StatusUtil.SUBMISSION_CANCELLED, sub.getValue().getStatus());
    }
    
    @Test(expected=SubmissionNotFoundException.class)
    public void testCancelSubmissionNullId() throws SubmissionStatusException {
    	when(cassandraRepo.getSubmissionByStudentById(studentId, null)).thenReturn(Optional.empty());
        studentWorkService.cancelSubmission(studentId, null);
    }
    
    @Test(expected=SubmissionStatusException.class)
    public void testCancelSubmissionNotPending() throws SubmissionStatusException {
        Submission sub = new Submission();
        sub.setStatus(StatusUtil.EVALUATION_BEGUN);
        Optional<SubmissionByIdModel> submissionById = Optional.of(new SubmissionByIdModel(sub));
        when(cassandraRepo.getSubmissionByStudentById(any(String.class), any(UUID.class))).thenReturn(submissionById);
        
        studentWorkService.cancelSubmission(studentId, UUID.randomUUID());
    }
    
    @Test
    public void testAddAttachment() throws SubmissionStatusException {
        AttachmentModel attachment = new AttachmentModel();
        attachment.setTitle("title");
        attachment.setSize(new Long(101));
        attachment.setUrl("location/to/file");
        studentWorkService.addAttachment(studentId, submissionId, attachment);
        
        ArgumentCaptor<SubmissionByIdModel> sub = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(sub.capture(), user.capture(), status.capture());
        assertEquals(1, sub.getValue().getAttachmentsNS().size());
    }
    
    @Test
    public void testAddAttachment2() throws SubmissionStatusException {
        Submission sub = new Submission();
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment attachment1 = new Attachment();
        attachment1.setTitle("title");
        attachment1.setSize(new Long(101));
        attachment1.setUrl("location/to/file");
        Attachment attachment2 = new Attachment();
        attachment2.setTitle("title2");
        attachment2.setSize(new Long(202));
        attachment2.setUrl("location/to/file2");
        attachments.add(attachment1);
        attachments.add(attachment2);
        sub.setAttachments(attachments);
        sub.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);
        
        Optional<SubmissionByIdModel> submissionById = Optional.of(new SubmissionByIdModel(sub));
        when(cassandraRepo.getSubmissionByStudentById(studentId, submissionId)).thenReturn(submissionById);
        
        Attachment attachment3 = new Attachment();
        attachment3.setTitle("title3");
        studentWorkService.addAttachment(studentId, submissionId, new AttachmentModel(attachment3));
        
        ArgumentCaptor<SubmissionByIdModel> subArg = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(subArg.capture(), user.capture(), status.capture());
        assertEquals(3, subArg.getValue().getAttachmentsNS().size());
    }
    
    @Test(expected=SubmissionNotFoundException.class)
    public void testAddAttachmentSubmissionNotFound() throws SubmissionStatusException {
    	UUID test = UUID.randomUUID();
    	when(cassandraRepo.getSubmissionByStudentById(studentId, test)).thenReturn(Optional.empty());
        AttachmentModel attachment = new AttachmentModel();
        attachment.setTitle("Title");
        studentWorkService.addAttachment(studentId, test, attachment);
    }
    
    @Test(expected=SubmissionStatusException.class)
    public void testAddAttachmentBadStatus() throws SubmissionStatusException {
        AttachmentModel attachment = new AttachmentModel();
        attachment.setTitle("Title");
        Submission sub = new Submission();
        sub.setStudentId(studentId);
        sub.setSubmissionId(submissionId);
        sub.setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
        Optional<SubmissionByIdModel> submissionById = Optional.of(new SubmissionByIdModel(sub));
        when(cassandraRepo.getSubmissionByStudentById(studentId, submissionId)).thenReturn(submissionById);
        
        studentWorkService.addAttachment(studentId, submissionId, attachment);
    }
    
    @Test
    public void testRemoveAttachment() throws SubmissionStatusException {
        Submission sub = new Submission();
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment attachment1 = new Attachment();
        attachment1.setTitle("title 1");
        attachment1.setSize(new Long(101));
        attachment1.setUrl("location/to/file1");
        Attachment attachment2 = new Attachment();
        attachment2.setTitle("title 2");
        attachment2.setSize(new Long(202));
        attachment2.setUrl("location/to/file2");
        attachments.add(attachment1);
        attachments.add(attachment2);
        sub.setAttachments(attachments);
        sub.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);
        Optional<SubmissionByIdModel> submissionById = Optional.of(new SubmissionByIdModel(sub));
        when(cassandraRepo.getSubmissionByStudentById(studentId, submissionId)).thenReturn(submissionById);
        
        studentWorkService.removeAttachment(studentId, submissionId, "title 1");
        
        ArgumentCaptor<SubmissionByIdModel> subArg = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(subArg.capture(), user.capture(), status.capture());
        assertEquals(1, subArg.getValue().getAttachmentsNS().size());
    }
    
    @Test(expected=SubmissionNotFoundException.class)
    public void testRemoveAttachmentSubmissionNotFound() throws SubmissionStatusException {
    	UUID test = UUID.randomUUID();
    	when(cassandraRepo.getSubmissionByStudentById(studentId, test)).thenReturn(Optional.empty());
        studentWorkService.removeAttachment(studentId, test, "title");
    }
    
    @Test
    public void testRemoveAttachmentEmptySet() throws SubmissionStatusException {
        studentWorkService.removeAttachment(studentId, submissionId, "title");
        
        Mockito.verify(cassandraRepo).getSubmissionByStudentById(studentId, submissionId);
        Mockito.verifyNoMoreInteractions(cassandraRepo);
    }
    
    @Test
    public void testRemoveAttachments() throws SubmissionStatusException {
        Submission sub = new Submission();
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment attachment1 = new Attachment();
        attachment1.setTitle("title 1");
        attachment1.setSize(new Long(101));
        attachment1.setUrl("location/to/file1");
        Attachment attachment2 = new Attachment();
        attachment2.setTitle("title 2");
        attachment2.setSize(new Long(202));
        attachment2.setUrl("location/to/file2");
        attachments.add(attachment1);
        attachments.add(attachment2);
        sub.setAttachments(attachments);
        sub.setStatus(StatusUtil.AUTHOR_RESUBMISSION_STARTED);
        Optional<SubmissionByIdModel> submissionById = Optional.of(new SubmissionByIdModel(sub));
        when(cassandraRepo.getSubmissionByStudentById(studentId, submissionId)).thenReturn(submissionById);
        
        studentWorkService.removeAttachments(studentId, submissionId);
        
        ArgumentCaptor<SubmissionByIdModel> subArg = ArgumentCaptor.forClass(SubmissionByIdModel.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);

        Mockito.verify(cassandraRepo).saveSubmission(subArg.capture(), user.capture(), status.capture());
        assertEquals(0, subArg.getValue().getAttachmentsNS().size());
    }
    
    @Test(expected=SubmissionNotFoundException.class)
    public void testRemoveAttachmentsSubmissionNotFound() throws SubmissionStatusException {
    	UUID test = UUID.randomUUID();
    	when(cassandraRepo.getSubmissionByStudentById(studentId, test)).thenReturn(Optional.empty());
        studentWorkService.removeAttachments(studentId, test);
    }
    
    @Test
    public void testSaveSubmission() throws SubmissionStatusException {
        submission.get().setComments("notes for evaluator...");
        Submission savedSubmission = studentWorkService.saveSubmission(studentId, submissionId, new Submission(submission.get()));
        
        Mockito.verify(cassandraRepo).saveSubmission(submission.get(), studentId, StatusUtil.AUTHOR_SUBMISSION_STARTED);
        assertEquals(savedSubmission, new Submission(submission.get()));
    }
    
    @Test(expected=SubmissionNotFoundException.class)
    public void testSaveSubmissionNotFound() throws SubmissionStatusException {
    	UUID test = UUID.randomUUID();
    	when(cassandraRepo.getSubmissionByStudentById(studentId, test)).thenReturn(Optional.empty());
        studentWorkService.saveSubmission(studentId, test, new Submission());
    }
    
    @Test
    public void testSubmissionByStudentSort() {
    	List<SubmissionByStudentAndTaskModel> submissions = new ArrayList<SubmissionByStudentAndTaskModel>();
    	SubmissionByStudentAndTaskModel sub1 = new SubmissionByStudentAndTaskModel();
    	sub1.setAttempt(1);
    	submissions.add(sub1);
    	
    	SubmissionByStudentAndTaskModel sub2 = new SubmissionByStudentAndTaskModel();
    	sub2.setAttempt(3);
    	submissions.add(sub2);
    	
    	SubmissionByStudentAndTaskModel sub3 = new SubmissionByStudentAndTaskModel();
    	sub3.setAttempt(2);
    	submissions.add(sub3);
    	
    	Collections.sort(submissions);
    	assertEquals(3, submissions.get(0).getAttempt());
    }


    private static Map<String, AttachmentModel> createTestAttachmentMapNoGood() {
        Map<String, AttachmentModel> attachmentMap = new HashMap<>();

        AttachmentModel attachment1 = TestObjectFactory.getAttachmentModel("Attachment1", new Long(0), Boolean.TRUE, "document", Boolean.FALSE, null);
        AttachmentModel attachment2 = TestObjectFactory.getAttachmentModel("Attachment2", new Long(123), Boolean.TRUE, "document", Boolean.FALSE, null);
        AttachmentModel attachment3 = TestObjectFactory.getAttachmentModel("Attachment3", null, Boolean.TRUE, "url", Boolean.TRUE, null);

        attachmentMap.put(attachment1.getTitle(), attachment1);
        attachmentMap.put(attachment2.getTitle(), attachment2);
        attachmentMap.put(attachment3.getTitle(), attachment3);

        return attachmentMap;
    }

    private static Map<String, AttachmentModel> createTestAttachmentMapGood() {
        Map<String, AttachmentModel> attachmentMap = new HashMap<>();

        AttachmentModel attachment1 = TestObjectFactory.getAttachmentModel("Attachment1", new Long(8765), Boolean.TRUE, "document", Boolean.FALSE, null);
        AttachmentModel attachment2 = TestObjectFactory.getAttachmentModel("Attachment2", new Long(123), Boolean.TRUE, "document", Boolean.FALSE, null);
        AttachmentModel attachment3 = TestObjectFactory.getAttachmentModel("Attachment3", null, Boolean.TRUE, "url", Boolean.TRUE, null);

        attachmentMap.put(attachment1.getTitle(), attachment1);
        attachmentMap.put(attachment2.getTitle(), attachment2);
        attachmentMap.put(attachment3.getTitle(), attachment3);

        return attachmentMap;
    }

}
