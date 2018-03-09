package edu.wgu.dmadmin.model.audit;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;

import lombok.Data;

@Data
public class StatusLogModel {
	
	@Column(name="log_id")
	UUID logId;
	
	@Column(name="student_id")
	String studentId;
	
	@Column(name="activity_date")
	Date activityDate;
	
	@Column(name="course_code")
	String courseCode;
	
	@Column(name="assessment_id")
	UUID assessmentId;
	
	@Column(name="task_id")
	UUID taskId;
	
	@Column(name="submission_id")
	UUID submissionId;
	
	@Column(name="user_id")
	String userId;
	
	@Column(name="old_status")
	String oldStatus;
	
	@Column(name="new_status")
	String newStatus;
}
