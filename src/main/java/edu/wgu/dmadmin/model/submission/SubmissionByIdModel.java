package edu.wgu.dmadmin.model.submission;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_id", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class SubmissionByIdModel extends SubmissionModel {

	@PartitionKey(0)
	public UUID getSubmissionId() {
		return this.submissionId;
	}

	@PartitionKey(1)
	public String getStudentId() {
		return this.studentId;
	}

	@PartitionKey(2)
	public UUID getTaskId() {
		return this.taskId;
	}

	@PartitionKey(3)
	public int getAttempt() {
		return this.attempt;
	}

	public SubmissionByIdModel(SubmissionModel model) {
		this.populate(model);
	}
}
