package edu.wgu.dmadmin.model.evaluation;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.evaluation.EvaluationAttachment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "evaluation_attachment")
public class EvaluationAttachmentModel {
    @PartitionKey(0)
    @Column(name = "student_id")
    String studentId;

    @PartitionKey(1)
    @Column(name = "task_id")
    UUID taskId;

    @PartitionKey(2)
    @Column(name = "submission_id")
    UUID submissionId;

    @PartitionKey(3)
    @Column(name = "evaluation_id")
    UUID evaluationId;

    @PartitionKey(4)
    @Column(name = "evaluation_attachment_id")
    UUID evaluationAttachmentId;

    @Column(name = "evaluator_id")
    String evaluatorId;

    @Column
    String title;

    @Column(name = "created_at")
    Date createdAt;

    @Column(name = "modified_at")
    Date modifiedAt;

    @Column(name = "upload_status")
    String uploadStatus;

    @Column(name="size")
    Long size;

    @Column(name = "mime_type")
    String mimeType;

    @Column(name = "path")
    String path;

    @Column(name = "url")
    String url;

    @Column(name = "attempt")
    int attempt;

    public EvaluationAttachmentModel(EvaluationAttachment attachment) {
        this.studentId = attachment.getStudentId();
        this.taskId = attachment.getTaskId();
        this.submissionId = this.getSubmissionId();
        this.evaluationId = this.getEvaluationId();
        this.evaluationAttachmentId = attachment.getAttachmentId();
        this.evaluatorId = attachment.getEvaluatorId();
        this.title = attachment.getTitle();
        this.createdAt = attachment.getCreatedAt();
        this.modifiedAt = attachment.getModifiedAt();
        this.uploadStatus = attachment.getUploadStatus();
        this.size = attachment.getSize();
        this.mimeType = attachment.getMimeType();
        this.path = attachment.getPath();
        this.url = attachment.getUrl();
        this.attempt = attachment.getAttempt();
    }

    public void populate(EvaluationAttachmentModel model){
        this.studentId = model.getStudentId();
        this.taskId = model.getTaskId();
        this.submissionId = this.getSubmissionId();
        this.evaluationId = this.getEvaluationId();
        this.evaluationAttachmentId = model.getEvaluationAttachmentId();
        this.evaluatorId = model.getEvaluatorId();
        this.title = model.getTitle();
        this.createdAt = model.getCreatedAt();
        this.modifiedAt = model.getModifiedAt();
        this.uploadStatus = model.getUploadStatus();
        this.size = model.getSize();
        this.mimeType = model.getMimeType();
        this.path = model.getPath();
        this.url = model.getUrl();
        this.attempt = model.getAttempt();

    }
}
