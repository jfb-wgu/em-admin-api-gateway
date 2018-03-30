package edu.wgu.dmadmin.model.publish;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "task_by_course", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class TaskModel {

	@PartitionKey(0)
	@Column(name = "course_id")
	Long courseId;

	@Column(name = "assessment_name")
	String assessmentName;

	@Column(name = "assessment_code")
	String assessmentCode;

	@PartitionKey(1)
	@Column(name = "assessment_id")
	UUID assessmentId;

	@Column(name = "task_name")
	String taskName;

	@PartitionKey(2)
	@Column(name = "task_id")
	UUID taskId;

	@Column(name = "task_order")
	int taskOrder;
}
