package edu.wgu.dmadmin.domain.submission;

import edu.wgu.dmadmin.model.submission.AttachmentModel;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by joshua.barnett on 5/25/17.
 */
public class AttachmentTest {

    private Attachment attachment;
    UUID submissionId;
    String studentId;
    String url;
    String filename;
    UUID taskId;
    String courseCode;
    String assessmentCode;

    @Before
    public void setUp() throws Exception {
        attachment = new Attachment();
        submissionId = UUID.randomUUID();
        studentId = "106679";
        url = "www.starwars.com";
        filename = "test.pdf";
        taskId = UUID.randomUUID();
        courseCode = "C740";
        assessmentCode = "HIT2";
    }

    @Test
    public void testBuildStudentUrl() throws Exception {
        attachment.setTitle(filename);
        attachment.setStudentId(studentId);
        attachment.setSubmissionId(submissionId);
        assertNotNull(attachment.getDisplayUrl());
        assertEquals("dmsubmission/v1/student/" + studentId + "/submission/" + submissionId + "/attachment/" + filename, attachment.getDisplayUrl());
    }

    @Test
    public void testBuildStudentUrlIsUrlNotNull() throws Exception {
        attachment.setIsUrl(Boolean.TRUE);
        attachment.setUrl(url);
        assertEquals(url, attachment.getUrl());
    }

    @Test
    public void testBuildUrlNotUrlStudentAttachment() throws Exception {
        attachment.setIsUrl(Boolean.FALSE);
        attachment.setIsTaskDocument(Boolean.FALSE);
        attachment.setUrl(url);
        attachment.setTitle(filename);
        attachment.setStudentId(studentId);
        attachment.setSubmissionId(submissionId);

        assertEquals("dmsubmission/v1/student/" + studentId + "/submission/" + submissionId + "/attachment/" + filename, attachment.getDisplayUrl());
    }

    @Test
    public void testBuildUrlNotUrlTaskDocumentAttachment() throws Exception {

        attachment.setIsUrl(Boolean.FALSE);
        attachment.setIsTaskDocument(Boolean.TRUE);

        assertNull(attachment.getUrl());
    }

    @Test
    public void testBuildDocumentUrlNotUrl() throws Exception {
        
        attachment.setIsUrl(Boolean.FALSE);
        attachment.setIsTaskDocument(Boolean.TRUE);
        attachment.setUrl(url);
        attachment.setTitle(filename);
        attachment.setCourseCode(courseCode);
        attachment.setAssessmentCode(assessmentCode);
        attachment.setTaskId(taskId);
        assertEquals("dmsubmission/v1/assessment/supportingdocument/course/" + courseCode + "/assessment/" + assessmentCode + "/task/" + taskId + "/" + filename, attachment.getDisplayUrl());
    }

    @Test
    public void testBuildDocumentUrl() throws Exception {
        attachment.setTitle(filename);
        attachment.setCourseCode(courseCode);
        attachment.setAssessmentCode(assessmentCode);
        attachment.setTaskId(taskId);
        attachment.setIsTaskDocument(Boolean.TRUE);
        assertNotNull(attachment.getDisplayUrl());
        assertEquals("dmsubmission/v1/assessment/supportingdocument/course/" + courseCode + "/assessment/" + assessmentCode + "/task/" + taskId + "/" + filename, attachment.getDisplayUrl());
    }

    @Test
    public void testBuildDocumentUrlIsUrlNotNull() throws Exception {
        attachment.setIsUrl(Boolean.TRUE);
        attachment.setUrl(url);

        assertEquals(url, attachment.getUrl());
    }

    @Test
    public void testBuildDocumentUrlNotUrlStudentAttachment() throws Exception {
        attachment.setIsUrl(Boolean.FALSE);
        attachment.setIsTaskDocument(Boolean.FALSE);

        attachment.setTitle(filename);
        attachment.setStudentId(studentId);
        attachment.setSubmissionId(submissionId);

        assertNotNull(attachment.getDisplayUrl());
        assertEquals("dmsubmission/v1/student/" + studentId + "/submission/" + submissionId + "/attachment/" + filename, attachment.getDisplayUrl());
    }

    @Test
    public void testGetTitle() throws Exception {
        String title = "title";
        attachment.setTitle(title);

        assertEquals(title, attachment.getTitle());
    }

    @Test
    public void testGetUrl() throws Exception {
        attachment.setUrl(url);
        attachment.setIsUrl(Boolean.TRUE);

        assertEquals(url, attachment.getUrl());
    }

    @Test
    public void testGetMimeType() throws Exception {
        String mimeType = "mime/type";
        attachment.setMimeType(mimeType);

        assertEquals(mimeType, attachment.getMimeType());
    }

    @Test
    public void testGetSize() throws Exception {
        Long size = new Long(23432);
        attachment.setSize(size);

        assertEquals(size, attachment.getSize());
    }

    @Test
    public void testGetIsUrl() throws Exception {
        attachment.setIsUrl(Boolean.FALSE);

        assertFalse(attachment.getIsUrl().booleanValue());
    }

    @Test
    public void testGetIsTaskDocument() throws Exception {
        attachment.setIsTaskDocument(Boolean.FALSE);

        assertFalse(attachment.getIsTaskDocument().booleanValue());
    }

    @Test
    public void testConstructor() throws Exception {
        String mimeType = "mimeType";
        Long size = new Long(2343);

        AttachmentModel model = new AttachmentModel();
        model.setIsTaskDocument(Boolean.FALSE);
        model.setIsUrl(Boolean.FALSE);
        model.setMimeType(mimeType);
        model.setSize(size);
        model.setTitle(filename);
        model.setUrl(url);

        Attachment attachment1 = new Attachment(model, studentId, submissionId);

        assertFalse(attachment1.getIsTaskDocument().booleanValue());
        assertFalse(attachment1.getIsUrl().booleanValue());
        assertEquals(mimeType, attachment1.getMimeType());
        assertEquals(filename, attachment1.getTitle());
        assertEquals("dmsubmission/v1/student/" + studentId + "/submission/" + submissionId + "/attachment/" + filename, attachment1.getDisplayUrl());
        assertEquals(size, attachment1.getSize());
    }
}
