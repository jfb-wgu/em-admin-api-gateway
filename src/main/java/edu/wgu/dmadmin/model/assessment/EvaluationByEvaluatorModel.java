package edu.wgu.dmadmin.model.assessment;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name = "evaluation_by_evaluator", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class EvaluationByEvaluatorModel extends EvaluationModel {
	
	@PartitionKey(0)
	public String getEvaluatorId() {
		return evaluatorId;
	}
	
	@PartitionKey(1)
	public String getStatus() {
		return status;
	}
	
	@PartitionKey(2)
	public UUID getSubmissionId() {
		return submissionId;
	}

	@PartitionKey(3)
	public String getStudentId() {
		return studentId;
	}
	
	@PartitionKey(4)
	public UUID getTaskId() {
		return taskId;
	}
	
	@PartitionKey(5)
	public int getAttempt() {
		return attempt;
	}
	
	@PartitionKey(6)
	public UUID getEvaluationId() {
		return evaluationId;
	}
	
	public EvaluationByEvaluatorModel(EvaluationModel model) {
		this.populate(model);
	}
}
