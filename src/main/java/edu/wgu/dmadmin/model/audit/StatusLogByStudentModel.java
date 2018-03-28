package edu.wgu.dmadmin.model.audit;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name="status_log_by_student", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class StatusLogByStudentModel extends StatusLogModel {
	
	@PartitionKey(0)
	public String getStudentId() {
		return this.studentId;
	}
	
	@PartitionKey(1)
	public UUID getSubmissionId() {
		return this.submissionId;
	}

	@PartitionKey(2)
	public Date getActivityDate() {
		return this.activityDate;
	}
	
	@PartitionKey(3)
	public Long getAssessmentId() {
		return this.assessmentId;
	}
	
	@PartitionKey(4)
	public UUID getTaskId() {
		return this.taskId;
	}
	
	@PartitionKey(5)
	public UUID getLogId() {
		return this.logId;
	}
}
