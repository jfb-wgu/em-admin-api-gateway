package edu.wgu.dmadmin.domain.assessment;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class EvaluationTest {
    private Evaluation evaluation;
    private EvaluationModel evaluationModel;

    @Before
    public void setUp() throws Exception {
        this.evaluationModel = TestObjectFactory.getEvaluationModel(TestObjectFactory.getUserModel(), TestObjectFactory.getSubmissionModel(StatusUtil.EVALUATION_BEGUN));
        this.evaluation = new Evaluation(this.evaluationModel);
    }

    @Test
    public void testGetEvaluationId() throws Exception {
        assertEquals(this.evaluationModel.getEvaluationId(), this.evaluation.getEvaluationId());
        UUID evaluationId = UUID.randomUUID();
        this.evaluation.setEvaluationId(evaluationId);

        assertEquals(evaluationId, this.evaluation.getEvaluationId());
    }

    @Test
    public void testGetEvaluatorId() throws Exception {
        assertEquals(this.evaluationModel.getEvaluatorId(), this.evaluation.getEvaluatorId());
        String evalId = "EvalId";
        this.evaluation.setEvaluatorId(evalId);

        assertEquals(evalId, this.evaluation.getEvaluatorId());
    }

    @Test
    public void testGetSubmissionId() throws Exception {
        assertEquals(this.evaluationModel.getSubmissionId(), this.evaluation.getSubmissionId());
        UUID submissionId = UUID.randomUUID();
        this.evaluation.setSubmissionId(submissionId);

        assertEquals(submissionId, this.evaluation.getSubmissionId());
    }

    @Test
    public void testGetStatus() throws Exception {
        assertEquals(this.evaluationModel.getStatus(), this.evaluation.getStatus());
        String status = StatusUtil.EVALUATION_RELEASED;
        this.evaluation.setStatus(status);

        assertEquals(status, this.evaluation.getStatus());
    }

    @Test
    public void testGetMinutesSpent() throws Exception {
        assertEquals(this.evaluationModel.getMinutesSpent(), this.evaluation.getMinutesSpent());
        int minutesSpent = 2345;
        this.evaluation.setMinutesSpent(minutesSpent);

        assertEquals(minutesSpent, this.evaluation.getMinutesSpent());
    }

    @Test
    public void testGetTaskId() throws Exception {
        assertEquals(this.evaluationModel.getTaskId(), this.evaluation.getTaskId());
        UUID taskId = UUID.randomUUID();
        this.evaluation.setTaskId(taskId);

        assertEquals(taskId, this.evaluation.getTaskId());
    }

    @Test
    public void testGetAttempt() throws Exception {
        assertEquals(this.evaluationModel.getAttempt(), this.evaluation.getAttempt());
        int attempt = 4;
        this.evaluation.setAttempt(attempt);

        assertEquals(attempt, this.evaluation.getAttempt());
    }

    @Test
    public void testGetStudentId() throws Exception {
        assertEquals(this.evaluationModel.getStudentId(), this.evaluation.getStudentId());
        String studentId = "2343";
        this.evaluation.setStudentId(studentId);

        assertEquals(studentId, this.evaluation.getStudentId());
    }

    @Test
    public void testGetDateStarted() throws Exception {
        assertEquals(this.evaluationModel.getDateStarted(), this.evaluation.getDateStarted());
        Date dateStarted = new Date();
        this.evaluation.setDateStarted(dateStarted);

        assertEquals(dateStarted, this.evaluation.getDateStarted());
    }

    @Test
    public void testGetDateCompleted() throws Exception {
        assertEquals(this.evaluationModel.getDateCompleted(), this.evaluation.getDateCompleted());
        Date dateCompleted = new Date();
        this.evaluation.setDateCompleted(dateCompleted);

        assertEquals(dateCompleted, this.evaluation.getDateCompleted());
    }

    @Test
    public void testGetDateUpdated() throws Exception {
        assertEquals(this.evaluationModel.getDateUpdated(), this.evaluation.getDateUpdated());
        Date dateUpdated = new Date();
        this.evaluation.setDateUpdated(dateUpdated);

        assertEquals(dateUpdated, this.evaluation.getDateUpdated());
    }

    @Test
    public void testGetAttachments() throws Exception {
        assertEquals(this.evaluationModel.getAttachments(), this.evaluation.getAttachments());
        List<Attachment> attachmentList = TestObjectFactory.getAttachmentList(3);
        this.evaluation.setAttachments(attachmentList);

        assertEquals(attachmentList, this.evaluation.getAttachments());

    }

    @Test
    public void testGetScoreReport() throws Exception {
        assertEquals(this.evaluationModel.getScoreReport().getName(), this.evaluation.getScoreReport().getName());
        ScoreReport scoreReport = TestObjectFactory.getScoreReport("Test", "test");
        this.evaluation.setScoreReport(scoreReport);

        assertEquals(scoreReport, this.evaluation.getScoreReport());
    }

    @Test
    public void testGetEvaluatorFirstName() throws Exception {
        assertEquals(this.evaluationModel.getEvaluatorFirstName(), this.evaluation.getEvaluatorFirstName());
        String evaluatorFirstName = "John";
        this.evaluation.setEvaluatorFirstName(evaluatorFirstName);

        assertEquals(evaluatorFirstName, this.evaluation.getEvaluatorFirstName());
    }

    @Test
    public void testGetEvaluatorLastName() throws Exception {
        assertEquals(this.evaluationModel.getEvaluatorLastName(), this.evaluation.getEvaluatorLastName());
        String evaluatorLastName = "Doe";
        this.evaluation.setEvaluatorLastName(evaluatorLastName);

        assertEquals(evaluatorLastName, this.evaluation.getEvaluatorLastName());
    }

    @Test
    public void testConstructor() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        List<Attachment> attachments = TestObjectFactory.getAttachmentList(3);
        ScoreReport scoreReport = new ScoreReport();
        String evaluatorId = "12123123";
        UUID submissionId = UUID.randomUUID();
        String status = "Status";
        int minutesSpent = 234;
        UUID taskId = UUID.randomUUID();
        int attempt = 3;
        String studentId = "234232";
        Date date = new Date();
        String evaluatorFirstName = "John";
        String evaluatorLastName = "Doe";

        Evaluation eval2 = new Evaluation(evaluationId, evaluatorId, submissionId, status, minutesSpent, taskId, attempt, studentId, date, date, date, attachments, scoreReport, evaluatorFirstName, evaluatorLastName);

        assertEquals(evaluationId, eval2.getEvaluationId());
        assertEquals(attachments, eval2.getAttachments());
        assertEquals(scoreReport, eval2.getScoreReport());
        assertEquals(evaluatorId, eval2.getEvaluatorId());
        assertEquals(submissionId, eval2.getSubmissionId());
        assertEquals(status, eval2.getStatus());
        assertEquals(minutesSpent, eval2.getMinutesSpent());
        assertEquals(taskId, eval2.getTaskId());
        assertEquals(attempt, eval2.getAttempt());
        assertEquals(studentId, eval2.getStudentId());
        assertEquals(date, eval2.getDateUpdated());
        assertEquals(date, eval2.getDateCompleted());
        assertEquals(date, eval2.getDateStarted());
        assertEquals(evaluatorFirstName, eval2.getEvaluatorFirstName());
        assertEquals(evaluatorLastName, eval2.getEvaluatorLastName());
    }

}