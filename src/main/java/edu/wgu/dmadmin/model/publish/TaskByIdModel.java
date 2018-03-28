package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name = "task_by_id", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class TaskByIdModel extends TaskModel {

	@PartitionKey(0)
	public UUID getTaskId() {
		return this.taskId;
	}

	@PartitionKey(1)
	public Long getAssessmentId() {
		return this.assessmentId;
	}
}
