package edu.wgu.dmadmin.model.submission;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_student_and_task", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class SubmissionByStudentAndTaskModel {

	@PartitionKey(0)
	@Column(name = "student_id")
	public String studentId;

	@PartitionKey(1)
	@Column(name = "task_id")
	public UUID taskId;

	@PartitionKey(2)
	int attempt;

	@PartitionKey(3)
	@Column(name = "submission_id")
	UUID submissionId;
}
