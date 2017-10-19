package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.publish.Task;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@Table(keyspace = "dm", name = "task_by_course", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class TaskByCourseModel extends TaskModel {	
	
	@PartitionKey(0)
	public Long getCourseId() {
		return courseId;
	}

	@PartitionKey(1)
	public UUID getAssessmentId() {
		return assessmentId;
	}

	@PartitionKey(2)
	public UUID getTaskId() {
		return taskId;
	}
    
    public TaskByCourseModel(TaskModel model) {
    	this.populate(model);
    }
    
    public TaskByCourseModel(Task task) {
    	this.courseName = task.getCourseName();
    	this.courseCode = task.getCourseCode();
    	this.courseId = task.getCourseId();
    	this.assessmentName = task.getAssessmentName();
    	this.assessmentCode = task.getAssessmentCode();
    	this.assessmentDate = task.getAssessmentDate();
    	this.assessmentType = task.getAssessmentType();
    	this.assessmentOrder = task.getAssessmentOrder();
    	this.assessmentId = task.getAssessmentId();
    	this.taskName = task.getTaskName();
    	this.taskId = task.getTaskId();
    	this.taskOrder = task.getTaskOrder();
    	this.averageTime = task.getAverageTime();
    	this.description = task.getDescription();
    	this.competencies = task.getCompetencies().stream().map(competency -> new CompetencyModel(competency)).collect(Collectors.toList());
    	this.supportingDocuments = task.getSupportingDocuments().entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> new AttachmentModel(e.getValue())));
    	this.introduction = task.getIntroduction();
    	this.scenario = task.getScenario();
    	this.notes = task.getNotes();
    	this.requirements = task.getRequirements();
    	this.CRDNotes = task.getCRDNotes();
   		this.rubric = new RubricModel(task.getRubric());
    	this.aspectCount = task.getAspectCount();
    	this.webLinks = task.getWebLinks();
    	this.originalityMinimum = task.getOriginalityMinimum();
    	this.originalityWarning = task.getOriginalityWarning();
    	this.dateCreated = task.getDateCreated();
    	this.dateUpdated = task.getDateUpdated();
    	this.datePublished = task.getDatePublished();
    	this.dateRetired = task.getDateRetired();
    	this.publicationStatus = task.getPublicationStatus();
    }
}
