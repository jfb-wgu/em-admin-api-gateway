package edu.wgu.dmadmin.domain.submission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wgu.dmadmin.domain.common.Attachment;
import edu.wgu.dmadmin.model.submission.SubmissionAttachmentModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SubmissionAttachment extends Attachment {
	
	private static final Logger logger = LoggerFactory.getLogger(SubmissionAttachment.class);
	
	Float originalityScore;
	String originalityReportURL;
	List<Error> originalityErrors = new ArrayList<>();

	@JsonIgnore
	String studentId;

	@JsonIgnore
	UUID submissionId;

	@JsonIgnore
	private String attachmentUrl = "dmsubmission/v1/student/%s/submission/%s/attachmentid/%s";

	public SubmissionAttachment(SubmissionAttachmentModel attachment) {
		this.setAttachmentId(attachment.getAttachmentId());
		this.setTitle(attachment.getTitle());
		this.setUrl(attachment.getUrl());
		this.setMimeType(attachment.getMimeType());
		this.setSize(attachment.getSize());
		this.setCreatedAt(attachment.getCreatedAt());
		this.setModifiedAt(attachment.getModifiedAt());
		this.setUploadStatus(attachment.getUploadStatus());
		this.setStudentId(attachment.getStudentId());
		this.setSubmissionId(attachment.getSubmissionId());
		this.setPath(attachment.getPath());
		this.setOriginalityReportURL(attachment.getOriginalityViewUrl());
		this.setOriginalityScore(attachment.getOriginalityScore());
		
		if (StringUtils.isNotBlank(attachment.getUploadError())) {
			this.originalityErrors.addAll(parseErrors(attachment.getUploadError()));
		}
		
		if (StringUtils.isNotBlank(attachment.getCheckError())) {
			this.originalityErrors.addAll(parseErrors(attachment.getCheckError()));
		}
	}

	@JsonGetter("url")
	public String getDisplayUrl() {
		if (this.getIsUrl()) return this.getUrl();
		
		if (ObjectUtils.allNotNull(this.studentId, this.submissionId, this.getAttachmentId())) {
			return String.format(this.attachmentUrl, this.studentId, this.submissionId, this.getAttachmentId());
		}
		
		return null;
	}
	
	private static List<Error> parseErrors(String errorArray) {
		List<Error> errors = new ArrayList<>();
	
		try {			
			ObjectMapper mapper = new ObjectMapper();
			errors = mapper.readValue(errorArray, new TypeReference<List<Error>>(){});
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		
		return errors;
	}
}
