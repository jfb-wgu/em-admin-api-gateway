package edu.wgu.dmadmin.model.assessment;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EvaluationModelTest {
    private EvaluationModel model;

    @Before
    public void setUp() throws Exception {
        this.model = new EvaluationModel();
    }

    @Test
    public void testGetScoreReport() throws Exception {
        String scoreReportName = "This is a name";
        ScoreReportModel scoreReport = new ScoreReportModel();
        scoreReport.setName(scoreReportName);
        this.model.setScoreReport(scoreReport);

        assertEquals(scoreReport, this.model.getScoreReport());
        assertEquals(scoreReport.getName(), this.model.getScoreReport().getName());
    }

    @Test
    public void testGetScoreReportNull() throws Exception {
        assertNull(this.model.getScoreReport().getName());
    }

    @Test
    public void testImportScoreReport() throws Exception {
        ScoreReportModel scoreReportModel = TestObjectFactory.getScoreReportModel("Test", "test");
        this.model.importScoreReport(scoreReportModel);

        assertEquals(scoreReportModel, this.model.getScoreReport());
        assertEquals(scoreReportModel.getName(), this.model.getScoreReport().getName());
    }

    @Test
    public void testImportScoreReport2() throws Exception {
        ScoreReportModel scoreReportModel = TestObjectFactory.getScoreReportModel("Test", "test");
        scoreReportModel.getScores().get("1").setAssignedScore(0);
        this.model.importScoreReport(scoreReportModel);

        assertEquals(scoreReportModel, this.model.getScoreReport());
        assertEquals(scoreReportModel.getName(), this.model.getScoreReport().getName());
        assertEquals(-1, this.model.getScoreReport().getScores().get("1").getAssignedScore());
        assertEquals(-1, this.model.getScoreReport().getScores().get("1").getPreviousScore());
    }

    @Test
    public void testAssignScoreReport() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String userId = TestObjectFactory.getUserId();

        UserModel user = new UserModel();
        user.setUserId(userId);

        SubmissionModel sub = new SubmissionModel();
        sub.setTaskId(taskId);

        EvaluationModel current = new EvaluationModel();
        current.setEvaluationId(evaluationId);

        ScoreReportModel scoreReport = TestObjectFactory.getScoreReportModel("Name1", "Description 1");
        current.setScoreReport(scoreReport);

        Map<UUID, String> orgScoreComments = new HashMap<>();
        Map<UUID, String> newScoreComments = new HashMap<>();

        getOrgScoreComments(scoreReport, orgScoreComments);

        this.model.assignScoreReport(current, sub, user);

        assertEquals(scoreReport, this.model.getScoreReport());

        getNewScoreComments(newScoreComments);

        assertEquals(orgScoreComments, newScoreComments);

        assertCommentsTheSame(orgScoreComments, newScoreComments);

    }

    @Test
    public void testAssignScoreReport2() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String userId = TestObjectFactory.getUserId();
        String evaluatorId = TestObjectFactory.getEvaluatorId();
        ScoreReportModel scoreReport = TestObjectFactory.getScoreReportModel("Name1", "Description 1");

        Map<UUID, String> orgScoreComments = new HashMap<>();
        Map<UUID, String> newScoreComments = new HashMap<>();

        getOrgScoreComments(scoreReport, orgScoreComments);

        UserModel user = new UserModel();
        user.setUserId(userId);

        SubmissionModel sub = new SubmissionModel();
        sub.setTaskId(taskId);
        sub.setAttempt(1);

        EvaluationModel current = new EvaluationModel();
        current.setEvaluationId(evaluationId);
        current.setScoreReport(scoreReport);
        current.setEvaluatorId(evaluatorId);

        this.model.setTaskId(taskId);
        this.model.assignScoreReport(current, sub, user);

        assertEquals(scoreReport, this.model.getScoreReport());
        assertEquals(sub.getTaskId(), this.model.getTaskId());
        assertTrue(this.model.getScoreReport().getComments().isEmpty());

        getNewScoreComments(newScoreComments);

        assertNotEquals(orgScoreComments, newScoreComments);

        assertCommentsAreDifferent(orgScoreComments, newScoreComments);
    }


    @Test
    public void testAssignScoreReport3() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String userId = TestObjectFactory.getUserId();
        String evaluatorId = TestObjectFactory.getEvaluatorId();
        Map<UUID, CommentModel> comments = TestObjectFactory.getCommentsMap(3);

        ScoreReportModel scoreReport = TestObjectFactory.getScoreReportModel("Name1", "Description 1");
        scoreReport.setComments(comments);

        Map<UUID, String> orgScoreComments = new HashMap<>();
        Map<UUID, String> newScoreComments = new HashMap<>();

        Map<UUID, String> orgComments = new HashMap<>();
        Map<UUID, String> newComments = new HashMap<>();

        getOrgScoreComments(scoreReport, orgScoreComments);

        getOrgComments(comments, orgComments);

        UserModel user = new UserModel();
        user.setUserId(userId);

        SubmissionModel sub = new SubmissionModel();
        sub.setTaskId(taskId);
        sub.setAttempt(1);

        EvaluationModel current = new EvaluationModel();
        current.setEvaluationId(evaluationId);
        current.setScoreReport(scoreReport);
        current.setEvaluatorId(evaluatorId);

        this.model.setTaskId(taskId);
        this.model.assignScoreReport(current, sub, user);

        assertEquals(scoreReport, this.model.getScoreReport());
        assertEquals(sub.getTaskId(), this.model.getTaskId());

        getNewScoreComments(newScoreComments);

        getNewComments(newComments);

        assertNotEquals(orgScoreComments, newScoreComments);
        assertNotEquals(orgComments, newComments);

        assertCommentsAreDifferent(orgScoreComments, newScoreComments);

        assertCommentsAreDifferent(orgComments, newComments);

    }

    @Test
    public void testAssignScoreReport4() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String userId = TestObjectFactory.getUserId() + "UID";
        String evaluatorId = TestObjectFactory.getEvaluatorId();

        ScoreReportModel scoreReport = TestObjectFactory.getScoreReportModel("Name1", "Description 1");
        Map<UUID, CommentModel> comments = TestObjectFactory.getCommentsMap(3);
        scoreReport.setComments(comments);

        Map<UUID, String> orgScoreComments = new HashMap<>();
        Map<UUID, String> newScoreComments = new HashMap<>();

        Map<UUID, String> orgComments = new HashMap<>();
        Map<UUID, String> newComments = new HashMap<>();

        getOrgScoreComments(scoreReport, orgScoreComments);

        getOrgComments(comments, orgComments);

        UserModel user = new UserModel();
        user.setUserId(userId);

        SubmissionModel sub = new SubmissionModel();
        sub.setTaskId(taskId);
        sub.setAttempt(1);

        EvaluationModel current = new EvaluationModel();
        current.setEvaluationId(evaluationId);
        current.setScoreReport(scoreReport);
        current.setEvaluatorId(evaluatorId);

        this.model.setTaskId(taskId);
        this.model.assignScoreReport(current, sub, user);

        assertEquals(scoreReport, this.model.getScoreReport());

        getNewScoreComments(newScoreComments);
        getNewComments(newComments);

        assertNotEquals(orgScoreComments, newScoreComments);
        assertNotEquals(orgComments, newComments);

        assertCommentsAreDifferent(orgScoreComments, newScoreComments);

        assertCommentsAreDifferent(orgComments, newComments);
    }


    @Test
    public void testAssignScoreReport5() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String userId = TestObjectFactory.getUserId();
        String evaluatorId = TestObjectFactory.getEvaluatorId() + "UID";

        ScoreReportModel scoreReport = TestObjectFactory.getScoreReportModel("Name1", "Description 1");
        Map<UUID, CommentModel> comments = TestObjectFactory.getCommentsMap(3);
        scoreReport.setComments(comments);

        Map<UUID, String> orgScoreComments = new HashMap<>();
        Map<UUID, String> newScoreComments = new HashMap<>();

        Map<UUID, String> orgComments = new HashMap<>();
        Map<UUID, String> newComments = new HashMap<>();

        getOrgScoreComments(scoreReport, orgScoreComments);

        getOrgComments(comments, orgComments);

        UserModel user = new UserModel();
        user.setUserId(userId);

        SubmissionModel sub = new SubmissionModel();
        sub.setTaskId(taskId);
        sub.setAttempt(1);

        EvaluationModel current = new EvaluationModel();
        current.setEvaluationId(evaluationId);
        current.setScoreReport(scoreReport);
        current.setEvaluatorId(evaluatorId);

        this.model.setTaskId(taskId);
        this.model.assignScoreReport(current, sub, user);

        getNewScoreComments(newScoreComments);
        getNewComments(newComments);

        assertEquals(orgScoreComments, newScoreComments);

        assertCommentsTheSame(orgScoreComments, newScoreComments);

        assertCommentsTheSame(orgComments, newComments);

    }


    @Test
    public void testAssignScoreReport6() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String userId = TestObjectFactory.getUserId();
        String evaluatorId = TestObjectFactory.getEvaluatorId() + "UID";

        ScoreReportModel scoreReport = TestObjectFactory.getScoreReportModel("Name1", "Description 1");
        Map<UUID, CommentModel> comments = TestObjectFactory.getCommentsMap(3);
        scoreReport.setComments(comments);

        Map<UUID, String> orgScoreComments = new HashMap<>();
        Map<UUID, String> newScoreComments = new HashMap<>();

        Map<UUID, String> orgComments = new HashMap<>();
        Map<UUID, String> newComments = new HashMap<>();

        getOrgScoreComments(scoreReport, orgScoreComments);

        getOrgComments(comments, orgComments);

        UserModel user = new UserModel();
        user.setUserId(userId);

        SubmissionModel sub = new SubmissionModel();
        sub.setTaskId(taskId);
        sub.setAttempt(2);

        EvaluationModel current = new EvaluationModel();
        current.setEvaluationId(evaluationId);
        current.setScoreReport(scoreReport);
        current.setEvaluatorId(evaluatorId);

        this.model.setTaskId(taskId);
        this.model.assignScoreReport(current, sub, user);

        getNewScoreComments(newScoreComments);
        getNewComments(newComments);

        assertEquals(orgScoreComments, newScoreComments);

        assertCommentsTheSame(orgScoreComments, newScoreComments);

        assertCommentsTheSame(orgComments, newComments);

    }

    @Test
    public void testComplete() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        Date dateStarted = cal.getTime();

        String status = StatusUtil.COMPLETED;

        this.model.setDateStarted(dateStarted);
        this.model.complete(status);

        int dateDifference = Math.toIntExact(TimeUnit.MILLISECONDS.toMinutes(this.model.getDateCompleted().getTime() - this.model.getDateStarted().getTime()));

        assertEquals(status, this.model.getStatus());
        assertTrue(dateStarted.before(this.model.getDateCompleted()));
        assertEquals(dateDifference, this.model.getMinutesSpent());

    }

    @Test
    public void testPopulate() throws Exception {
        String employeeid = "employeeid";
        String firstName = "Bruce";
        String lastName = "Wayne";
        String userId = "userId";
        Set<UUID> roles = new HashSet<>();
        roles.add(UUID.randomUUID());
        Set<String> permissions = new HashSet<>();
        permissions.add("Permission 1");
        Set<UUID> tasks = new HashSet<>();
        tasks.add(UUID.randomUUID());
        Set<String> landings = new HashSet<>();
        landings.add("Landing");

        UserModel user = TestObjectFactory.getUserModel(firstName, lastName, userId, roles, permissions, tasks, landings, employeeid);
        String evaluationBegun = StatusUtil.EVALUATION_BEGUN;
        String evaluationReleased = StatusUtil.EVALUATION_RELEASED;
        SubmissionModel submissionModel = TestObjectFactory.getSubmissionModel(evaluationBegun);

        EvaluationModel evalModel = TestObjectFactory.getEvaluationModel(user, submissionModel);

        this.model.populate(evalModel);

        assertEquals(evalModel.getEvaluationId(), this.model.getEvaluationId());
        assertEquals(userId, this.model.getEvaluatorId());
        assertEquals(submissionModel.getSubmissionId(), this.model.getSubmissionId());
        assertEquals(evaluationReleased, this.model.getStatus());
        assertEquals(evalModel.getMinutesSpent(), this.model.getMinutesSpent());
        assertNotNull(this.model.getDateStarted());
        assertNull(this.model.getDateUpdated());
        assertEquals(evalModel.getAttachments(), this.model.getAttachments());
        assertEquals(submissionModel.getTaskId(), this.model.getTaskId());
        assertEquals(submissionModel.getStudentId(), this.model.getStudentId());
        assertEquals(evalModel.getAttempt(), this.model.getAttempt());
        assertEquals(evalModel.getScoreReport(), this.model.getScoreReport());
        assertEquals(firstName, this.model.getEvaluatorFirstName());
        assertEquals(lastName, this.model.getEvaluatorLastName());
    }

    @Test
    public void testGetEvaluationId() throws Exception {
        UUID evaluationId = UUID.randomUUID();
        model.setEvaluationId(evaluationId);

        assertEquals(evaluationId, model.getEvaluationId());
    }

    @Test
    public void testGetEvaluatorId() throws Exception {
        String id = "ID";
        model.setEvaluatorId(id);

        assertEquals(id, model.getEvaluatorId());

    }

    @Test
    public void testGetSubmissionId() throws Exception {
        UUID submissionId = UUID.randomUUID();
        model.setSubmissionId(submissionId);

        assertEquals(submissionId, model.getSubmissionId());
    }

    @Test
    public void testGetStatus() throws Exception {
        String evaluationCancelled = StatusUtil.EVALUATION_CANCELLED;
        model.setStatus(evaluationCancelled);

        assertEquals(evaluationCancelled, model.getStatus());
    }

    @Test
    public void testGetMinutesSpent() throws Exception {
        int minutesSpent = 234;
        model.setMinutesSpent(minutesSpent);

        assertEquals(minutesSpent, model.getMinutesSpent());
    }

    @Test
    public void testGetDateStarted() throws Exception {
        Date dateStarted = new Date();
        model.setDateStarted(dateStarted);

        assertEquals(dateStarted, model.getDateStarted());
    }

    @Test
    public void testGetDateCompleted() throws Exception {
        Date dateCompleted = new Date();
        model.setDateCompleted(dateCompleted);

        assertEquals(dateCompleted, model.getDateCompleted());
    }

    @Test
    public void testGetDateUpdated() throws Exception {
        Date dateUpdated = new Date();
        model.setDateUpdated(dateUpdated);

        assertEquals(dateUpdated, model.getDateUpdated());
    }

    @Test
    public void testGetAttachments() throws Exception {
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        String title = "Attachment 1";
        attachment.setTitle(title);
        attachments.add(attachment);

        model.setAttachments(attachments);

        assertEquals(title, model.getAttachments().get(0).getTitle());
    }

    @Test
    public void testGetTaskId() throws Exception {
        model.setTaskId(UUID.randomUUID());
    }

    @Test
    public void testGetStudentId() throws Exception {
    }

    @Test
    public void testGetAttempt() throws Exception {
    }

    @Test
    public void testGetEvaluatorFirstName() throws Exception {
    }

    @Test
    public void testGetEvaluatorLastName() throws Exception {
    }

    private void assertCommentsAreDifferent(Map<UUID, String> orgScoreComments, Map<UUID, String> newScoreComments) {
        while (newScoreComments.keySet().iterator().hasNext()) {
            UUID key = newScoreComments.keySet().iterator().next();
            for (UUID id : newScoreComments.keySet()) {
                if (orgScoreComments.containsKey(id)) {
                    assertNotEquals(orgScoreComments.get(id), newScoreComments.get(id));
                    assertEquals(CommentTypes.STUDENT, orgScoreComments.get(id));
                    assertEquals(CommentTypes.INTERNAL, newScoreComments.get(id));
                }
            }
            newScoreComments.remove(key);
        }
    }

    private void assertCommentsTheSame(Map<UUID, String> orgScoreComments, Map<UUID, String> newScoreComments) {
        while (newScoreComments.keySet().iterator().hasNext()) {
            UUID key = newScoreComments.keySet().iterator().next();
            for (UUID id : newScoreComments.keySet()) {
                if (orgScoreComments.containsKey(id)) {
                    assertEquals(orgScoreComments.get(id), newScoreComments.get(id));
                    assertEquals(CommentTypes.STUDENT, orgScoreComments.get(id));
                    assertEquals(CommentTypes.STUDENT, newScoreComments.get(id));
                }
            }
            newScoreComments.remove(key);
        }
    }

    private void getNewComments(Map<UUID, String> newComments) {
        this.model.getScoreReport().getComments().forEach((uuid, comment) -> newComments.put(uuid, comment.getType()));
    }

    private void getOrgComments(Map<UUID, CommentModel> comments, Map<UUID, String> orgComments) {
        comments.forEach((uuid, commentModel) -> orgComments.put(uuid, commentModel.getType()));
    }

    private void getNewScoreComments(Map<UUID, String> newScoreComments) {
        this.model.getScoreReport().getScores().forEach((name, score) -> {
            List<CommentModel> scoreComments = new ArrayList<>(score.getComments().values());

            for (CommentModel comment : scoreComments) {
                newScoreComments.put(comment.getCommentId(), comment.getType());
            }
        });
    }

    private void getOrgScoreComments(ScoreReportModel scoreReport, Map<UUID, String> orgScoreComments) {
        scoreReport.getScores().forEach((name, score) -> score.getComments().values().forEach(c -> {
            orgScoreComments.put(c.getCommentId(), c.getType());
        }));
    }
}