package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.publish.MimeType;
import edu.wgu.dmadmin.domain.publish.PAMSCourse;
import edu.wgu.dmadmin.domain.publish.Task;
import edu.wgu.dmadmin.domain.publish.TaskDashboard;
import edu.wgu.dmadmin.domain.publish.TaskListResponse;
import edu.wgu.dmadmin.domain.publish.TaskResponse;
import edu.wgu.dmadmin.domain.publish.TaskTree;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.service.PublicationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class PublicationControllerTest {
    @InjectMocks
    private PublicationController publicationController;

    @Mock
    private PublicationService publicationService;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private UUID assessmentId = UUID.randomUUID();
    private UUID taskId = UUID.randomUUID();
    private Task task = TestObjectFactory.getPublishTask();
    private TaskModel taskModel = TestObjectFactory.getTaskModel();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.publicationController).build();
    }

    @Test
    public void testGetAllTasks() throws Exception {
        String url = "/v1/publication/tasks";

        List<Task> tasks = new ArrayList<>();
        tasks.add(this.task);

        when(this.publicationService.getAllTasks()).thenReturn(tasks);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(new TaskListResponse(tasks)), result.getResponse().getContentAsString());

        verify(this.publicationService).getAllTasks();
    }

    @Test
    public void testGetTasksForCourse() throws Exception {
        Long courseId = 123987L;
        String url = "/v1/publication/tasks/course/" + courseId;

        List<Task> tasks = new ArrayList<>();
        tasks.add(this.task);

        when(this.publicationService.getTasksForCourse(courseId)).thenReturn(tasks);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(new TaskListResponse(tasks)), result.getResponse().getContentAsString());

        ArgumentCaptor<Long> arg1 = ArgumentCaptor.forClass(Long.class);

        verify(this.publicationService).getTasksForCourse(arg1.capture());
        assertEquals(courseId, arg1.getValue());
    }

    @Test
    public void testGetTasksForAssessment() throws Exception {
        String url = "/v1/publication/tasks/assessment/" + this.assessmentId;

        List<Task> tasks = new ArrayList<>();
        tasks.add(this.task);

        when(this.publicationService.getTasksForAssessment(this.assessmentId)).thenReturn(tasks);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(new TaskListResponse(tasks)), result.getResponse().getContentAsString());

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.publicationService).getTasksForAssessment(arg1.capture());
        assertEquals(this.assessmentId, arg1.getValue());
    }

    @Test
    public void testGetTask() throws Exception {
        String url = "/v1/publication/tasks/" + this.taskId;

        when(this.publicationService.getTask(this.taskId)).thenReturn(this.task);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(new TaskResponse(this.task)), result.getResponse().getContentAsString());

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.publicationService).getTask(arg1.capture());
        assertEquals(this.taskId, arg1.getValue());
    }

    @Test
    public void testGetTaskTree() throws Exception {
        String url = "/v1/publication/tasks/tree";

        List<TaskModel> taskModels = new ArrayList<>();
        taskModels.add(this.taskModel);
        TaskTree taskTree = new TaskTree(taskModels);

        when(this.publicationService.getTaskTree()).thenReturn(taskTree);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(taskTree), result.getResponse().getContentAsString());

        verify(this.publicationService).getTaskTree();
    }

    @Test
    public void testGetTaskDashbaord() throws Exception {
        String url = "/v1/publication/tasks/dashboard";

        List<TaskModel> taskModels = new ArrayList<>();
        taskModels.add(this.taskModel);
        TaskDashboard taskDashboard = new TaskDashboard(taskModels);

        when(this.publicationService.getTaskDashboard()).thenReturn(taskDashboard);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(taskDashboard), result.getResponse().getContentAsString());

        verify(this.publicationService).getTaskDashboard();
    }

    @Test
    public void testAddTask() throws Exception {
        String url = "/v1/publication/tasks";

        doNothing().when(this.publicationService).addTask(this.task);

        this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(this.task)))
                .andExpect(status().isNoContent())
                .andReturn();

        ArgumentCaptor<Task> arg1 = ArgumentCaptor.forClass(Task.class);

        verify(this.publicationService).addTask(arg1.capture());
        assertEquals(this.task.getTaskName(), arg1.getValue().getTaskName());
    }

    @Test
    public void testDeleteTask() throws Exception {
        String url = "/v1/publication/tasks/" + this.taskId;

        doNothing().when(this.publicationService).deleteTask(this.taskId);

        this.mockMvc.perform(delete(url))
                .andExpect(status().isNoContent())
                .andReturn();

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.publicationService).deleteTask(arg1.capture());
        assertEquals(this.taskId, arg1.getValue());
    }

    @Test
    public void testGetCourseVersion() throws Exception {
        String courseCode = "C745";
        String url = "/v1/publication/courses/" + courseCode + "/pams";

        PAMSCourse pamsCourse = new PAMSCourse();

        when(this.publicationService.getCourseVersion(courseCode)).thenReturn(pamsCourse);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(pamsCourse), result.getResponse().getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.publicationService).getCourseVersion(arg1.capture());
        assertEquals(courseCode, arg1.getValue());
    }

    @Test
    public void testGetMimeTypes() throws Exception {
        String url = "/v1/publication/mimetypes";

        List<MimeType> mimeTypes = new ArrayList<>();
        mimeTypes.add(new MimeType());

        when(this.publicationService.getMimeTypes()).thenReturn(mimeTypes);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(mimeTypes), result.getResponse().getContentAsString());


        verify(this.publicationService).getMimeTypes();
    }

    @Test
    public void testAddMimeType() throws Exception {
        String url = "/v1/publication/mimetypes";

        MimeType mimeType = new MimeType();

        doNothing().when(this.publicationService).addMimeType(mimeType);

        this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(mimeType)))
                .andExpect(status().isNoContent())
                .andReturn();

        ArgumentCaptor<MimeType> arg1 = ArgumentCaptor.forClass(MimeType.class);

        verify(this.publicationService).addMimeType(arg1.capture());
        assertEquals(mimeType, arg1.getValue());
    }

    @Test
    public void testAddSupportingDoc() throws Exception {
        String url = "/v1/publication/task/" + this.taskId + "/supportingdocument";

        List<Attachment> supportingDocuments = new ArrayList<>();
        Attachment supportingDocument = TestObjectFactory.getAttachment("title", 234L, true, "text", false, null);
        supportingDocuments.add(supportingDocument);

        AttachmentModel supportingDocumentModel = TestObjectFactory.getAttachmentModel("title", 234L, true, "text", false, null);

        when(this.publicationService.addSupportingDocument(this.taskId, supportingDocumentModel)).thenReturn(supportingDocuments);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(supportingDocumentModel)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(supportingDocuments), result.getResponse().getContentAsString());

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<AttachmentModel> arg2 = ArgumentCaptor.forClass(AttachmentModel.class);

        verify(this.publicationService).addSupportingDocument(arg1.capture(), arg2.capture());
        assertEquals(this.taskId, arg1.getValue());
        assertEquals(supportingDocumentModel, arg2.getValue());
    }

    @Test
    public void testDeleteSupportingDoc() throws Exception {
        String docTitle = "title";
        String url = "/v1/publication/task/" + this.taskId + "/supportingdocument/" + docTitle;

        doNothing().when(this.publicationService).deleteSupportingDocument(this.taskId, docTitle);

        this.mockMvc.perform(delete(url))
                .andExpect(status().isNoContent())
                .andReturn();

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

        verify(this.publicationService).deleteSupportingDocument(arg1.capture(), arg2.capture());
        assertEquals(this.taskId, arg1.getValue());
        assertEquals(docTitle, arg2.getValue());
    }

}