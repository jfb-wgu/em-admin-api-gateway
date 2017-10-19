package edu.wgu.dmadmin.model.assessment;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.assessment.Evaluation;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name = "evaluation_by_id", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class EvaluationByIdModel extends EvaluationModel {
	
	@PartitionKey(0)
	public UUID getEvaluationId() {
		return evaluationId;
	}
	
	@PartitionKey(1)
	public String getEvaluatorId() {
		return evaluatorId;
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

	public EvaluationByIdModel(Evaluation evaluation) {
		this.evaluationId = evaluation.getEvaluationId();
		this.evaluatorId = evaluation.getEvaluatorId();
		this.submissionId = evaluation.getSubmissionId();
		this.status = evaluation.getStatus();
		this.minutesSpent = evaluation.getMinutesSpent();
		this.dateStarted = evaluation.getDateStarted();
		this.dateCompleted = evaluation.getDateCompleted();
		this.dateUpdated = evaluation.getDateUpdated();
		this.attachments = evaluation.getAttachments();
		this.attempt = evaluation.getAttempt();
		this.studentId = evaluation.getStudentId();
		this.taskId = evaluation.getTaskId();
		this.evaluatorFirstName = evaluation.getEvaluatorFirstName();
		this.evaluatorLastName = evaluation.getEvaluatorLastName();
		
		if (evaluation.getScoreReport() != null) {
			this.scoreReport = new ScoreReportModel(evaluation.getScoreReport());
		}
	}
	
	public EvaluationByIdModel(EvaluationModel model) {
		this.populate(model);
	}
}
