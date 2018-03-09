package edu.wgu.dmadmin.model.submission;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_status_and_task", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class SubmissionByStatusAndTaskModel extends SubmissionModel {

	@PartitionKey(0)
	public String getStatus() {
		return status;
	}

	@PartitionKey(1)
	public UUID getTaskId() {
		return taskId;
	}

	@PartitionKey(2)
	public String getStudentId() {
		return studentId;
	}

	@PartitionKey(3)
	public int getAttempt() {
		return attempt;
	}

	@PartitionKey(4)
	public UUID getSubmissionId() {
		return submissionId;
	}

	public SubmissionByStatusAndTaskModel(SubmissionModel model) {
		this.populate(model);
	}
}
