package edu.wgu.dmadmin.model.submission;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_id")
public class SubmissionModel {

	@PartitionKey(1)
	@Column(name = "student_id")
	public String studentId;

	@PartitionKey(2)
	@Column(name = "task_id")
	public UUID taskId;

	@PartitionKey(3)
	int attempt;

	@PartitionKey(0)
	@Column(name = "submission_id")
	UUID submissionId;
}
