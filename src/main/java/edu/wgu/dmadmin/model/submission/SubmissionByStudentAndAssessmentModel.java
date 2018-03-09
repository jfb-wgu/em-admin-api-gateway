package edu.wgu.dmadmin.model.submission;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_student_and_assessment", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class SubmissionByStudentAndAssessmentModel extends SubmissionModel {

    @PartitionKey(0)
    public String getStudentId() {
        return studentId;
    }

    @PartitionKey(1)
    public UUID getAssessmentId() {
        return assessmentId;
    }

    @PartitionKey(2)
    public UUID getTaskId() {
        return taskId;
    }
    
    @PartitionKey(3)
    public int getAttempt() {
        return attempt;
    }

    @PartitionKey(4)
    public UUID getSubmissionId() {
        return submissionId;
    }
}
