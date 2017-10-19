package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorConfirmation;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorWorkspace;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.service.EvaluationAdminService;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.dmadmin.util.StatusUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.UUID;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationAdminControllerTest {
    private EvaluationAdminController evaluationAdminController = new EvaluationAdminController();

    private EvaluationAdminService adminService = mock(EvaluationAdminService.class);
    private IdentityUtil iUtil = mock(IdentityUtil.class);

    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    private String userId = "123456";
    private String evaluatorId = "evaluator";
    private UUID submissionId = UUID.randomUUID();
    private UUID commentId = UUID.randomUUID();
    private UUID taskId = UUID.randomUUID();
    private Comment comment;
    private TaskModel taskModel;
    private EvaluationModel evaluationModel;
    private SubmissionModel submissionModel;

    @Before
    public void setUp() throws Exception {
        this.evaluationAdminController.setIdentityUtil(this.iUtil);
        this.evaluationAdminController.setEvaluationAdminService(this.adminService);

        this.mockMvc = standaloneSetup(this.evaluationAdminController).build();

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        this.comment = new Comment();
        this.comment.setCommentId(this.commentId);
        this.comment.setAttempt(1);
        this.comment.setComments("testing");
        this.comment.setDateCreated(DateUtil.getZonedNow());
        this.comment.setFirstName("Test");
        this.comment.setLastName("User");
        this.comment.setScore(2);
        this.comment.setType(CommentTypes.STUDENT);
        this.comment.setUserId(this.userId);

        String evaluatorLastName = "Grayson";
        String evaluatorFirstName = "Dick";
        this.submissionModel = TestObjectFactory.getSubmissionModel(this.submissionId, StatusUtil.EVALUATION_BEGUN, this.taskId, this.userId, evaluatorFirstName, evaluatorLastName, this.evaluatorId);
        UserModel userModel = TestObjectFactory.getUserModel();
        this.evaluationModel = TestObjectFactory.getEvaluationModel(userModel, this.submissionModel);
        this.taskModel = TestObjectFactory.getTaskModel();


    }

    @Test
    public void assignEvaluator() throws Exception {

        this.mockMvc.perform(post("/v1/admin/submissions/" + this.submissionId + "/assign/" + this.evaluatorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(this.comment)))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg3 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);

        Mockito.verify(this.adminService).assignEvaluation(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.evaluatorId, arg2.getValue());
        assertEquals(this.submissionId, arg3.getValue());
        assertEquals(this.commentId, arg4.getValue().getCommentId());
    }

    @Test
    public void assignEvaluatorNoComment() throws Exception {

        this.mockMvc.perform(post("/v1/admin/submissions/" + this.submissionId + "/assign/" + this.evaluatorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void assignEvaluatorNoSubId() throws Exception {

        this.mockMvc.perform(post("/v1/admin/submissions/" + StringUtils.EMPTY + "/assign/" + this.evaluatorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void assignEvaluatorNoAssignee() throws Exception {

        this.mockMvc.perform(post("/v1/admin/submissions/" + this.submissionId + "/assign/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void testCancelEvaluation() throws Exception {

        this.mockMvc.perform(post("/v1/admin/submissions/" + this.submissionId + "/cancel")
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(this.comment)))
                .andExpect(status().isNoContent())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Comment> arg3 = ArgumentCaptor.forClass(Comment.class);

        Mockito.verify(this.adminService).cancelEvaluation(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(this.comment, arg3.getValue());
    }

    @Test
    public void testReleaseEvaluation() throws Exception {
        when(this.adminService.releaseEvaluation(this.userId, this.submissionId, false, this.comment)).thenReturn("64");

        MvcResult result = this.mockMvc.perform(post("/v1/admin/submissions/" + this.submissionId + "/release/" + false)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(this.comment)))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Boolean> arg3 = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);

        Mockito.verify(this.adminService).releaseEvaluation(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(this.comment, arg4.getValue());
        assertFalse(arg3.getValue());

        String responseEntity = this.mapper.readValue(result.getResponse().getContentAsString(), String.class);
        assertEquals("64", responseEntity);
    }

    @Test
    public void testCreateEvaluationForReview() throws Exception {
        UUID id = UUID.randomUUID();
        when(this.adminService.reviewEvaluation(this.userId, this.submissionId)).thenReturn(id);

        MvcResult result = this.mockMvc.perform(post("/v1/admin/submissions/" + this.submissionId + "/createReview")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(this.adminService).reviewEvaluation(arg1.capture(), arg2.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());

        String responseEntity = this.mapper.readValue(result.getResponse().getContentAsString(), String.class);
        assertEquals(id.toString(), responseEntity);
    }

    @Test
    public void testRelseaseReviewEvaluation() throws Exception {
        String status = StatusUtil.EVALUATION_RELEASED;
        when(this.adminService.releaseReviewEvaluation(this.evaluatorId, this.submissionId, false, this.comment)).thenReturn(status);

        this.mockMvc.perform(post("/v1/admin/submissions/" + this.submissionId + "/releaseReview/" + false)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(this.comment)))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Boolean> arg3 = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Comment> arg4 = ArgumentCaptor.forClass(Comment.class);

        Mockito.verify(this.adminService).releaseReviewEvaluation(arg1.capture(), arg2.capture(), arg3.capture(), arg4.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
    }


    @Test
    public void testGetEvaluatorWorkspace() throws Exception {
        EvaluatorWorkspace evaluatorWorkspace = new EvaluatorWorkspace(this.submissionModel, this.taskModel, this.evaluationModel);
        evaluatorWorkspace.setPreviousSubmissions(new ArrayList<>());

        when(this.adminService.getReviewWorkspace(this.userId, this.submissionId)).thenReturn(evaluatorWorkspace);

        MvcResult result = this.mockMvc.perform(get("/v1/admin/submissions/" + this.submissionId + "/workspace")
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(this.comment)))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(this.adminService).getReviewWorkspace(arg1.capture(), arg2.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());

        String stringresult = result.getResponse().getContentAsString();
        assertTrue(stringresult.contains("evaluatorWorkspace"));
        assertTrue(stringresult.contains(this.submissionId.toString()));
        assertTrue(stringresult.contains(this.userId));
        assertTrue(stringresult.contains(this.evaluatorId));
    }


    @Test
    public void testGetEvaluatorConfirmation() throws Exception {
        EvaluatorConfirmation confirmation = new EvaluatorConfirmation(this.submissionModel, this.taskModel.getRubric(), this.evaluationModel);

        when(this.iUtil.getUserId()).thenReturn(this.evaluatorId);
        when(this.adminService.getReviewConfirmation(this.evaluatorId, this.submissionId)).thenReturn(confirmation);

        MvcResult result = this.mockMvc.perform(get("/v1/admin/submissions/" + this.submissionId + "/review")
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(this.comment)))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(this.adminService).getReviewConfirmation(arg1.capture(), arg2.capture());
        assertEquals(this.evaluatorId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());

        String stringresult = result.getResponse().getContentAsString();
        assertTrue(stringresult.contains("evaluatorConfirmation"));
        assertTrue(stringresult.contains(this.submissionId.toString()));

    }

}
