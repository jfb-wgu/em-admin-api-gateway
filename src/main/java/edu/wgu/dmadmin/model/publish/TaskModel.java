package edu.wgu.dmadmin.model.publish;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskModel {

	@Column(name = "course_id")
	Long courseId;

	@Column(name = "assessment_name")
	String assessmentName;

	@Column(name = "assessment_code")
	String assessmentCode;

	@Column(name = "assessment_id")
	UUID assessmentId;

	@Column(name = "task_name")
	String taskName;

	@Column(name = "task_id")
	UUID taskId;

	@Column(name = "task_order")
	int taskOrder;
}
