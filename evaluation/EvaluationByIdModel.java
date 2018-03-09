package edu.wgu.dreammachine.model.evaluation;

import java.util.UUID;
import java.util.stream.Collectors;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dreammachine.domain.evaluation.Evaluation;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "evaluation_by_id", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class EvaluationByIdModel extends EvaluationModel {

	@PartitionKey(0)
	public UUID getEvaluationId() {
		return this.evaluationId;
	}

	@PartitionKey(1)
	public String getEvaluatorId() {
		return this.evaluatorId;
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

	public EvaluationByIdModel(Evaluation eval) {
		this.setStudentId(eval.getStudentId());
		this.setAttempt(eval.getAttempt());
		this.setComments(eval.getComments());
		this.setDateCompleted(eval.getDateCompleted());
		this.setDateStarted(eval.getDateStarted());
		this.setDateUpdated(eval.getDateUpdated());
		this.setEvaluationId(eval.getEvaluationId());
		this.setEvaluatorFirstName(eval.getEvaluatorFirstName());
		this.setEvaluatorLastName(eval.getEvaluatorLastName());
		this.setEvaluatorId(eval.getEvaluatorId());
		this.setMinutesSpent(eval.getMinutesSpent());
		this.setStatus(eval.getStatus());
		this.setSubmissionId(eval.getSubmissionId());
		this.setTaskId(eval.getTaskId());
		this.setAspects(eval.getAspects().values().stream()
				.collect(Collectors.toMap(a -> a.getAspectName(), a -> new EvaluationAspectModel(a))));
	}
}
