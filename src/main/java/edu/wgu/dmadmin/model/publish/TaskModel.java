package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.FrozenValue;

import edu.wgu.dmadmin.model.submission.AttachmentModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TaskModel implements Comparable<TaskModel> {

    @Column(name="course_name")
	String courseName;
    
    @Column(name="course_code")
    String courseCode;
    
	@Column(name="course_id")
	Long courseId;

    @Column(name="assessment_name")
    String assessmentName;

    @Column(name="assessment_code")
    String assessmentCode;

    @Column(name="assessment_date")
    String assessmentDate;
    
    @Column(name="assessment_type")
    String assessmentType;
    
    @Column(name="assessment_order")
    int assessmentOrder;
    
    @Column(name="assessment_id")
    UUID assessmentId;
    
    @Column(name="task_name")
    String taskName;  
    
    @Column(name="task_id")
    UUID taskId;

    @Column(name="task_order")
    int taskOrder;

    @Column(name="avg_time_required")
    int averageTime;
    
    String description;
    
    @Frozen
    List<CompetencyModel> competencies;
    
    @Column(name="supporting_documents")
    @FrozenValue
    Map<String, AttachmentModel> supportingDocuments;
    
    String introduction;
    String scenario;
    String notes;
    String requirements;
    
    @Column(name="crd_notes")
    String CRDNotes;
    
    @Frozen
    RubricModel rubric;
    
    @Column(name="aspect_count")
    int aspectCount;
    
    @Column(name="web_links")
    List<String> webLinks;
    
    @Column(name="originality_minimum")
    int originalityMinimum;
    
    @Column(name="originality_warning")
    int originalityWarning;
    
    @Column(name="date_created")
    Date dateCreated;
    
    @Column(name="date_updated")
    Date dateUpdated;
    
    @Column(name="date_published")
    Date datePublished;
    
    @Column(name="date_retired")
    Date dateRetired;
    
    @Column(name="publication_status")
    String publicationStatus;
    
    public Map<String, AttachmentModel> getSupportingDocuments() {
    	if (supportingDocuments == null) supportingDocuments = new HashMap<String, AttachmentModel>();
    	return supportingDocuments;
    }
    
    public List<CompetencyModel> getCompetencies() {
    	if (competencies == null) competencies = new ArrayList<CompetencyModel>();
    	return competencies;
    }
    
    public List<String> getWebLinks() {
    	if (webLinks == null) webLinks = new ArrayList<String>();
    	return webLinks;
    }
    
    public String getNotes() {
    	if (notes == null) notes = "";
    	return notes;
    }
    
    public String getRequirements() {
    	if (requirements == null) requirements = "";
    	return requirements;
    }
    
    public void populate(TaskModel model) {
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
    	this.competencies = model.getCompetencies();
    	this.supportingDocuments = model.getSupportingDocuments();    	
    	this.introduction = model.getIntroduction();
    	this.scenario = model.getScenario();
    	this.notes = model.getNotes();
    	this.requirements = model.getRequirements();
    	this.CRDNotes = model.getCRDNotes();
    	this.rubric = model.getRubric();
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
	public int compareTo(TaskModel o) {
		return this.getAssessmentOrder() - o.getAssessmentOrder();
	}
}
