package edu.wgu.dmadmin.model.audit;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name="status_log_by_student", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class StatusLogByStudentModel extends StatusLogModel {
	
	@PartitionKey(0)
	public String getStudentId() {
		return studentId;
	}
	
	@PartitionKey(1)
	public UUID getSubmissionId() {
		return submissionId;
	}

	@PartitionKey(2)
	public Date getActivityDate() {
		return activityDate;
	}
	
	@PartitionKey(3)
	public UUID getAssessmentId() {
		return assessmentId;
	}
	
	@PartitionKey(4)
	public UUID getTaskId() {
		return taskId;
	}
	
	@PartitionKey(5)
	public UUID getLogId() {
		return logId;
	}
	
	public StatusLogByStudentModel(StatusLogModel model) {
		this.populate(model);
	}
}
