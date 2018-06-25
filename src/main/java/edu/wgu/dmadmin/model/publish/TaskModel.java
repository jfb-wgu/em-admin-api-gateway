package edu.wgu.dmadmin.model.publish;

import java.util.List;
import java.util.UUID;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "task_by_id")
public class TaskModel {

	@Column(name = "assessment_name")
	String assessmentName;

	@Column(name = "assessment_code")
	String assessmentCode;

	@PartitionKey(1)
	@Column(name = "pams_assessment_id")
	Long pamsAssessmentId;

	@Column(name = "task_name")
	String taskName;

	@PartitionKey(0)
	@Column(name = "task_id")
	UUID taskId;

	@Column(name = "task_order")
	int taskOrder;
	
	@Frozen
    List<CompetencyModel> competencies;
	
	@Frozen
    RubricModel rubric;
}
