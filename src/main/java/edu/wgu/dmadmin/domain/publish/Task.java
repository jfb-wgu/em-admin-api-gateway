package edu.wgu.dmadmin.domain.publish;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.model.publish.TaskModel;

@Data
@NoArgsConstructor
public class Task implements Comparable<Task> {
	String courseName;
    String courseCode;
    Long courseId;
    String assessmentName;
    String assessmentCode;
    String assessmentDate;
    String assessmentType;
    int assessmentOrder;
    UUID assessmentId;
    String taskName;  
    UUID taskId;
    int taskOrder;
    int averageTime;
    String description;
    List<Competency> competencies;
    Map<String, Attachment> supportingDocuments;
    String introduction;
    String scenario;
    String notes;
    String requirements;
    String CRDNotes;
    Rubric rubric;
    int aspectCount;
    List<String> webLinks;
    int originalityMinimum;
    int originalityWarning;
    Date dateCreated;
    Date dateUpdated;
    Date datePublished;
    Date dateRetired;
    String publicationStatus;
    
    public Map<String, Attachment> getSupportingDocuments() {
    	if (supportingDocuments == null) supportingDocuments = new HashMap<String, Attachment>();
    	return supportingDocuments;
    }
    
    public List<Competency> getCompetencies() {
    	if (competencies == null) competencies = new ArrayList<Competency>();
    	return competencies;
    }
    
    public Task(TaskModel model) {
    	this.courseName = model.getCourseName();
    	this.courseCode = model.getCourseCode();
    	this.courseId = model.getCourseId();
    	this.assessmentName = model.getAssessmentName();
    	this.assessmentCode = model.getAssessmentCode();
    	this.assessmentDate = model.getAssessmentDate();
    	this.assessmentType = model.getAssessmentType();
    	this.assessmentOrder = model.getAssessmentOrder();
    	this.assessmentId = model.getAssessmentId();
    	this.taskName = model.getTaskName();
    	this.taskId = model.getTaskId();
    	this.taskOrder = model.getTaskOrder();
    	this.averageTime = model.getAverageTime();
    	this.description = model.getDescription();
    	this.competencies = model.getCompetencies().stream().map(competency -> new Competency(competency)).collect(Collectors.toList());
    	this.supportingDocuments = model.getSupportingDocuments().entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> new Attachment(e.getValue(), courseCode, assessmentCode, taskId)));
    	this.introduction = model.getIntroduction();
    	this.scenario = model.getScenario();
    	this.notes = model.getNotes();
    	this.requirements = model.getRequirements();
    	this.CRDNotes = model.getCRDNotes();
   		this.rubric = new Rubric(model.getRubric());
    	this.aspectCount = model.getAspectCount();
    	this.webLinks = model.getWebLinks();
    	this.originalityMinimum = model.getOriginalityMinimum();
    	this.originalityWarning = model.getOriginalityWarning();
    	this.dateCreated = model.getDateCreated();
    	this.dateUpdated = model.getDateUpdated();
    	this.datePublished = model.getDatePublished();
    	this.dateRetired = model.getDateRetired();
    	this.publicationStatus = model.getPublicationStatus();
    }

	@Override
	public int compareTo(Task o) {
		if (this.assessmentOrder == o.assessmentOrder) {
			return this.taskOrder - o.taskOrder;
		} else {
			return this.assessmentOrder - o.assessmentOrder;
		}
	}
}
