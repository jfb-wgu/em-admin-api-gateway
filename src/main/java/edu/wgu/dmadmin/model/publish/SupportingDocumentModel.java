package edu.wgu.dmadmin.model.publish;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SupportingDocumentModel {
	@Column(name="attachment_id")
	UUID attachmentId;
	
	@Column(name="task_id")
	UUID taskId;
	
	@Column(name="assessment_code")
	String assessmentCode;
	
	@Column(name="course_code")
	String courseCode;
	
    String title;
    
    @Column(name="created_at")
    Date createdAt;
    
    @Column(name="modified_at")
    Date modifiedAt;
    
    @Column(name="upload_status")
    String uploadStatus;
    
    Long size;

    @Column(name="mime_type")
    String mimeType;
    
    String path;
    
	public void populate(SupportingDocumentModel model) {
		this.setAttachmentId(model.getAttachmentId());
		this.setCreatedAt(model.getCreatedAt());
		this.setModifiedAt(model.getModifiedAt());
		this.setUploadStatus(model.getUploadStatus());
		this.setTitle(model.getTitle());
		this.setMimeType(model.getMimeType());
		this.setSize(model.getSize());
		this.setCourseCode(model.getCourseCode());
		this.setAssessmentCode(model.getAssessmentCode());
		this.setTaskId(model.getTaskId());
		this.setPath(model.getPath());
	}
}
