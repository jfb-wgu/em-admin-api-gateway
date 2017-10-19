package edu.wgu.dmadmin.domain.feedback;

import java.util.Date;
import java.util.UUID;

import edu.wgu.dmadmin.model.feedback.StudentFeedbackModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentFeedback {
	String studentId;
	UUID studentRatingId;
	int rating;
	String comments;
	int attempt;
	UUID taskId;
	Date dateRated;
	
	String assessmentCode;
	
	public StudentFeedback(StudentFeedbackModel model) {
		this.setStudentId(model.getStudentId());
		this.setStudentRatingId(model.getStudentRatingId());
		this.setRating(model.getRating());
		this.setComments(model.getComments());
		this.setAttempt(model.getAttempt());
		this.setTaskId(model.getTaskId());
		this.setDateRated(model.getDateRated());
		this.setAssessmentCode(model.getAssessmentCode());
	}
}
