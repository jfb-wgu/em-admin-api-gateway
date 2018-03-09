package edu.wgu.dmadmin.model.evaluation;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Table(keyspace = "dm", name = "evaluation_attachment_by_id", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class EvaluationAttachmentByIdModel extends EvaluationAttachmentModel {
    @PartitionKey(0)
    public UUID getEvaluationAttachmentId() {
        return this.evaluationAttachmentId;
    }

    @PartitionKey(1)
    public String getStudentId() {
        return this.studentId;
    }

    @PartitionKey(2)
    public UUID getTaskId() {
        return this.taskId;
    }

    @PartitionKey(3)
    public UUID getSubmissionId() {
        return this.submissionId;
    }

    @PartitionKey(4)
    public UUID getEvaluationId() {
        return this.evaluationId;
    }

    public EvaluationAttachmentByIdModel(EvaluationAttachmentModel evaluationAttachment) {
        this.populate(evaluationAttachment);
    }
}
