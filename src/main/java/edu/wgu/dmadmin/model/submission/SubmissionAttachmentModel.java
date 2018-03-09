package edu.wgu.dmadmin.model.submission;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_attachment")
public class SubmissionAttachmentModel {

	@PartitionKey(0)
	@Column(name = "submission_id")
	UUID submissionId;

	@PartitionKey(1)
	@Column(name = "student_id")
	String studentId;

	@PartitionKey(2)
	@Column(name="attachment_id")
	UUID attachmentId;

	String title;

	@Column(name = "mime_type")
	String mimeType;

	@Column(name = "upload_status")
	String uploadStatus;

	@Column(name = "created_at")
	Date createdAt;

	@Column(name = "modified_at")
	Date modifiedAt;

	Long size;
	String url;
	String path;

	@Column(name="soft_delete")
	boolean softDelete;

	@Column(name="originality_score")
	Float originalityScore;

	@Column(name="originality_view_url")
	String originalityViewUrl;

	@Column(name="originality_edit_url")
	String originalityEditUrl;

	@Column(name="unicheck_file_id")
	Long unicheckFileId;

	@Column(name="unicheck_file_uuid")
	String unicheckFileUUID;

	@Column(name="unicheck_directory_id")
	Long unicheckDirectoryId;

	@Column(name="unicheck_check_id")
	Long unicheckCheckId;
	
	@Column(name="unicheck_check_progress")
	Float unicheckCheckProgress;
	
	@Column(name="unicheck_upload_error")
	String uploadError;
	
	@Column(name="unicheck_check_error")
	String checkError;
}
