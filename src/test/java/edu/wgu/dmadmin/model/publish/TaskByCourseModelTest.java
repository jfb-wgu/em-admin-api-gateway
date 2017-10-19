package edu.wgu.dmadmin.model.publish;

import edu.wgu.dmadmin.TestObjectFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TaskByCourseModelTest {
    TaskByCourseModel taskByCourseModel;

    @Before
    public void setUp() throws Exception {
        this.taskByCourseModel = new TaskByCourseModel();
    }

    @Test
    public void testGetCourseId() throws Exception {
        Long courseId = 12345324L;
        this.taskByCourseModel.setCourseId(courseId);
        assertEquals(courseId, this.taskByCourseModel.getCourseId());
    }

    @Test
    public void testGetAssessmentId() throws Exception {
        UUID assessmentId = UUID.randomUUID();
        this.taskByCourseModel.setAssessmentId(assessmentId);
        assertEquals(assessmentId, this.taskByCourseModel.getAssessmentId());
    }

    @Test
    public void testGetTaskId() throws Exception {
        UUID taskId = UUID.randomUUID();
        this.taskByCourseModel.setTaskId(taskId);
        assertEquals(taskId, this.taskByCourseModel.getTaskId());
    }

    @Test
    public void testGetNotes() throws Exception {
        String notes = "these are some notes";
        this.taskByCourseModel.setNotes(notes);
        assertEquals(notes, this.taskByCourseModel.getNotes());
    }

    @Test
    public void testGetRequirements() throws Exception {
        String requirements = "these are some requirements";
        this.taskByCourseModel.setRequirements(requirements);
        assertEquals(requirements, this.taskByCourseModel.getRequirements());
    }

    @Test
    public void testGetWebLinks() throws Exception {
        List<String> webLinks = TestObjectFactory.getWebLinkList(3);
        this.taskByCourseModel.setWebLinks(webLinks);
        assertEquals(webLinks, this.taskByCourseModel.getWebLinks());
    }

}