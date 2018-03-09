package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name = "task_by_assessment", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class TaskByAssessmentModel extends TaskModel {

	@PartitionKey(0)
	public UUID getAssessmentId() {
		return this.assessmentId;
	}

	@PartitionKey(1)
	public Long getCourseId() {
		return this.courseId;
	}

	@PartitionKey(2)
	public UUID getTaskId() {
		return this.taskId;
	}
}
