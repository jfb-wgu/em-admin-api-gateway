package edu.wgu.dmadmin.model.publish;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskModel implements Comparable<TaskModel> {

	@Column(name = "course_name")
	String courseName;

	@Column(name = "course_code")
	String courseCode;

	@Column(name = "course_id")
	Long courseId;

	@Column(name = "assessment_name")
	String assessmentName;

	@Column(name = "assessment_code")
	String assessmentCode;

	@Column(name = "assessment_date")
	String assessmentDate;

	@Column(name = "assessment_type")
	String assessmentType;

	@Column(name = "assessment_order")
	int assessmentOrder;

	@Column(name = "assessment_id")
	UUID assessmentId;

	@Column(name = "task_name")
	String taskName;

	@Column(name = "task_id")
	UUID taskId;

	@Column(name = "task_order")
	int taskOrder;

	@Column(name = "avg_time_required")
	int averageTime;

	String description;

	@Frozen
	List<CompetencyModel> competencies;

	String introduction;
	String scenario;
	String notes;
	String requirements;

	@Column(name = "crd_notes")
	String CRDNotes;

	@Frozen
	RubricModel rubric;

	@Column(name = "aspect_count")
	int aspectCount;

	@Column(name = "web_links")
	List<HyperlinkModel> webLinks;

	@Column(name = "originality_minimum")
	int originalityMinimum;

	@Column(name = "originality_warning")
	int originalityWarning;

	@Column(name = "date_created")
	Date dateCreated;

	@Column(name = "date_updated")
	Date dateUpdated;

	@Column(name = "date_published")
	Date datePublished;

	@Column(name = "date_retired")
	Date dateRetired;

	@Column(name = "publication_status")
	String publicationStatus;

	String given;

	@Column(name="reference_list")
	String referenceList;

	@Override
	public int compareTo(TaskModel o) {
		return this.getAssessmentOrder() - o.getAssessmentOrder();
	}
}
