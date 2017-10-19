package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.service.EmailService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.IdentityUtil;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class SubmissionUtilityControllerTest {
    @InjectMocks
    private SubmissionUtilityController submissionUtilityController;

    @Mock
    private SubmissionUtilityService utilityService;

    @Mock
    private EmailService emailService;

    @Mock
    private IdentityUtil iUtil;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private UUID submissionId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.submissionUtilityController).build();
    }

    @Test
    public void testGetComments() throws Exception {
        String url = "/v1/submissions/" + this.submissionId + "/comments";

        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setType("Student");
        comment.setUserId("UserId");
        comment.setComments("Comments");
        comment.setCommentId(UUID.randomUUID());
        comment.setAttempt(1);
        comment.setDateCreated(DateUtil.getZonedNow());
        comment.setFirstName("firstName");
        comment.setLastName("LastName");
        comment.setScore(3);
        comment.setDateUpdated(DateUtil.getZonedNow());

        comments.add(comment);


        when(this.utilityService.getInternalComments(this.submissionId)).thenReturn(comments);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(comments), result.getResponse().getContentAsString());

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.utilityService).getInternalComments(arg1.capture());
        assertEquals(this.submissionId, arg1.getValue());
    }

    @Test
    public void testSetComments() throws Exception {
        String url = "/v1/submissions/" + this.submissionId + "/comments";
        String bannerId = "BannerId";

        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setType("Student");
        comment.setUserId("UserId");
        comment.setComments("Comments");
        comment.setCommentId(UUID.randomUUID());
        comment.setAttempt(1);
        comment.setDateCreated(DateUtil.getZonedNow());
        comment.setFirstName("firstName");
        comment.setLastName("LastName");
        comment.setScore(3);
        comment.setDateUpdated(DateUtil.getZonedNow());

        comments.add(comment);

        when(this.iUtil.getUserId()).thenReturn(bannerId);
        when(this.utilityService.updateInternalComments(bannerId, this.submissionId, comments)).thenReturn(comments);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(comments)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(comments), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<List<Comment>> arg3 = ArgumentCaptor.forClass((Class)List.class);

        verify(this.utilityService).updateInternalComments(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(bannerId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(comments, arg3.getValue());
    }

    @Test
    public void testSetComment() throws Exception {
        String url = "/v1/submissions/" + this.submissionId + "/comment";
        String bannerId = "BannerId";

        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setType("Student");
        comment.setUserId("UserId");
        comment.setComments("Comments");
        comment.setCommentId(UUID.randomUUID());
        comment.setAttempt(1);
        comment.setDateCreated(DateUtil.getZonedNow());
        comment.setFirstName("firstName");
        comment.setLastName("LastName");
        comment.setScore(3);
        comment.setDateUpdated(DateUtil.getZonedNow());
        comments.add(comment);


        when(this.iUtil.getUserId()).thenReturn(bannerId);
        when(this.utilityService.updateInternalComments(bannerId, this.submissionId, comments)).thenReturn(comments);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(comments), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<List<Comment>> arg3 = ArgumentCaptor.forClass((Class)List.class);

        verify(this.utilityService).updateInternalComments(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(bannerId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(comments, arg3.getValue());
    }

    @Test
    public void testClearSubmissionHold() throws Exception {
        String url = "/v1/submissions/" + this.submissionId + "/clear";
        String bannerId = "BannerId";
        String status = "16";

        Comment comment = new Comment();
        comment.setType("Student");
        comment.setUserId("UserId");
        comment.setComments("Comments");
        comment.setCommentId(UUID.randomUUID());
        comment.setAttempt(1);
        comment.setDateCreated(DateUtil.getZonedNow());
        comment.setFirstName("firstName");
        comment.setLastName("LastName");
        comment.setScore(3);
        comment.setDateUpdated(DateUtil.getZonedNow());

        when(this.iUtil.getUserId()).thenReturn(bannerId);
        when(this.utilityService.clearSubmissionHold(bannerId, this.submissionId, comment)).thenReturn(status);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(status, result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Comment> arg3 = ArgumentCaptor.forClass(Comment.class);

        verify(this.utilityService).clearSubmissionHold(arg1.capture(), arg2.capture(), arg3.capture());
        assertEquals(bannerId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
        assertEquals(comment, arg3.getValue());
    }

    @Test
    public void testSendTestEmail() throws Exception {
        String url = "/v1/submissions/sendTestEmail";

        doNothing().when(this.emailService).sendTestEmail();

        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        verify(this.emailService).sendTestEmail();
    }

}