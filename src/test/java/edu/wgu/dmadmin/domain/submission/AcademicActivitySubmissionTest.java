package edu.wgu.dmadmin.domain.submission;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class AcademicActivitySubmissionTest {
    AcademicActivitySubmission academicActivitySubmission;
    private String assessmentCode = "IXP2";
    private UUID assessmentId = new UUID(12343223L, 3L);
    private String comments = "Comments";
    private UUID evaluationId = new UUID(123456L, 2L);
    private String evaluatorId = "1234234";
    private String status = "2";
    private String studentId = "000121292";
    private UUID submissionId = new UUID(1234L, 1L);
    private Long pidm = new Long(123456);
    private String evaluatorFirstName = "Joshua";
    private String evaluatorLastName = "Smith";
    private String statusGroup = "PENDING";
    private String assessmentName = "Assessment Name";
    private int aspectCount = 3;
    private int attempt = 3;
    private String courseCode = "C745";
    private String courseName = "course123";
    private Date dateCreated = new Date();
    private Date dateSubmitted = new Date();
    private Date dateUpdated = new Date();
    private Date dateEstimated = new Date();
    private Date dateStarted = new Date();
    private Date dateCompleted = new Date();
    private UUID taskId = UUID.randomUUID();
    private String task_name = "task Name";
    private Submission sub;

    private Submission setUpSubmission(){
        sub = new Submission();
        sub.setStatus(status);
        sub.setDateCreated(dateCreated);
        sub.setDateSubmitted(dateSubmitted);
        sub.setDateUpdated(dateUpdated);
        sub.setDateEstimated(dateEstimated);
        sub.setDateStarted(dateStarted);
        sub.setDateCompleted(dateCompleted);
        sub.setTaskId(taskId);
        sub.setTaskName(task_name);
        sub.setSubmissionId(submissionId);
        sub.setAssessmentCode(assessmentCode);
        sub.setStudentId(studentId);
        sub.setAssessmentId(assessmentId);
        sub.setEvaluationId(evaluationId);
        sub.setEvaluatorId(evaluatorId);
        sub.setComments(comments);
        sub.setAttachments(null);
        sub.setInternalComments(null);
        sub.setEvaluatorFirstName(evaluatorFirstName);
        sub.setEvaluatorLastName(evaluatorLastName);
        sub.setStatusGroup(statusGroup);
        sub.setAssessmentName(assessmentName);
        sub.setAspectCount(aspectCount);
        sub.setCourseCode(courseCode);
        sub.setCourseName(courseName);
        sub.setAttempt(attempt);
        sub.setReferrals(null);
        sub.setPreviousSubmissionId(null);
        sub.setPreviousEvaluationId(null);
        sub.setPidm(pidm);
        return sub;
    }

    @Before
    public void setUp() throws Exception {
        setUpSubmission();
        academicActivitySubmission = new AcademicActivitySubmission(sub);
    }

    @Test
    public void testGetStatus() throws Exception {
        assertEquals(status, academicActivitySubmission.getStatus());
    }

    @Test
    public void testGetDateCreated() throws Exception {
        assertEquals(dateCreated, academicActivitySubmission.getDateCreated());
    }

    @Test
    public void testGetDateSubmitted() throws Exception {
        assertEquals(dateSubmitted, academicActivitySubmission.getDateSubmitted());
    }

    @Test
    public void testGetDateUpdated() throws Exception {
        assertEquals(dateUpdated, academicActivitySubmission.getDateUpdated());
    }

    @Test
    public void testGetDateEstimated() throws Exception {
        assertEquals(dateEstimated, academicActivitySubmission.getDateEstimated());
    }

    @Test
    public void testGetDateStarted() throws Exception {
        assertEquals(dateStarted, academicActivitySubmission.getDateStarted());
    }

    @Test
    public void testGetDateCompleted() throws Exception {
        assertEquals(dateCompleted, academicActivitySubmission.getDateCompleted());
    }

    @Test
    public void testGetTaskId() throws Exception {
        assertEquals(taskId.toString(), academicActivitySubmission.getTaskId());
    }

    @Test
    public void testGetTaskName() throws Exception {
        assertEquals(task_name, academicActivitySubmission.getTaskName());
    }

    @Test
    public void testGetSubmissionId() throws Exception {
        assertEquals(submissionId.toString(), academicActivitySubmission.getSubmissionId());
    }

    @Test
    public void testGetAssessmentCode() throws Exception {
        assertEquals(assessmentCode, academicActivitySubmission.getAssessmentCode());

    }

    @Test
    public void testGetStudentId() throws Exception {
        assertEquals(studentId, academicActivitySubmission.getStudentId());
    }

    @Test
    public void testGetPidm() throws Exception {
        assertEquals(pidm, academicActivitySubmission.getPidm());
    }

    @Test
    public void testGetAssessmentId() throws Exception {
        assertEquals(assessmentId.toString(), academicActivitySubmission.getAssessmentId());
    }

    @Test
    public void testGetEvaluationId() throws Exception {
        assertEquals(evaluationId.toString(), academicActivitySubmission.getEvaluationId());
    }

    @Test
    public void testGetEvaluatorId() throws Exception {
        assertEquals(evaluatorId, academicActivitySubmission.getEvaluatorId());
    }

    @Test
    public void testGetComments() throws Exception {
        assertEquals(comments, academicActivitySubmission.getComments());
    }

}