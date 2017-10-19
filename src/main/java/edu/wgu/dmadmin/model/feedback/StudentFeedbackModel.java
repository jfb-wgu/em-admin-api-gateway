package edu.wgu.dmadmin.model.feedback;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.feedback.StudentFeedback;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "student_feedback", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class StudentFeedbackModel {
	
	@PartitionKey(0)
	@Column(name="student_id")
	String studentId;
	
	@PartitionKey(1)
	@Column(name="student_rating_id")
	UUID studentRatingId;
	
	int rating;
	String comments;
	int attempt;
	
	@Column(name="task_id")
	UUID taskId;
	
	@Column(name="date_rated")
	Date dateRated;
	
	@Column(name = "assessment_code")
	String assessmentCode;
	
	public StudentFeedbackModel(StudentFeedback feedback) {
		this.setStudentId(feedback.getStudentId());
		this.setStudentRatingId(feedback.getStudentRatingId());
		this.setRating(feedback.getRating());
		this.setComments(feedback.getComments());
		this.setAttempt(feedback.getAttempt());
		this.setTaskId(feedback.getTaskId());
		this.setDateRated(feedback.getDateRated());
		this.setAssessmentCode(feedback.getAssessmentCode());
	}
}
