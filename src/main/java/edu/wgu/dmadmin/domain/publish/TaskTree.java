package edu.wgu.dmadmin.domain.publish;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import com.google.common.base.CharMatcher;

import edu.wgu.dmadmin.model.publish.TaskModel;
import lombok.Data;

@Data
public class TaskTree {
	private Map<Long, Course> courses;
	
	public TaskTree(List<? extends TaskModel> tasks) {
		tasks.forEach(task -> { 
			Course course = this.getCourse(task.getCourseCode(), task.getCourseId());
			Assessment assessment = course.getAssessment(task.getAssessmentCode(), task.getAssessmentId());
			assessment.addTask(task);
		});
	}
	
	public Collection<Assessment> getAssessmentsForCourse(Long courseId) {
		return courses.get(courseId).assessments.values();
	}
	
	public Course getCourse(String courseCode, Long courseId) {
		if (courses == null) courses = new HashMap<Long, Course>();
		
		if (courses.containsKey(courseId)) {
			return courses.get(courseId);
		} else {
			Course course = new Course(courseCode, courseId);
			courses.put(courseId, course);
			return course;
		}
	}
	
	@Data
	private class Course {
		private String courseCode;
		private Long courseId;
		private Map<UUID, Assessment> assessments;
		
		public Course(String courseCode, Long courseId) {
			this.courseCode = courseCode;
			this.courseId = courseId;
		}
		
		public Assessment getAssessment(String assessmentCode, UUID assessmentId) {
			if (assessments == null) assessments = new HashMap<UUID, Assessment>();
			
			if (assessments.containsKey(assessmentId)) {
				return assessments.get(assessmentId);
			} else {
				Assessment assessment = new Assessment(assessmentCode, assessmentId);
				assessments.put(assessmentId, assessment);
				return assessment;
			}
		}
	}
	
	@Data
	private class Assessment {
		private String assessmentCode;
		private UUID assessmentId;
		private SortedSet<Task> tasks;
		
		public Assessment(String assessmentCode, UUID assessmentId) {
			this.assessmentCode = assessmentCode;
			this.assessmentId = assessmentId;
		}
		
		public SortedSet<Task> getTasks() {
			return tasks;
		}
		
		public void addTask(TaskModel task) {
			if (tasks == null) tasks = new TreeSet<Task>();
			tasks.add(new Task(task));
		}
		
		@Override
		public boolean equals(Object assessment) {
			return assessment instanceof Assessment && this.assessmentId.equals(((Assessment)assessment).assessmentId);
		}
		
		@Override
		public int hashCode() {
			return Integer.parseInt(CharMatcher.DIGIT.retainFrom(this.assessmentId.toString()));
		}
	}
	
	@Data
	private class Task implements Comparable<Object> {
		private String taskName;
		private UUID taskId;
		private int order;
		
		public Task(TaskModel task) {
			this.taskName = task.getTaskName();
			this.taskId = task.getTaskId();
			this.order = task.getTaskOrder();
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
		public int compareTo(Object o) {
			if (o instanceof Task) {
				return this.order - ((Task)o).order;
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
}
