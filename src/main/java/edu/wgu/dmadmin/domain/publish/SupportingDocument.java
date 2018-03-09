package edu.wgu.dmadmin.domain.publish;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.wgu.dmadmin.domain.common.Attachment;
import edu.wgu.dmadmin.domain.common.UploadStatus;
import edu.wgu.dmadmin.model.publish.SupportingDocumentModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SupportingDocument extends Attachment {

	@JsonIgnore
	String assessmentCode;

	@JsonIgnore
	String courseCode;

	UUID taskId;

	@JsonIgnore
	private String supportingDocumentUrl = "dmpublish/v1/supportingdocument/attachmentid/%s";

	public SupportingDocument(SupportingDocumentModel attachment) {
		this.setAttachmentId(attachment.getAttachmentId());
		this.setTitle(attachment.getTitle());
		this.setMimeType(attachment.getMimeType());
		this.setSize(attachment.getSize());
		this.setCourseCode(attachment.getCourseCode());
		this.setAssessmentCode(attachment.getAssessmentCode());
		this.setTaskId(attachment.getTaskId());
		this.setCreatedAt(attachment.getCreatedAt());
		this.setModifiedAt(attachment.getModifiedAt());
		this.setUploadStatus(UploadStatus.COMPLETE);
		this.setPath(attachment.getPath());
	}

	@JsonGetter("url")
	public String getDisplayUrl() {
		return String.format(this.supportingDocumentUrl, this.getAttachmentId().toString());
	}
}
