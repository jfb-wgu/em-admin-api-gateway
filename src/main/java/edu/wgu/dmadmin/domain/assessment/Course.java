package edu.wgu.dmadmin.domain.assessment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import edu.wgu.dmadmin.model.publish.TaskModel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jessica Pamdeth
 */

@Data
@NoArgsConstructor
public class Course {
	private String courseCode;
	private Long courseId;
	private Map<UUID, Assessment> assessments;
	private boolean requestFeedback;
	
	@JsonGetter("assessments")
	public List<Assessment> getAssessments() {
		if (assessments == null) assessments = new HashMap<UUID, Assessment>();
		
		List<Assessment> result = new ArrayList<Assessment>();
		result.addAll(assessments.values());
		Collections.sort(result);
		return result;
	}

	public Assessment getAssessment(TaskModel task) {
		if (assessments == null) assessments = new HashMap<UUID, Assessment>();
		
		if (this.assessments.containsKey(task.getAssessmentId())) {
			return this.assessments.get(task.getAssessmentId());
		} else {
			Assessment assessment = new Assessment(task);
			this.assessments.put(assessment.getAssessmentId(), assessment);
			return assessment;
		}
	}
	
	public Course(List<? extends TaskModel> tasks) {
		this.courseCode = tasks.get(0).getCourseCode();
		this.courseId = tasks.get(0).getCourseId();
		
		tasks.forEach(task -> {
			Assessment assessment = this.getAssessment(task);
			assessment.addAssessmentTask(task);
		});
	}
}
