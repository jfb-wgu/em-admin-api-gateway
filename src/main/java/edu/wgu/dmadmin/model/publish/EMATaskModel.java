package edu.wgu.dmadmin.model.publish;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "task_by_course")
public class EMATaskModel {

	@Column(name = "assessment_name")
	String assessmentName;

	@Column(name = "assessment_code")
	String assessmentCode;

	@PartitionKey(0)
	@Column(name = "assessment_id")
	Long assessmentId;

	@Column(name = "task_name")
	String taskName;

	@PartitionKey(1)
	@Column(name = "task_id")
	UUID taskId;

	@Column(name = "task_order")
	int taskOrder;
}
