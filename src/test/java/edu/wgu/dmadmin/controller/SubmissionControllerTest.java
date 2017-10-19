package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.service.StudentWorkService;
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

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("boxing")
public class SubmissionControllerTest {
    SubmissionController controller = new SubmissionController();
    private StudentWorkService studentWorkService = mock(StudentWorkService.class);
    private IdentityUtil iUtil = mock(IdentityUtil.class);
    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();
    private Submission returnSubmission;

    private Long pidm = 234567L;
    private String userId = "123456";
    private UUID taskId = UUID.randomUUID();
    private UUID submissionId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        this.controller.setIdentityUtil(this.iUtil);
        this.controller.setStudentWorkService(this.studentWorkService);

        this.mockMvc = standaloneSetup(this.controller).build();

        when(this.iUtil.getUserId()).thenReturn(this.userId);
        when(this.iUtil.getUserPidm()).thenReturn(this.pidm);

        this.returnSubmission = TestObjectFactory.getTestSubmission(StatusUtil.AUTHOR_SUBMISSION_STARTED);
        this.returnSubmission.setSubmissionId(this.submissionId);
    }

    @Test
    public void beginSubmission() throws Exception {
        when(this.studentWorkService.beginSubmission(this.userId, this.taskId, this.pidm)).thenReturn(this.returnSubmission);

        MvcResult result = this.mockMvc.perform(post("/v1/submission/task/" + this.taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.iUtil).getUserPidm();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Long> arg3 = ArgumentCaptor.forClass(Long.class);

        Mockito.verify(this.studentWorkService).beginSubmission(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.taskId, arg2.getValue());
        assertEquals(this.pidm, arg3.getValue());

        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        Submission sub = this.mapper.readValue(result.getResponse().getContentAsString(), Submission.class);
        assertEquals(this.submissionId, sub.getSubmissionId());
    }

    @Test
    public void beginSubmissionNoTask() throws Exception {
        this.mockMvc.perform(post("/v1/submission/task/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.iUtil, never()).getUserPidm();
        Mockito.verify(this.studentWorkService, never()).beginSubmission(anyString(), any(UUID.class), anyLong());
    }

    @Test
    public void beginSubmissionServiceException() throws Exception {
        when(this.studentWorkService.beginSubmission(anyString(), any(UUID.class), anyLong()))
                .thenThrow(new SubmissionStatusException(this.submissionId, "test"));

        this.mockMvc.perform(post("/v1/submission/task/" + this.taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.iUtil).getUserPidm();
        Mockito.verify(this.studentWorkService).beginSubmission(anyString(), any(UUID.class), anyLong());
    }

    @Test
    public void addComments() throws Exception {
        String comment = "test comment";
        when(this.studentWorkService.addComments(this.userId, this.submissionId, comment)).thenReturn(this.returnSubmission);

        MvcResult result = this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(comment))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.studentWorkService).addComments(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals("test comment", arg3.getValue());

        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        Submission sub = this.mapper.readValue(result.getResponse().getContentAsString(), Submission.class);
        assertEquals(this.submissionId, sub.getSubmissionId());
    }

    @Test
    public void addCommentsServiceException() throws Exception {
        when(this.studentWorkService.addComments(anyString(), any(UUID.class), anyString()))
                .thenThrow(new SubmissionStatusException(this.submissionId, "test"));

        String comment = "test comment";

        this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(comment))
                .andExpect(status().is5xxServerError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService).addComments(anyString(), any(UUID.class), anyString());
    }

    @Test
    public void addCommentsNoSubId() throws Exception {
        this.mockMvc.perform(post("/v1/submission/" + StringUtils.EMPTY + "/comment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.studentWorkService, never()).addComments(anyString(), any(UUID.class), anyString());
    }

    @Test
    public void addCommentsNoStudentId() throws Exception {
        when(this.iUtil.getUserId()).thenThrow(new UserIdNotFoundException("testing"));

        this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/comment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.studentWorkService, never()).addComments(anyString(), any(UUID.class), anyString());
    }

    @Test
    public void saveSubmission() throws Exception {
        when(this.studentWorkService.saveSubmission(anyString(), any(UUID.class), any(Submission.class))).thenReturn(this.returnSubmission);

        MvcResult result = this.mockMvc.perform(post("/v1/submission/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(this.returnSubmission)))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Submission> arg3 = ArgumentCaptor.forClass(Submission.class);

        Mockito.verify(this.studentWorkService).saveSubmission(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(this.returnSubmission.getSubmissionId(), arg3.getValue().getSubmissionId());

        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        Submission sub = this.mapper.readValue(result.getResponse().getContentAsString(), Submission.class);
        assertEquals(this.submissionId, sub.getSubmissionId());
    }

    @Test
    public void saveSubmissionServiceException() throws Exception {
        when(this.studentWorkService.saveSubmission(anyString(), any(UUID.class), any(Submission.class)))
                .thenThrow(new SubmissionStatusException(this.submissionId, "test"));

        this.mockMvc.perform(post("/v1/submission/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(this.returnSubmission)))
                .andExpect(status().is5xxServerError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService).saveSubmission(anyString(), any(UUID.class), any(Submission.class));
    }

    @Test
    public void saveSubmissionNoSubId() throws Exception {
        this.mockMvc.perform(post("/v1/submission/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(this.returnSubmission)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.studentWorkService, never()).saveSubmission(anyString(), any(UUID.class), any(Submission.class));
    }

    @Test
    public void saveSubmissionBadSubId() throws Exception {
        this.mockMvc.perform(post("/v1/submission/askdjfajsdl;kfj")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(this.returnSubmission)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.studentWorkService, never()).saveSubmission(anyString(), any(UUID.class), any(Submission.class));
    }

    @Test
    public void saveSubmissionNoSub() throws Exception {
        this.mockMvc.perform(post("/v1/submission/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.studentWorkService, never()).saveSubmission(anyString(), any(UUID.class), any(Submission.class));
    }

    @Test
    public void saveSubmissionNoUserId() throws Exception {
        when(this.iUtil.getUserId()).thenThrow(new UserIdNotFoundException("testing"));

        this.mockMvc.perform(post("/v1/submission/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(this.returnSubmission)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.studentWorkService, never()).saveSubmission(anyString(), any(UUID.class), any(Submission.class));
    }

    @Test
    public void cancelSubmission() throws Exception {
        when(this.studentWorkService.cancelSubmission(anyString(), any(UUID.class))).thenReturn(this.returnSubmission);

        MvcResult result = this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(this.studentWorkService).cancelSubmission(arg1.capture(), arg2.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());

        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        Submission sub = this.mapper.readValue(result.getResponse().getContentAsString(), Submission.class);
        assertEquals(this.submissionId, sub.getSubmissionId());
    }

    @Test
    public void cancelSubmissionServiceException() throws Exception {
        when(this.studentWorkService.cancelSubmission(anyString(), any(UUID.class)))
                .thenThrow(new SubmissionStatusException(this.submissionId, "test"));

        this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService).cancelSubmission(anyString(), any(UUID.class));
    }

    @Test
    public void cancelSubmissionNoUser() throws Exception {
        when(this.iUtil.getUserId()).thenThrow(new UserIdNotFoundException("testing"));

        this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService, never()).cancelSubmission(anyString(), any(UUID.class));
    }

    @Test
    public void cancelSubmissionNoSubId() throws Exception {
        this.mockMvc.perform(post("/v1/submission/" + StringUtils.EMPTY + "/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.studentWorkService, never()).cancelSubmission(anyString(), any(UUID.class));
    }

    @Test
    public void queueSubmission() throws Exception {
        when(this.studentWorkService.submitForEvaluation(anyString(), any(UUID.class), anyString())).thenReturn(this.returnSubmission);

        MvcResult result = this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/queue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("testing"))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.studentWorkService).submitForEvaluation(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals("testing", arg3.getValue());

        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        Submission sub = this.mapper.readValue(result.getResponse().getContentAsString(), Submission.class);
        assertEquals(this.submissionId, sub.getSubmissionId());
    }

    @Test
    public void queueSubmissionServiceException() throws Exception {
        when(this.studentWorkService.submitForEvaluation(anyString(), any(UUID.class), anyString()))
                .thenThrow(new SubmissionStatusException(this.submissionId, "test"));

        this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/queue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("testing"))
                .andExpect(status().is5xxServerError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService).submitForEvaluation(anyString(), any(UUID.class), anyString());
    }

    @Test
    public void queueSubmissionNoSubId() throws Exception {
        this.mockMvc.perform(post("/v1/submission/" + StringUtils.EMPTY + "/queue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("testing"))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.studentWorkService, never()).submitForEvaluation(anyString(), any(UUID.class), anyString());
    }

    @Test
    public void queueSubmissionNoUser() throws Exception {
        when(this.iUtil.getUserId()).thenThrow(new UserIdNotFoundException("testing"));

        this.mockMvc.perform(post("/v1/submission/" + this.submissionId + "/queue")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService, never()).submitForEvaluation(anyString(), any(UUID.class), anyString());
    }

    @Test
    public void getSubmissions() throws Exception {
        this.mockMvc.perform(get("/v1/submissions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.studentWorkService).getSubmissions(arg1.capture());
        assertEquals(this.userId, arg1.getValue());
    }

    @Test
    public void getSubmissionsNoUser() throws Exception {
        when(this.iUtil.getUserId()).thenThrow(new UserIdNotFoundException("testing"));

        this.mockMvc.perform(get("/v1/submissions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService, never()).getSubmissions(anyString());
    }

    @Test
    public void getSubmission() throws Exception {
        when(this.studentWorkService.getSubmission(anyString(), any(UUID.class))).thenReturn(this.returnSubmission);

        MvcResult result = this.mockMvc.perform(get("/v1/submissions/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(this.studentWorkService).getSubmission(arg1.capture(), arg2.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());

        assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
        Submission sub = this.mapper.readValue(result.getResponse().getContentAsString(), Submission.class);
        assertEquals(this.submissionId, sub.getSubmissionId());
    }

    @Test
    public void getSubmissionNoUser() throws Exception {
        when(this.iUtil.getUserId()).thenThrow(new UserIdNotFoundException("testing"));

        this.mockMvc.perform(get("/v1/submissions/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService, never()).getSubmission(anyString(), any(UUID.class));
    }

    @Test
    public void getSubmissionServiceException() throws Exception {
        when(this.studentWorkService.getSubmission(anyString(), any(UUID.class)))
                .thenThrow(new SubmissionNotFoundException(this.submissionId));

        this.mockMvc.perform(get("/v1/submissions/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentWorkService).getSubmission(anyString(), any(UUID.class));
    }
}
