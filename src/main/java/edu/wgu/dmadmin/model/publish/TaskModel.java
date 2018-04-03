package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TaskModel {

	@Column(name = "assessment_name")
	String assessmentName;

	@Column(name = "assessment_code")
	String assessmentCode;

	@Column(name = "assessment_id")
	Long assessmentId;

	@Column(name = "task_name")
	String taskName;

	@Column(name = "task_id")
	UUID taskId;

	@Column(name = "task_order")
	int taskOrder;
}
