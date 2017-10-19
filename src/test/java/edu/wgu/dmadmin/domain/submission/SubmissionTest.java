package edu.wgu.dmadmin.domain.submission;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class SubmissionTest {
    Submission submission;

    @Before
    public void setUp() throws Exception {
        submission = new Submission();
    }

    @Test
    public void getStudentStatus() throws Exception {
        String studentStatus = StatusUtil.EVALUATION_RELEASED;
        submission.setStudentStatus(studentStatus);
        submission.setAttempt(2);
        submission.setStatus(StatusUtil.EVALUATION_RELEASED);
        assertEquals(StatusUtil.DISPLAY_PASSED, submission.getStudentStatus());
    }

    @Test
    public void getStatus() throws Exception {
        String status = "Status";
        submission.setStatus(status);
        assertEquals(status, submission.getStatus());
    }

    @Test
    public void getStatusGroup() throws Exception {
        String statusGroup = "Status Group";
        submission.setStatusGroup(statusGroup);
        assertEquals(statusGroup, submission.getStatusGroup());
    }

    @Test
    public void getDateCreated() throws Exception {
        Date dateCrated = new Date();
        submission.setDateCreated(dateCrated);
        assertEquals(dateCrated, submission.getDateCreated());
    }

    @Test
    public void getDateSubmitted() throws Exception {
        Date dateSubmitted = new Date();
        submission.setDateSubmitted(dateSubmitted);
        assertEquals(dateSubmitted, submission.getDateSubmitted());
    }

    @Test
    public void getDateUpdated() throws Exception {
        Date dateUpdated = new Date();
        submission.setDateUpdated(dateUpdated);
        assertEquals(dateUpdated, submission.getDateUpdated());
    }

    @Test
    public void getDateEstimated() throws Exception {
        Date dateEstimated = new Date();
        submission.setDateEstimated(dateEstimated);
        assertEquals(dateEstimated, submission.getDateEstimated());
    }

    @Test
    public void getDateStarted() throws Exception {
        Date dateStarted = new Date();
        submission.setDateStarted(dateStarted);
        assertEquals(dateStarted, submission.getDateStarted());
    }

    @Test
    public void getDateCompleted() throws Exception {
        Date dateCompleted = new Date();
        submission.setDateCompleted(dateCompleted);
        assertEquals(dateCompleted, submission.getDateCompleted());
    }

    @Test
    public void getTaskId() throws Exception {
        UUID taskId = UUID.randomUUID();
        submission.setTaskId(taskId);
        assertEquals(taskId, submission.getTaskId());
    }

    @Test
    public void getPidm() throws Exception {
        Long pidm = new Long(123456);
        submission.setPidm(pidm);
        assertEquals(pidm, submission.getPidm());
    }

    @Test
    public void getTaskName() throws Exception {
        String taskName = "task Name";
        submission.setTaskName(taskName);
        assertEquals(taskName, submission.getTaskName());
    }

    @Test
    public void getSubmissionId() throws Exception {
        UUID submissionId = UUID.randomUUID();
        submission.setSubmissionId(submissionId);
        assertEquals(submissionId, submission.getSubmissionId());
    }

    @Test
    public void getAttachments() throws Exception {
        List<Attachment> attachments = new ArrayList<>();
        submission.setAttachments(attachments);
        assertEquals(attachments, submission.getAttachments());
    }

    @Test
    public void getComments() throws Exception {
        String comments = "comments";
        submission.setComments(comments);
        assertEquals(comments, submission.getComments());
    }

    @Test
    public void getInternalComments() throws Exception {
        List<Comment> comments = new ArrayList<>();
        submission.setInternalComments(comments);
        assertEquals(comments, submission.getInternalComments());
    }

    @Test
    public void getEvaluatorId() throws Exception {
        String evaluatorId = "12345566";
        submission.setEvaluatorId(evaluatorId);
        assertEquals(evaluatorId, submission.getEvaluatorId());
    }

    @Test
    public void getEvaluatorFirstName() throws Exception {
        String firstName = "James";
        submission.setEvaluatorFirstName(firstName);
        assertEquals(firstName, submission.getEvaluatorFirstName());
    }

    @Test
    public void getEvaluatorLastName() throws Exception {
        String lastName = "James";
        submission.setEvaluatorLastName(lastName);
        assertEquals(lastName, submission.getEvaluatorLastName());
    }

    @Test
    public void getEvaluationId() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        submission.setEvaluationId(evaluationId);
        assertEquals(evaluationId, submission.getEvaluationId());
    }

    @Test
    public void getStudentId() throws Exception {
        String studentId = "234456436";
        submission.setStudentId(studentId);
        assertEquals(studentId, submission.getStudentId());
    }

    @Test
    public void getAttempt() throws Exception {
        int attempt = 3;
        submission.setAttempt(attempt);
        assertEquals(attempt, submission.getAttempt());
    }

    @Test
    public void getAspectCount() throws Exception {
        int aspectCount = 43;
        submission.setAspectCount(aspectCount);
        assertEquals(aspectCount, submission.getAspectCount());
    }

    @Test
    public void getAssessmentId() throws Exception {
        UUID assessmentId = UUID.randomUUID();
        submission.setAssessmentId(assessmentId);
        assertEquals(assessmentId, submission.getAssessmentId());
    }

    @Test
    public void getAssessmentCode() throws Exception {
        String assessmentCode = "C7543";
        submission.setAssessmentCode(assessmentCode);
        assertEquals(assessmentCode, submission.getAssessmentCode());
    }

    @Test
    public void getAssessmentName() throws Exception {
        String assessmentName = "James";
        submission.setAssessmentName(assessmentName);
        assertEquals(assessmentName, submission.getAssessmentName());
    }

    @Test
    public void getCourseCode() throws Exception {
        String courseCode = "James";
        submission.setCourseCode(courseCode);
        assertEquals(courseCode, submission.getCourseCode());
    }

    @Test
    public void getCourseName() throws Exception {
        String courseName = "James";
        submission.setCourseName(courseName);
        assertEquals(courseName, submission.getCourseName());
    }

    @Test
    public void getReferrals() throws Exception {
        List<Referral> referrals = new ArrayList<>();
        submission.setReferrals(referrals);
        assertEquals(referrals, submission.getReferrals());
    }

    @Test
    public void getPreviousSubmissionId() throws Exception {
        UUID preSubId = UUID.randomUUID();
        submission.setPreviousSubmissionId(preSubId);
        assertEquals(preSubId, submission.getPreviousSubmissionId());
    }

    @Test
    public void getPreviousEvaluationId() throws Exception {
        UUID preEvalId = UUID.randomUUID();
        submission.setPreviousEvaluationId(preEvalId);
        assertEquals(preEvalId, submission.getPreviousEvaluationId());
    }

    @Test
    public void getReviewEvaluationId() throws Exception {
        UUID reviewEvaluationId = UUID.randomUUID();
        submission.setReviewEvaluationId(reviewEvaluationId);
        assertEquals(reviewEvaluationId, submission.getReviewEvaluationId());
    }

    @Test
    public void testConstructor() throws Exception {
        SubmissionModel model = TestObjectFactory.getSubmissionModel(StatusUtil.EVALUATION_RELEASED);
        Submission sub = new Submission(model);

        assertEquals(model.getPidm(), sub.getPidm());
    }

}