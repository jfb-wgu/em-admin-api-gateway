package edu.wgu.dmadmin.model.assessment;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name = "evaluation_by_submission", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class EvaluationBySubmissionModel extends EvaluationModel {
	
	@PartitionKey(0)
	public UUID getSubmissionId() {
		return submissionId;
	}
	
	@PartitionKey(1)
	public String getEvaluatorId() {
		return evaluatorId;
	}
	
	@PartitionKey(2)
	public String getStudentId() {
		return studentId;
	}
	
	@PartitionKey(3)
	public UUID getTaskId() {
		return taskId;
	}
	
	@PartitionKey(4)
	public int getAttempt() {
		return attempt;
	}


	@PartitionKey(5)
	public UUID getEvaluationId() {
		return evaluationId;
	}
	
	public EvaluationBySubmissionModel(UserModel evaluator, SubmissionModel submission) {
		super(evaluator, submission);
	}
	
	public EvaluationBySubmissionModel(EvaluationModel model) {
		this.populate(model);
	}
}
