package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorConfirmation;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorDashboard;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorWorkspace;
import edu.wgu.dmadmin.domain.submission.Referral;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.service.EvaluatorService;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationControllerTest {
    @InjectMocks
    private EvaluationController evaluationController = new EvaluationController();

    @Mock
    private EvaluatorService evaluatorService;

    @Mock
    private IdentityUtil iUtil;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    private String userId = "123456";
    private String evaluatorId = "evaluator";
    private UUID submissionId = UUID.randomUUID();
    private UUID commentId = UUID.randomUUID();
    private UUID taskId = UUID.randomUUID();
    private TaskModel taskModel;
    private EvaluationModel evaluationModel;
    private SubmissionModel submissionModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(this.evaluationController).build();

        when(this.iUtil.getUserId()).thenReturn(this.evaluatorId);

        Comment comment = new Comment();
        comment.setCommentId(this.commentId);
        comment.setAttempt(1);
        comment.setComments("testing");
        comment.setDateCreated(DateUtil.getZonedNow());
        comment.setFirstName("Test");
        comment.setLastName("User");
        comment.setScore(2);
        comment.setType(CommentTypes.STUDENT);
        comment.setUserId(this.userId);

        String evaluatorLastName = "Grayson";
        String evaluatorFirstName = "Dick";
        this.submissionModel = TestObjectFactory.getSubmissionModel(this.submissionId, StatusUtil.EVALUATION_BEGUN, this.taskId, this.userId, evaluatorFirstName, evaluatorLastName, this.evaluatorId);
        UserModel userModel = TestObjectFactory.getUserModel();
        this.evaluationModel = TestObjectFactory.getEvaluationModel(userModel, this.submissionModel);
        this.taskModel = TestObjectFactory.getTaskModel();


    }

    @Test
    public void testGetEvaluatorDashboard() throws Exception {

        EvaluatorDashboard evaluatorDashboard = new EvaluatorDashboard("Dick", "Grayson");

        when(this.evaluatorService.getEvaluatorDashboard(this.evaluatorId)).thenReturn(evaluatorDashboard);

        MvcResult result = this.mockMvc.perform(get("/v1/evaluator/dashboard")).andExpect(status().isOk()).andReturn();

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.evaluatorService).getEvaluatorDashboard(arg1.capture());
        assertEquals(this.evaluatorId, arg1.getValue());

        assertTrue(result.getResponse().getContentAsString().contains("Dick"));
    }

    @Test
    public void testGetEvaluatorWorkspace() throws Exception {
        EvaluatorWorkspace evaluatorWorkspace = new EvaluatorWorkspace(this.submissionModel, this.taskModel, this.evaluationModel);
        evaluatorWorkspace.setPreviousSubmissions(new ArrayList<>());

        when(this.evaluatorService.getEvaluatorWorkspace(this.submissionId)).thenReturn(evaluatorWorkspace);

        MvcResult result = this.mockMvc.perform(get("/v1/evaluator/submissions/" + this.submissionId + "/workspace")).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.evaluatorService).getEvaluatorWorkspace(arg1.capture());
        assertEquals(this.submissionId, arg1.getValue());

        assertTrue(result.getResponse().getContentAsString().contains("evaluatorWorkspace"));
    }

    @Test
    public void testGetEvaluatorWorkspace2() throws Exception {
        EvaluatorWorkspace evaluatorWorkspace = new EvaluatorWorkspace(this.submissionModel, this.taskModel, this.evaluationModel);
        evaluatorWorkspace.setPreviousSubmissions(new ArrayList<>());

        when(this.evaluatorService.getEvaluatorWorkspace(this.submissionId)).thenReturn(evaluatorWorkspace);

        MvcResult result = this.mockMvc.perform(get("/v1/evaluator/workspace/" + this.submissionId)).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.evaluatorService).getEvaluatorWorkspace(arg1.capture());
        assertEquals(this.submissionId, arg1.getValue());

        assertTrue(result.getResponse().getContentAsString().contains("evaluatorWorkspace"));
    }

    @Test
    public void testGetEvaluatorConfirmation() throws Exception {
        EvaluatorConfirmation confirmation = new EvaluatorConfirmation(this.submissionModel, this.taskModel.getRubric(), this.evaluationModel);

        when(this.evaluatorService.getEvaluatorConfirmation(this.submissionId)).thenReturn(confirmation);

        MvcResult result = this.mockMvc.perform(get("/v1/evaluator/submissions/" + this.submissionId + "/review")).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.evaluatorService).getEvaluatorConfirmation(arg1.capture());
        assertEquals(this.submissionId, arg1.getValue());

        assertTrue(result.getResponse().getContentAsString().contains("evaluatorConfirmation"));
    }

    @Test
    public void testGetEvaluatorConfirmation2() throws Exception {
        EvaluatorConfirmation confirmation = new EvaluatorConfirmation(this.submissionModel, this.taskModel.getRubric(), this.evaluationModel);

        when(this.evaluatorService.getEvaluatorConfirmation(this.submissionId)).thenReturn(confirmation);

        MvcResult result = this.mockMvc.perform(get("/v1/evaluator/review/" + this.submissionId)).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.evaluatorService).getEvaluatorConfirmation(arg1.capture());
        assertEquals(this.submissionId, arg1.getValue());

        assertTrue(result.getResponse().getContentAsString().contains("evaluatorConfirmation"));
    }

    @Test
    public void testClaimSubmission() throws Exception {
        String url = "/v1/evaluator/submissions/" + this.submissionId + "/claim";

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        doNothing().when(this.evaluatorService).claimSubmission(this.userId, this.submissionId);
        this.mockMvc.perform(post(url))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        verify(this.evaluatorService).claimSubmission(arg1.capture(), arg2.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());

    }

    @Test
    public void testClaimSubmission2() throws Exception {
        String url = "/v1/evaluator/claim/" + this.submissionId;

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        doNothing().when(this.evaluatorService).claimSubmission(this.userId, this.submissionId);
        this.mockMvc.perform(post(url))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        verify(this.evaluatorService).claimSubmission(arg1.capture(), arg2.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());

    }

    @Test
    public void testCancelEvaluation() throws Exception {
        String comments = "hi there";
        String url = "/v1/evaluator/submissions/" + this.submissionId + "/cancel";

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.cancelEvaluation(this.userId, this.submissionId, comments)).thenReturn("Cancelled");

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(comments))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("Cancelled", result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);

        verify(this.evaluatorService).cancelEvaluation(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(comments, arg3.getValue());
    }

    @Test
    public void testCancelEvaluation2() throws Exception {
        String comments = "hi there";
        String url = "/v1/evaluator/cancel/" + this.submissionId;

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.cancelEvaluation(this.userId, this.submissionId, comments)).thenReturn("Cancelled");

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(comments))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("Cancelled", result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);

        verify(this.evaluatorService).cancelEvaluation(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(comments, arg3.getValue());
    }

    @Test
    public void testReleaseScoreReport() throws Exception {
        String url = "/v1/evaluator/submissions/" + this.submissionId + "/release/" + false;
        Comment comment = new Comment();
        comment.setComments("Looks good");
        comment.setUserId(this.evaluatorId);


        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.releaseEvaluation(this.userId, this.submissionId, false, comment)).thenReturn("16");

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("16", result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Boolean> arg3 = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);

        verify(this.evaluatorService).releaseEvaluation(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertFalse(arg3.getValue());
        assertEquals(comment.toString(), arg4.getValue().toString());
        assertEquals(comment.getComments(), arg4.getValue().getComments());
    }

    @Test
    public void testReleaseScoreReport2() throws Exception {
        String url = "/v1/evaluator/release/" + this.submissionId + "/retry/" + false;
        Comment comment = new Comment();
        comment.setComments("Looks good");
        comment.setUserId(this.evaluatorId);


        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.releaseEvaluation(this.userId, this.submissionId, false, comment)).thenReturn("16");

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("16", result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Boolean> arg3 = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);

        verify(this.evaluatorService).releaseEvaluation(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertFalse(arg3.getValue());
        assertEquals(comment.toString(), arg4.getValue().toString());
        assertEquals(comment.getComments(), arg4.getValue().getComments());
    }

    @Test
    public void testSaveAspectScore() throws Exception {
        String aspectName = "A2";
        Integer score = 2;
        String url = "/v1/evaluator/submissions/" + this.submissionId + "/score/" + aspectName + "/" + score;
        Comment comment = new Comment();
        comment.setComments("Looks good");
        comment.setUserId(this.evaluatorId);


        when(this.iUtil.getUserId()).thenReturn(this.userId);

        doNothing().when(this.evaluatorService).saveAspectScore(this.userId, this.submissionId, aspectName, score);

        MvcResult result = this.mockMvc.perform(post(url))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> arg4 = ArgumentCaptor.forClass(Integer.class);

        verify(this.evaluatorService).saveAspectScore(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(aspectName, arg3.getValue());
        assertEquals(score, arg4.getValue());
    }

    @Test
    public void testSaveAspectScore2() throws Exception {
        String aspectName = "A2";
        Integer score = 2;
        String url = "/v1/evaluator/score/" + this.submissionId + "/aspect/" + aspectName + "/score/" + score;
        Comment comment = new Comment();
        comment.setComments("Looks good");
        comment.setUserId(this.evaluatorId);


        when(this.iUtil.getUserId()).thenReturn(this.userId);

        doNothing().when(this.evaluatorService).saveAspectScore(this.userId, this.submissionId, aspectName, score);

        MvcResult result = this.mockMvc.perform(post(url))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> arg4 = ArgumentCaptor.forClass(Integer.class);

        verify(this.evaluatorService).saveAspectScore(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(aspectName, arg3.getValue());
        assertEquals(score, arg4.getValue());
    }

    @Test
    public void testSaveAspectComment() throws Exception {
        String aspectName = "A2";
        String url = "/v1/evaluator/submissions/" + this.submissionId + "/comment/" + aspectName;
        Comment comment = new Comment();
        comment.setComments("Looks good");
        comment.setUserId(this.evaluatorId);

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.saveAspectComment(this.userId, this.submissionId, aspectName, comment)).thenReturn(comment);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(comment), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);

        verify(this.evaluatorService).saveAspectComment(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(aspectName, arg3.getValue());
        assertEquals(comment.toString(), arg4.getValue().toString());
        assertEquals(comment.getComments(), arg4.getValue().getComments());
    }

    @Test
    public void testSaveAspectComment2() throws Exception {
        String aspectName = "A2";
        String url = "/v1/evaluator/comment/" + this.submissionId + "/aspect/" + aspectName;
        Comment comment = new Comment();
        comment.setComments("Looks good");
        comment.setUserId(this.evaluatorId);

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.saveAspectComment(this.userId, this.submissionId, aspectName, comment)).thenReturn(comment);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(comment), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);

        verify(this.evaluatorService).saveAspectComment(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(aspectName, arg3.getValue());
        assertEquals(comment.toString(), arg4.getValue().toString());
        assertEquals(comment.getComments(), arg4.getValue().getComments());
    }

    @Test
    public void testSaveReportComment() throws Exception {
        String url = "/v1/evaluator/submissions/" + this.submissionId + "/comment/";
        Comment comment = new Comment();
        comment.setComments("Looks good");
        comment.setUserId(this.evaluatorId);

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.saveReportComment(this.userId, this.submissionId, comment)).thenReturn(comment);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(comment), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Comment> arg3 = ArgumentCaptor.forClass(Comment.class);

        verify(this.evaluatorService).saveReportComment(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(comment.toString(), arg3.getValue().toString());
        assertEquals(comment.getComments(), arg3.getValue().getComments());
    }

    @Test
    public void testSaveReportComment2() throws Exception {
        String url = "/v1/evaluator/comment/" + this.submissionId;
        Comment comment = new Comment();
        comment.setComments("Looks good");
        comment.setUserId(this.evaluatorId);

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.saveReportComment(this.userId, this.submissionId, comment)).thenReturn(comment);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(comment), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Comment> arg3 = ArgumentCaptor.forClass(Comment.class);

        verify(this.evaluatorService).saveReportComment(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(comment.toString(), arg3.getValue().toString());
        assertEquals(comment.getComments(), arg3.getValue().getComments());
    }

    @Test
    public void testSaveReferral() throws Exception {
        String url = "/v1/evaluator/submissions/" + this.submissionId + "/refer";
        String createdBy = "Batman";
        String creatorComments = "this needs to be referred";

        List<Referral> referrals = new ArrayList<>();

        Referral referral = new Referral();
        referral.setCreatedBy(createdBy);
        referral.setCreatorComments(creatorComments);

        referrals.add(referral);

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.saveReferral(this.userId, this.submissionId, referral)).thenReturn(referrals);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(referral)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(referrals), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Referral> arg3 = ArgumentCaptor.forClass(Referral.class);

        verify(this.evaluatorService).saveReferral(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(referral.toString(), arg3.getValue().toString());
        assertEquals(referral.getCreatedBy(), arg3.getValue().getCreatedBy());
    }

    @Test
    public void testSaveReferral2() throws Exception {
        String url = "/v1/evaluator/refer/" + this.submissionId;
        String createdBy = "Batman";
        String creatorComments = "this needs to be referred";

        List<Referral> referrals = new ArrayList<>();

        Referral referral = new Referral();
        referral.setCreatedBy(createdBy);
        referral.setCreatorComments(creatorComments);

        referrals.add(referral);

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        when(this.evaluatorService.saveReferral(this.userId, this.submissionId, referral)).thenReturn(referrals);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(referral)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(referrals), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Referral> arg3 = ArgumentCaptor.forClass(Referral.class);

        verify(this.evaluatorService).saveReferral(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(referral.toString(), arg3.getValue().toString());
        assertEquals(referral.getCreatedBy(), arg3.getValue().getCreatedBy());
    }

}