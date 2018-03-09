package edu.wgu.dmadmin.model.submission;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_student_and_task", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class SubmissionByStudentAndTaskModel extends SubmissionModel {

	@PartitionKey(0)
	public String getStudentId() {
		return this.studentId;
	}

	@PartitionKey(1)
	public UUID getTaskId() {
		return this.taskId;
	}

	@PartitionKey(2)
	public int getAttempt() {
		return this.attempt;
	}

	@PartitionKey(3)
	public UUID getSubmissionId() {
		return this.submissionId;
	}
}
