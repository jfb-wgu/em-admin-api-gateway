package edu.wgu.dmadmin.model.audit;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name="status_log_by_assessment")
public class StatusLogModel {

	@PartitionKey(5)
	@Column(name="log_id")
	UUID logId;

	@PartitionKey(3)
	@Column(name="student_id")
	String studentId;

	@PartitionKey(1)
	@Column(name="activity_date")
	Date activityDate;
	
	@Column(name="course_code")
	String courseCode;

	@PartitionKey(0)
	@Column(name="assessment_id")
	UUID assessmentId;

	@PartitionKey(2)
	@Column(name="task_id")
	UUID taskId;

	@PartitionKey(4)
	@Column(name="submission_id")
	UUID submissionId;
	
	@Column(name="user_id")
	String userId;
	
	@Column(name="old_status")
	String oldStatus;
	
	@Column(name="new_status")
	String newStatus;
}
