package edu.wgu.dmadmin.domain.submission;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@SuppressWarnings("boxing")
public class Attachment {

    String title;
    String url;
    String mimeType;
    Long size;
    Boolean isUrl;
    
    @JsonIgnore
    Boolean isTaskDocument;
    
    @JsonIgnore
    String studentId;
    
    @JsonIgnore
    UUID submissionId;
    
    @JsonIgnore
    String assessmentCode;
    
    @JsonIgnore
    String courseCode;
    
    @JsonIgnore
    UUID taskId;

    @JsonIgnore
    private String studentAttachmentUrl = "dmsubmission/v1/student/%s/submission/%s/attachment/%s";

    @JsonIgnore
    private String supportingDocumentUrl = "dmsubmission/v1/assessment/supportingdocument/course/%s/assessment/%s/task/%s/%s";

    public Attachment(AttachmentModel attachment, String studentId, UUID submissionId) {
        this.title = attachment.getTitle();
        this.url = attachment.getUrl();
        this.mimeType = attachment.getMimeType();
        this.size = attachment.getSize();
        this.isUrl = attachment.getIsUrl();
        this.studentId = studentId;
        this.submissionId = submissionId;
        this.isTaskDocument = Boolean.FALSE;
    }
    
    public Attachment(AttachmentModel attachment, String courseCode, String assessmentCode, UUID taskId) {
        this.title = attachment.getTitle();
        this.url = attachment.getUrl();
        this.mimeType = attachment.getMimeType();
        this.size = attachment.getSize();
        this.isUrl = attachment.getIsUrl();
        this.courseCode = courseCode;
        this.assessmentCode = assessmentCode;
        this.taskId = taskId;
        this.isTaskDocument = Boolean.TRUE;
    }

    @JsonGetter("url")
	public String getDisplayUrl() {
    	if (isUrl != null && isUrl) return url;
    	
    	String builtURL;
    	
        if (isTaskDocument != null && isTaskDocument) {
        	if (courseCode == null || assessmentCode == null || taskId == null || title == null) return null;
        	builtURL = String.format(supportingDocumentUrl, courseCode, assessmentCode, taskId, title);
        } else {
        	if (studentId == null || submissionId == null || title == null) return null;
        	builtURL = String.format(studentAttachmentUrl, studentId, submissionId, title);
        }
        
        return builtURL;
    }
}
