package edu.wgu.dmadmin.model.publish;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "task_by_course", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class TaskByCourseModel extends TaskModel {

	@PartitionKey(0)
	public Long getCourseId() {
		return this.courseId;
	}

	@PartitionKey(1)
	public UUID getAssessmentId() {
		return this.assessmentId;
	}

	@PartitionKey(2)
	public UUID getTaskId() {
		return this.taskId;
	}
}
