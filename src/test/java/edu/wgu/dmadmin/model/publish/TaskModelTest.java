package edu.wgu.dmadmin.model.publish;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.util.DateUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class TaskModelTest {
    TaskModel taskModel;

    @Before
    public void setUp() throws Exception {
        this.taskModel = new TaskModel();
    }

    @Test
    public void testGetSupportingDocuments() throws Exception {
        Map<String, AttachmentModel> docs = new HashMap<>();
        AttachmentModel Attachment1 = new AttachmentModel();
        docs.put("hi", Attachment1);

        this.taskModel.setSupportingDocuments(docs);

        assertEquals(docs, this.taskModel.getSupportingDocuments());
    }

    @Test
    public void testGetCompetencies() throws Exception {
        List<CompetencyModel> comps = new ArrayList<>();
        comps.add(new CompetencyModel());
        this.taskModel.setCompetencies(comps);

        assertEquals(comps, this.taskModel.getCompetencies());
    }

    @Test
    public void testGetWebLinks() throws Exception {
        List<String> webLinks = TestObjectFactory.getWebLinkList(3);
        this.taskModel.setWebLinks(webLinks);

        assertEquals(webLinks, this.taskModel.getWebLinks());
    }

    @Test
    public void testGetNotes() throws Exception {
        String notes = "There are some notes";
        this.taskModel.setNotes(notes);

        assertEquals(notes, this.taskModel.getNotes());
    }

    @Test
    public void testGetRequirements() throws Exception {
        String requirements = "requirements";
        this.taskModel.setRequirements(requirements);

        assertEquals(requirements, this.taskModel.getRequirements());
    }

    @Test
    public void populate() throws Exception {
        TaskModel testModel = new TaskModel();
        String notes = "these are some notes";
        testModel.setNotes(notes);
        this.taskModel.populate(testModel);

        assertEquals(notes, this.taskModel.getNotes());
        assertNull(this.taskModel.getTaskId());
    }

    @Test
    public void testGetCourseName() throws Exception {
        String courseName = "How to be Batman";
        this.taskModel.setCourseName(courseName);

        assertEquals(courseName, this.taskModel.getCourseName());
    }

    @Test
    public void testGetCourseCode() throws Exception {
        String courseCode = "Bat101";
        this.taskModel.setCourseCode(courseCode);

        assertEquals(courseCode, this.taskModel.getCourseCode());
    }

    @Test
    public void testGetCourseId() throws Exception {
        Long courseId = 23432423L;
        this.taskModel.setCourseId(courseId);

        assertEquals(courseId, this.taskModel.getCourseId());
    }

    @Test
    public void testGetAssessmentName() throws Exception {
        String assessmentName = "AssessmentName";
        this.taskModel.setAssessmentName(assessmentName);

        assertEquals(assessmentName, this.taskModel.getAssessmentName());
    }

    @Test
    public void testGetAssessmentCode() throws Exception {
        String code = "Code";
        this.taskModel.setAssessmentCode(code);

        assertEquals(code, this.taskModel.getAssessmentCode());
    }

    @Test
    public void testGetAssessmentDate() throws Exception {
        String assesDate = DateUtil.getZonedNow().toString();
        this.taskModel.setAssessmentDate(assesDate);

        assertEquals(assesDate, this.taskModel.getAssessmentDate());
    }

    @Test
    public void testGetAssessmentType() throws Exception {
        String type = "good test";
        this.taskModel.setAssessmentType(type);

        assertEquals(type, this.taskModel.getAssessmentType());
    }

    @Test
    public void testGetAssessmentOrder() throws Exception {
        int assessmentOrder = 4;
        this.taskModel.setAssessmentOrder(assessmentOrder);

        assertEquals(assessmentOrder, this.taskModel.getAssessmentOrder());
    }

    @Test
    public void testGetAssessmentId() throws Exception {
        UUID assessmentId = UUID.randomUUID();
        this.taskModel.setAssessmentId(assessmentId);

        assertEquals(assessmentId, this.taskModel.getAssessmentId());
    }

    @Test
    public void testGetTaskName() throws Exception {
        String task_name = "Task Name";
        this.taskModel.setTaskName(task_name);

        assertEquals(task_name, this.taskModel.getTaskName());
    }

    @Test
    public void testGetTaskId() throws Exception {
        UUID taskId = UUID.randomUUID();
        this.taskModel.setTaskId(taskId);

        assertEquals(taskId, this.taskModel.getTaskId());
    }

    @Test
    public void testGetTaskOrder() throws Exception {
        int taskOrder = 4;
        this.taskModel.setTaskOrder(taskOrder);

        assertEquals(taskOrder, this.taskModel.getTaskOrder());
    }

    @Test
    public void testGetAveratestGetime() throws Exception {
        int averageTime = 345;
        this.taskModel.setAverageTime(averageTime);

        assertEquals(averageTime, this.taskModel.getAverageTime());
    }

    @Test
    public void testGetDescription() throws Exception {
        String description = "Description";
        this.taskModel.setDescription(description);

        assertEquals(description, this.taskModel.getDescription());
    }

    @Test
    public void testGetIntroduction() throws Exception {
        String intro = "intro";
        this.taskModel.setIntroduction(intro);

        assertEquals(intro, this.taskModel.getIntroduction());
    }

    @Test
    public void testGetScenario() throws Exception {
        String scenario = "You want to be batman";
        this.taskModel.setScenario(scenario);

        assertEquals(scenario, this.taskModel.getScenario());
    }

    @Test
    public void testGetCRDNotes() throws Exception {
        String crdNotes = "CRDNotes";
        this.taskModel.setCRDNotes(crdNotes);

        assertEquals(crdNotes, this.taskModel.getCRDNotes());
    }

    @Test
    public void testGetRubric() throws Exception {
        RubricModel rubric = new RubricModel();
        this.taskModel.setRubric(rubric);

        assertEquals(rubric, this.taskModel.getRubric());
    }

    @Test
    public void testGetAspectCount() throws Exception {
        int aspectCount = 32;
        this.taskModel.setAspectCount(aspectCount);

        assertEquals(aspectCount, this.taskModel.getAspectCount());
    }

    @Test
    public void testGetOriginalityMinimum() throws Exception {
        int originalityMinimum = 3;
        this.taskModel.setOriginalityMinimum(originalityMinimum);

        assertEquals(originalityMinimum, this.taskModel.getOriginalityMinimum());
    }

    @Test
    public void testGetOriginalityWarning() throws Exception {
        int originalityWarning = 2;
        this.taskModel.setOriginalityWarning(originalityWarning);

        assertEquals(originalityWarning, this.taskModel.getOriginalityWarning());
    }

    @Test
    public void testGetDateCreated() throws Exception {
        Date dateCreated = DateUtil.getZonedNow();
        this.taskModel.setDateCreated(dateCreated);

        assertEquals(dateCreated, this.taskModel.getDateCreated());
    }

    @Test
    public void testGetDateUpdated() throws Exception {
        Date dateUpdated = DateUtil.getZonedNow();
        this.taskModel.setDateUpdated(dateUpdated);

        assertEquals(dateUpdated, this.taskModel.getDateUpdated());
    }

    @Test
    public void testGetDatePublished() throws Exception {
        Date datePublished = DateUtil.getZonedNow();
        this.taskModel.setDatePublished(datePublished);

        assertEquals(datePublished, this.taskModel.getDatePublished());
    }

    @Test
    public void testGetDateRetired() throws Exception {
        Date dateRetired = DateUtil.getZonedNow();
        this.taskModel.setDateRetired(dateRetired);

        assertEquals(dateRetired, this.taskModel.getDateRetired());
    }

    @Test
    public void testGetPublicationStatus() throws Exception {
        String published = "PUBLISHED";
        this.taskModel.setPublicationStatus(published);

        assertEquals(published, this.taskModel.getPublicationStatus());
    }

}