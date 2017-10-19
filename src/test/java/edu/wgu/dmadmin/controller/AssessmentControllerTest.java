package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.exception.CourseNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.service.StudentAssessmentService;
import edu.wgu.dmadmin.util.IdentityUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("boxing")
public class AssessmentControllerTest {
    AssessmentController controller = new AssessmentController();
    private StudentAssessmentService studentAssessmentService = mock(StudentAssessmentService.class);
    private IdentityUtil iUtil = mock(IdentityUtil.class);
    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    private String userId = "123456";
    private Long courseId = 1234L;
    private UUID submissionId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        this.controller.setIdentityUtil(this.iUtil);
        this.controller.setStudentAssessmentService(this.studentAssessmentService);

        this.mockMvc = standaloneSetup(this.controller).build();

        when(this.iUtil.getUserId()).thenReturn(this.userId);
    }

    @Test
    public void getCourse() throws Exception {

        this.mockMvc.perform(get("/v1/student/courses/" + this.courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> arg2 = ArgumentCaptor.forClass(Long.class);

        Mockito.verify(this.studentAssessmentService).getAssessmentsForCourse(arg1.capture(), arg2.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.courseId, arg2.getValue());
    }

    @Test
    public void getCourseNoCourseId() throws Exception {
        this.mockMvc.perform(get("/v1/student/courses/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.studentAssessmentService, never()).getAssessmentsForCourse(anyString(), anyLong());
    }

    @Test
    public void getCourseServiceException() throws Exception {
        when(this.studentAssessmentService.getAssessmentsForCourse(anyString(), anyLong()))
                .thenThrow(new CourseNotFoundException(this.courseId));

        this.mockMvc.perform(get("/v1/student/courses/" + this.courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentAssessmentService).getAssessmentsForCourse(anyString(), anyLong());
    }

    @Test
    public void getCourseNoUser() throws Exception {
        when(this.iUtil.getUserId()).thenThrow(new UserIdNotFoundException("testing"));

        this.mockMvc.perform(get("/v1/student/courses/" + this.courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentAssessmentService, never()).getAssessmentsForCourse(anyString(), anyLong());
    }

    @Test
    public void getScoreReport() throws Exception {

        this.mockMvc.perform(get("/v1/student/scorereport/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(this.studentAssessmentService).getScoreReport(arg1.capture(), arg2.capture());
        assertEquals(this.userId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
    }

    @Test
    public void getScoreReportServiceException1() throws Exception {
        when(this.studentAssessmentService.getScoreReport(anyString(), any(UUID.class)))
                .thenThrow(new SubmissionNotFoundException(this.submissionId));


        this.mockMvc.perform(get("/v1/student/scorereport/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentAssessmentService).getScoreReport(anyString(), any(UUID.class));
    }

    @Test
    public void getScoreReportServiceException2() throws Exception {
        when(this.studentAssessmentService.getScoreReport(anyString(), any(UUID.class)))
                .thenThrow(new TaskNotFoundException(UUID.randomUUID()));

        this.mockMvc.perform(get("/v1/student/scorereport/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentAssessmentService).getScoreReport(anyString(), any(UUID.class));
    }

    @Test
    public void getScoreReportNoUser() throws Exception {
        when(this.iUtil.getUserId()).thenThrow(new UserIdNotFoundException("testing"));

        this.mockMvc.perform(get("/v1/student/scorereport/" + this.submissionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil).getUserId();
        Mockito.verify(this.studentAssessmentService, never()).getScoreReport(anyString(), any(UUID.class));
    }

    @Test
    public void getScoreReportNoSubId() throws Exception {
        this.mockMvc.perform(get("/v1/student/scorereport/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(this.iUtil, never()).getUserId();
        Mockito.verify(this.studentAssessmentService, never()).getScoreReport(anyString(), any(UUID.class));
    }
}
