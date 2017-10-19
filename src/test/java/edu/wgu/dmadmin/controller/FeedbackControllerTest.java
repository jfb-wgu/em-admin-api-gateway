package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.domain.feedback.StudentFeedback;
import edu.wgu.dmadmin.domain.feedback.StudentFeedbackListResponse;
import edu.wgu.dmadmin.service.FeedbackService;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackControllerTest {
    @InjectMocks
    private FeedbackController feedbackController;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private IdentityUtil iUtil;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    private String studentId = "student";
    private UUID taskId = UUID.randomUUID();
    private StudentFeedback feedback;
    private UUID studentRatingId = UUID.randomUUID();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(this.feedbackController).build();

        when(this.iUtil.getUserId()).thenReturn(this.studentId);

        this.feedback = new StudentFeedback();
        this.feedback.setAssessmentCode("C745");
        this.feedback.setAttempt(2);
        this.feedback.setComments("hi there");
        this.feedback.setDateRated(DateUtil.getZonedNow());
        this.feedback.setRating(3);
        this.feedback.setStudentId(this.studentId);
        this.feedback.setStudentRatingId(this.studentRatingId);
        this.feedback.setTaskId(this.taskId);
    }

    @Test
    public void testSaveStudentFeedback() throws Exception {
        String url = "/v1/feedback/student";

        doNothing().when(this.feedbackService).saveStudentFeedback(this.studentId, this.feedback);

        this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(this.feedback)))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StudentFeedback> arg2 = ArgumentCaptor.forClass(StudentFeedback.class);

        verify(this.feedbackService).saveStudentFeedback(arg1.capture(), arg2.capture());
        assertEquals(this.studentId, arg1.getValue());
        assertEquals(this.feedback.getTaskId(), arg2.getValue().getTaskId());
    }

    @Test
    public void testGetStudentFeedback() throws Exception {
        String url = "/v1/feedback/student/list";
        List<StudentFeedback> studentFeedbackList = new ArrayList<>();
        studentFeedbackList.add(this.feedback);

        when(this.feedbackService.getStudentFeedback(this.studentId)).thenReturn(studentFeedbackList);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(new StudentFeedbackListResponse(studentFeedbackList)), result.getResponse().getContentAsString());

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.feedbackService).getStudentFeedback(arg1.capture());
        assertEquals(this.studentId, arg1.getValue());
    }

    @Test
    public void testHasStudentFeedback() throws Exception {
        String url = "/v1/feedback/student";

        when(this.feedbackService.hasStudentFeedback(this.studentId)).thenReturn(true);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(Boolean.valueOf(result.getResponse().getContentAsString()));

        verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.feedbackService).hasStudentFeedback(arg1.capture());
        assertEquals(this.studentId, arg1.getValue());
    }

}