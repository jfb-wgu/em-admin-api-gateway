package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name = "task_by_id", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class TaskByIdModel extends TaskModel {
    
    @PartitionKey(0)
    public UUID getTaskId() {
    	return taskId;
    }
	
    @PartitionKey(1)
    public UUID getAssessmentId() {
    	return assessmentId;
    }
	
	@PartitionKey(2)
	public Long getCourseId() {
		return courseId;
	}
    
    public TaskByIdModel(TaskModel model) {
    	this.populate(model);
    }
}
