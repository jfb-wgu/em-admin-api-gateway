package edu.wgu.dmadmin.domain.evaluation;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.wgu.dmadmin.domain.common.Attachment;
import edu.wgu.dmadmin.domain.common.UploadStatus;
import edu.wgu.dmadmin.model.evaluation.EvaluationAttachmentModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EvaluationAttachment extends Attachment {

    @JsonIgnore
    int attempt;

    @JsonIgnore
    String studentId;

    @JsonIgnore
    UUID taskId;

    @JsonIgnore
    UUID submissionId;

    @JsonIgnore
    UUID evaluationId;

    @JsonIgnore
    String evaluatorId;

    @JsonIgnore
    private String attachmentUrl = "dmevaluation/v1/attachment/%s";

    @JsonGetter("url")
    public String getDisplayUrl() {
        if (this.getIsUrl()) return this.getUrl();
        if (this.getAttachmentId() == null ||
                !UploadStatus.COMPLETE.equals(this.getUploadStatus())) return StringUtils.EMPTY;
        return String.format(this.attachmentUrl, this.getAttachmentId());
    }

    public EvaluationAttachment(EvaluationAttachmentModel metadata) {
        this.setTitle(metadata.getTitle());
        this.setUrl(metadata.getUrl());
        this.setMimeType(metadata.getMimeType());
        this.setSize(metadata.getSize());
        this.setUploadStatus(metadata.getUploadStatus());
        this.setEvaluatorId(metadata.getEvaluatorId());
        this.setEvaluationId(metadata.getEvaluationId());
        this.setStudentId(metadata.getStudentId());
        this.setTaskId(metadata.getTaskId());
        this.setSubmissionId(metadata.getSubmissionId());
        this.setEvaluationId(metadata.getEvaluationId());
        this.setAttachmentId(metadata.getEvaluationAttachmentId());
        this.setAttempt(metadata.getAttempt());
        this.setCreatedAt(metadata.getCreatedAt());
        this.setModifiedAt(metadata.getModifiedAt());
        this.setPath(metadata.getPath());
    }
}
