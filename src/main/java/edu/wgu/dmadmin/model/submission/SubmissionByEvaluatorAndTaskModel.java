package edu.wgu.dmadmin.model.submission;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_evaluator_and_task", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class SubmissionByEvaluatorAndTaskModel extends SubmissionModel {

	@PartitionKey(0)
	public String getEvaluatorId() {
		return evaluatorId;
	}

	@PartitionKey(1)
	public UUID getTaskId() {
		return taskId;
	}

	@PartitionKey(2)
	public int getAttempt() {
		return attempt;
	}

	@PartitionKey(3)
	public String getStudentId() {
		return studentId;
	}

	@PartitionKey(4)
	public UUID getSubmissionId() {
		return submissionId;
	}

	public SubmissionByEvaluatorAndTaskModel(SubmissionModel model) {
		this.populate(model);
	}
}
