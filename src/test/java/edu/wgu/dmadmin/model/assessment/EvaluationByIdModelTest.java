package edu.wgu.dmadmin.model.assessment;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.assessment.Evaluation;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class EvaluationByIdModelTest {
    private EvaluationByIdModel model;

    @Before
    public void setUp() throws Exception {
        model = new EvaluationByIdModel();
    }

    @Test
    public void testGetEvaluationId() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        model.setEvaluationId(evaluationId);

        assertEquals(evaluationId, model.getEvaluationId());
    }

    @Test
    public void testGetEvaluatorId() throws Exception {
        String evalId = "EvalId";
        model.setEvaluatorId(evalId);

        assertEquals(evalId, model.getEvaluatorId());
    }

    @Test
    public void testGetSubmissionId() throws Exception {
        UUID submissionId = UUID.randomUUID();
        model.setSubmissionId(submissionId);

        assertEquals(submissionId, model.getSubmissionId());
    }

    @Test
    public void testGetStudentId() throws Exception {
        String studentId = "7483732";
        model.setStudentId(studentId);

        assertEquals(studentId, model.getStudentId());
    }

    @Test
    public void testGetTaskId() throws Exception {
        UUID taskId = UUID.randomUUID();
        model.setTaskId(taskId);

        assertEquals(taskId, model.getTaskId());
    }

    @Test
    public void testGetAttempt() throws Exception {
        int attempt = 34;
        model.setAttempt(attempt);

        assertEquals(attempt, model.getAttempt());
    }

    @Test
    public void testConstructor() throws Exception {
        UserModel user = TestObjectFactory.getUserModel();
        SubmissionModel submission = TestObjectFactory.getSubmissionModel(StatusUtil.EVALUATION_BEGUN);
        Evaluation evaluation = TestObjectFactory.getEvaluation(user, submission);
        EvaluationByIdModel model2 = new EvaluationByIdModel(evaluation);

        assertEquals(evaluation.getEvaluationId(), model2.getEvaluationId());
        assertEquals(evaluation.getEvaluatorId(), model2.getEvaluatorId());
        assertEquals(evaluation.getSubmissionId(), model2.getSubmissionId());
        assertEquals(evaluation.getStatus(), model2.getStatus());
        assertEquals(evaluation.getMinutesSpent(), model2.getMinutesSpent());
        assertEquals(evaluation.getDateStarted(), model2.getDateStarted());
        assertEquals(evaluation.getDateCompleted(), model2.getDateCompleted());
        assertEquals(evaluation.getDateUpdated(), model2.getDateUpdated());
        assertEquals(evaluation.getAttachments(), model2.getAttachments());
        assertEquals(evaluation.getAttempt(), model2.getAttempt());
        assertEquals(evaluation.getStudentId(), model2.getStudentId());
        assertEquals(evaluation.getTaskId(), model2.getTaskId());
        assertEquals(evaluation.getEvaluatorFirstName(), model2.getEvaluatorFirstName());
        assertEquals(evaluation.getEvaluatorLastName(), model2.getEvaluatorLastName());
        assertEquals(evaluation.getScoreReport().getName(), model2.getScoreReport().getName());
    }

    @Test
    public void testConstructorNullScoreReport() throws Exception {
        UserModel user = TestObjectFactory.getUserModel();
        SubmissionModel submission = TestObjectFactory.getSubmissionModel(StatusUtil.EVALUATION_BEGUN);
        Evaluation evaluation = TestObjectFactory.getEvaluation(user, submission);
        evaluation.setScoreReport(null);
        EvaluationByIdModel model2 = new EvaluationByIdModel(evaluation);

        assertEquals(evaluation.getEvaluationId(), model2.getEvaluationId());
        assertEquals(evaluation.getEvaluatorId(), model2.getEvaluatorId());
        assertEquals(evaluation.getSubmissionId(), model2.getSubmissionId());
        assertEquals(evaluation.getStatus(), model2.getStatus());
        assertEquals(evaluation.getMinutesSpent(), model2.getMinutesSpent());
        assertEquals(evaluation.getDateStarted(), model2.getDateStarted());
        assertEquals(evaluation.getDateCompleted(), model2.getDateCompleted());
        assertEquals(evaluation.getDateUpdated(), model2.getDateUpdated());
        assertEquals(evaluation.getAttachments(), model2.getAttachments());
        assertEquals(evaluation.getAttempt(), model2.getAttempt());
        assertEquals(evaluation.getStudentId(), model2.getStudentId());
        assertEquals(evaluation.getTaskId(), model2.getTaskId());
        assertEquals(evaluation.getEvaluatorFirstName(), model2.getEvaluatorFirstName());
        assertEquals(evaluation.getEvaluatorLastName(), model2.getEvaluatorLastName());
        assertNull(model2.getScoreReport().getName());
    }

    @Test
    public void testConstructor2() throws Exception {
        UserModel user = TestObjectFactory.getUserModel();
        SubmissionModel submission = TestObjectFactory.getSubmissionModel(StatusUtil.EVALUATION_BEGUN);
        EvaluationModel evaluation = TestObjectFactory.getEvaluationModel(user, submission);
        EvaluationByIdModel model2 = new EvaluationByIdModel(evaluation);

        assertEquals(evaluation.getEvaluationId(), model2.getEvaluationId());
        assertEquals(evaluation.getEvaluatorId(), model2.getEvaluatorId());
        assertEquals(evaluation.getSubmissionId(), model2.getSubmissionId());
        assertEquals(evaluation.getStatus(), model2.getStatus());
        assertEquals(evaluation.getMinutesSpent(), model2.getMinutesSpent());
        assertEquals(evaluation.getDateStarted(), model2.getDateStarted());
        assertEquals(evaluation.getDateCompleted(), model2.getDateCompleted());
        assertEquals(evaluation.getDateUpdated(), model2.getDateUpdated());
        assertEquals(evaluation.getAttachments(), model2.getAttachments());
        assertEquals(evaluation.getAttempt(), model2.getAttempt());
        assertEquals(evaluation.getStudentId(), model2.getStudentId());
        assertEquals(evaluation.getTaskId(), model2.getTaskId());
        assertEquals(evaluation.getEvaluatorFirstName(), model2.getEvaluatorFirstName());
        assertEquals(evaluation.getEvaluatorLastName(), model2.getEvaluatorLastName());
        assertEquals(evaluation.getScoreReport().getName(), model2.getScoreReport().getName());
    }

}