package edu.wgu.dreammachine.model.evaluation;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "evaluation_by_evaluator_and_status", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class EvaluationByEvaluatorAndStatusModel extends EvaluationModel {
	
	@PartitionKey(0)
    public String getEvaluatorId() {
		return this.evaluatorId;
	}
	
	@PartitionKey(1)
    public String getStatus() {
		return this.status;
	}
	
	@PartitionKey(2)
    public UUID getSubmissionId() {
		return this.submissionId;
	}
	
	@PartitionKey(3)
    public String getStudentId() {
		return this.studentId;
	}

	@PartitionKey(4)
    public UUID getTaskId() {
		return this.taskId;
	}

	@PartitionKey(5)
    public UUID getEvaluationId() {
		return this.evaluationId;
	}
}
