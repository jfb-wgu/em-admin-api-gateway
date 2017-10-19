package edu.wgu.dmadmin.model.submission;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SubmissionModelTest {
    SubmissionModel submissionModel;

    private String userId = "123456";
    private UUID evaluationId = UUID.randomUUID();
    private String lastName = "bob";
    private String firstName = "jim";
    private Date dateStarted = new Date();
    private String statusWorkResubmitted = StatusUtil.AUTHOR_WORK_RESUBMITTED;
    private String statusCancelled = StatusUtil.EVALUATION_CANCELLED;

    @Before
    public void setUp() throws Exception {
        submissionModel = new SubmissionModel();
    }

    @Test
    public void testGetAttachmentsNS() throws Exception {
        assertEquals(0, submissionModel.getAttachmentsNS().size());
    }

    @Test
    public void testGetAttachmentsNSNotNull() throws Exception {
        Map<String, AttachmentModel> attachments = new HashMap<>();
        AttachmentModel attachment = new AttachmentModel();
        attachments.put("test", attachment);
        submissionModel.setAttachments(attachments);
        assertEquals(1, submissionModel.getAttachmentsNS().size());
    }

    @Test
    public void testGetReferralsNS() throws Exception {
        assertEquals(0, submissionModel.getReferralsNS().size());
    }

    @Test
    public void testGetReferralsNSNotNull() throws Exception {
        List<ReferralModel> referralModels = new ArrayList<>();
        ReferralModel referralModel = new ReferralModel();
        referralModels.add(referralModel);
        submissionModel.setReferrals(referralModels);

        assertEquals(1, submissionModel.getReferralsNS().size());
    }

    @Test
    public void testGetInternalCommentsNS() throws Exception {
        assertEquals(0, submissionModel.getInternalCommentsNS().size());
    }

    @Test
    public void testGetInternalCommentsNSNotNull() throws Exception {
        Map<UUID, CommentModel> internalComments = new HashMap<>();
        internalComments.put(UUID.randomUUID(), new CommentModel());
        submissionModel.setInternalComments(internalComments);
        assertEquals(1, submissionModel.getInternalCommentsNS().size());
    }

    @Test
    public void testSetEvaluation() throws Exception {
        UserModel user = setUpUserModel(userId, lastName, firstName);

        EvaluationModel evaluation = setUpEvaluationModel(evaluationId, dateStarted);

        setUpEvaluation(statusWorkResubmitted, user, evaluation);

        assertEquals("Status didn't get set properly.", statusWorkResubmitted, submissionModel.getStatus());
        assertEquals("UserId didn't get set properly.", userId, submissionModel.getEvaluatorId());
        assertEquals("evaluationId didn't get set properly.", evaluationId, submissionModel.getEvaluationId());
        assertEquals("dateStarted didn't get set properly.", dateStarted, submissionModel.getDateStarted());
        assertEquals("firstName didn't set set properly.", firstName, submissionModel.getEvaluatorFirstName());
        assertEquals("lastName didn't get set properly.", lastName, submissionModel.getEvaluatorLastName());
    }


    @Test
    public void testCancelEvaluation() throws Exception {
        UserModel user = setUpUserModel(userId, lastName, firstName);

        EvaluationModel evaluation = setUpEvaluationModel(evaluationId, dateStarted);

        setUpEvaluation(statusWorkResubmitted, user, evaluation);

        submissionModel.cancelEvaluation();

        assertEquals("submission status should be canceled", statusCancelled, submissionModel.getStatus());
        assertNull("evaluatior id should be null", submissionModel.getEvaluatorId());
        assertNull("evaluationId should be null", submissionModel.getEvaluationId());
        assertNull("dateStarted should be null", submissionModel.getDateStarted());
        assertNull("firstName should be null", submissionModel.getEvaluatorFirstName());
        assertNull("lastName should be null", submissionModel.getEvaluatorLastName());
    }

    @Test
    public void testPopulate() throws Exception {
        SubmissionModel model = TestObjectFactory.getSubmissionModel(statusCancelled);
        submissionModel.populate(model);

        assertEquals(statusCancelled, submissionModel.getStatus());
    }


    private void setUpEvaluation(String status, UserModel user, EvaluationModel evaluation) {
        submissionModel.setEvaluation(status, user, evaluation);
    }

    private static EvaluationModel setUpEvaluationModel(UUID evaluationId, Date dateStarted) {
        EvaluationModel evaluation = new EvaluationModel();
        evaluation.setEvaluationId(evaluationId);
        evaluation.setDateStarted(dateStarted);
        return evaluation;
    }

    private static UserModel setUpUserModel(String userId, String lastName, String firstName) {
        UserModel user = new UserModel();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}