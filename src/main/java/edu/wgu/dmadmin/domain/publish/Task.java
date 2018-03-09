package edu.wgu.dmadmin.domain.publish;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import edu.wgu.dmadmin.model.publish.SupportingDocumentModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	List<SupportingDocument> supportingDocuments;
	String introduction;
	String scenario;
	String notes;
	String requirements;
	String CRDNotes;
	Rubric rubric;
	int aspectCount;
	List<Hyperlink> webLinks;
	int originalityMinimum;
	int originalityWarning;
	Date dateCreated;
	Date dateUpdated;
	Date datePublished;
	Date dateRetired;
	String publicationStatus;
	String given;
	String referenceList;

	public List<Competency> getCompetencies() {
		this.competencies = ListUtils.defaultIfNull(this.competencies, new ArrayList<>());
		return this.competencies;
	}

	public List<Hyperlink> getWebLinks() {
		this.webLinks = ListUtils.defaultIfNull(this.webLinks, new ArrayList<>());
		return this.webLinks;
	}

	public Task(TaskModel model, List<? extends SupportingDocumentModel> documents) {
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
		this.competencies = model.getCompetencies().stream().map(c -> new Competency(c, this.taskId)).collect(Collectors.toList());
		this.supportingDocuments = documents.stream().map(d -> new SupportingDocument(d)).collect(Collectors.toList());
		this.introduction = model.getIntroduction();
		this.scenario = model.getScenario();
		this.notes = model.getNotes();
		this.requirements = model.getRequirements();
		this.CRDNotes = model.getCRDNotes();
		this.rubric = new Rubric(model.getRubric());
		this.aspectCount = model.getAspectCount();
		this.webLinks = model.getWebLinks().stream().map(l -> new Hyperlink(l)).collect(Collectors.toList());
		this.originalityMinimum = model.getOriginalityMinimum();
		this.originalityWarning = model.getOriginalityWarning();
		this.dateCreated = model.getDateCreated();
		this.dateUpdated = model.getDateUpdated();
		this.datePublished = model.getDatePublished();
		this.dateRetired = model.getDateRetired();
		this.publicationStatus = model.getPublicationStatus();
		this.given = model.getGiven();
		this.referenceList = model.getReferenceList();
	}

	@Override
	public int compareTo(Task o) {
		return Comparator.comparing(Task::getAssessmentOrder)
				.thenComparing(Task::getTaskOrder)
				.compare(this, o);
	}
}
