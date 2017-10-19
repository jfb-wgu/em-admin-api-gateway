package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.assessment.Assessment;
import edu.wgu.dmadmin.domain.assessment.Course;
import edu.wgu.dmadmin.domain.assessment.CourseResponse;
import edu.wgu.dmadmin.domain.assessment.Task;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.service.StudentAssessmentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class FacultyControllerTest {

    @InjectMocks
    private FacultyController facultyController;

    @Mock
    private StudentAssessmentService studentAssessmentService;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    private String studentId = "student";
    private UUID submissionId = UUID.randomUUID();
    private TaskModel taskModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(this.facultyController).build();

        this.taskModel = TestObjectFactory.getTaskModel();
    }

    @Test
    public void testGetCourse() throws Exception {
        Long courseIdLong = 123456L;
        String url = "/v1/students/" + this.studentId + "/courses/123456";
        Map<UUID, Assessment> assessmentMap = new HashMap<>();

        Course course = new Course();
        course.setRequestFeedback(true);
        course.setCourseCode("C740");
        course.setCourseId(courseIdLong);
        course.setAssessments(assessmentMap);

        when(this.studentAssessmentService.getAssessmentsForCourse(this.studentId, courseIdLong)).thenReturn(course);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(new CourseResponse(course)), result.getResponse().getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> arg2 = ArgumentCaptor.forClass(Long.class);

        verify(this.studentAssessmentService).getAssessmentsForCourse(arg1.capture(), arg2.capture());
        assertEquals(this.studentId, arg1.getValue());
        assertEquals(courseIdLong, arg2.getValue());
    }

    @Test
    public void testGetScoreReport() throws Exception {
        String url = "/v1/students/" + this.studentId + "/scorereports/" + this.submissionId;
        Map<UUID, Assessment> assessmentMap = new HashMap<>();

        Task task = new Task(this.taskModel);

        when(this.studentAssessmentService.getScoreReport(this.studentId, this.submissionId)).thenReturn(task);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(task), result.getResponse().getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> arg2 = ArgumentCaptor.forClass(UUID.class);

        verify(this.studentAssessmentService).getScoreReport(arg1.capture(), arg2.capture());
        assertEquals(this.studentId, arg1.getValue());
        assertEquals(this.submissionId, arg2.getValue());
    }

}