package edu.wgu.dmadmin.domain.publish;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.CharMatcher;

import edu.wgu.dmadmin.model.publish.TaskModel;
import lombok.Data;

@Data
public class TaskDashboard {
	private Map<String, Assessment> assessments;
	
	// Return the assessments sorted by last update descending
	public List<Assessment> getAssessments() {
		return assessments.values().stream().sorted().collect(Collectors.toList());
	}

	public TaskDashboard(List<? extends TaskModel> tasks) {
		tasks.forEach(task -> { 
			Assessment assessment = this.getAssessment(task.getAssessmentCode(), task.getAssessmentId(), task.getCourseId(), task.getCourseCode(), task.getCourseName(), task.getAssessmentName());
			assessment.addTask(task);
		});
	}

	public Assessment getAssessment(String assessmentCode, UUID assessmentId, Long courseId, String courseCode, String courseName, String assessmentName) {
		if (assessments == null) assessments = new HashMap<String, Assessment>();
		
		if (assessments.containsKey(assessmentCode)) {
			return assessments.get(assessmentCode);
		} else {
			Assessment assessment = new Assessment(assessmentCode, assessmentId, courseId, courseCode, courseName, assessmentName);
			assessments.put(assessmentCode, assessment);
			return assessment;
		}
	}
	
	@Data
	@JsonPropertyOrder({"assessmentCode", "assessmentId", "assessmentName", "courseId", "courseCode", "courseName", "dateUpdated", "tasks"})
	private class Assessment implements Comparable<Assessment> {
		private String assessmentCode;
		private UUID assessmentId;
		private Long courseId;
		private String courseCode;
		private String courseName;
		private String assessmentName;
		private SortedSet<Task> tasks;
		
		public Assessment(String assessmentCode, UUID assessmentId, Long courseId, String courseCode, String courseName, String assessmentName) {
			this.assessmentCode = assessmentCode;
			this.assessmentId = assessmentId;
			this.courseId = courseId;
			this.courseCode = courseCode;
			this.courseName = courseName;
			this.assessmentName = assessmentName;
		}
		
		public SortedSet<Task> getTasks() {
			return tasks;
		}
		
		public void addTask(TaskModel task) {
			if (tasks == null) tasks = new TreeSet<Task>();
			tasks.add(new Task(task));
		}

		public Date getDateUpdated() {
			return this.getTasks().stream().map(t -> t.getDateUpdated()).max(Date::compareTo).orElseThrow(() -> new IllegalArgumentException());
		}
		
		@Override
		public boolean equals(Object assessment) {
			return assessment instanceof Assessment && this.assessmentId.equals(((Assessment)assessment).assessmentId);
		}
		
		@Override
		public int hashCode() {
			return Integer.parseInt(CharMatcher.DIGIT.retainFrom(this.assessmentId.toString()));
		}

		@Override
		public int compareTo(Assessment o) {
			return o.getDateUpdated().compareTo(this.getDateUpdated());
		}
	}
	
	@Data
	private class Task implements Comparable<Task> {
		private String taskName;
		private UUID taskId;
		private int order;
		private Date dateUpdated;
		
		public Task(TaskModel task) {
			this.taskName = task.getTaskName();
			this.taskId = task.getTaskId();
			this.order = task.getTaskOrder();
			this.dateUpdated = task.getDateUpdated();
		}
		
		@Override
		public boolean equals(Object task) {
			return task instanceof Task && this.taskId.equals(((Task)task).taskId);
		}
		
		@Override
		public int hashCode() {
			return Integer.parseInt(CharMatcher.DIGIT.retainFrom(this.taskId.toString()));
		}
		
		@Override
		public int compareTo(Task o) {
			return this.order - o.order;
		}
	}
}
