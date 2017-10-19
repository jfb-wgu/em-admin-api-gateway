package edu.wgu.dmadmin.model.submission;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_student_and_task", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class SubmissionByStudentAndTaskModel extends SubmissionModel {
	
    @PartitionKey(0)
    public String getStudentId() {
    	return studentId;
    }
    
    @PartitionKey(1)
    public UUID getTaskId() {
    	return taskId;
    }

    @PartitionKey(2)
    public int getAttempt() {
    	return attempt;
    }
    
    @PartitionKey(3)
    public UUID getSubmissionId() {
    	return submissionId;
    }
    
    public SubmissionByStudentAndTaskModel(SubmissionModel model) {
    	this.populate(model);
    }
}
