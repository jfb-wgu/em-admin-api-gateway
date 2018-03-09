package edu.wgu.dmadmin.model.audit;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.audit.StatusLogEntry;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.util.DateUtil;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name="status_log_by_assessment", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class StatusLogByAssessmentModel extends StatusLogModel {
	
	@PartitionKey(0)
	public UUID getAssessmentId() {
		return assessmentId;
	}
	
	@PartitionKey(1)
	public Date getActivityDate() {
		return activityDate;
	}

	@PartitionKey(2)
	public UUID getTaskId() {
		return taskId;
	}
			
	@PartitionKey(3)
	public String getStudentId() {
		return studentId;
	}

	@PartitionKey(4)
	public UUID getSubmissionId() {
		return submissionId;
	}
	
	@PartitionKey(5)
	public UUID getLogId() {
		return logId;
	}
	
	public StatusLogByAssessmentModel(StatusLogModel model) {
		this.populate(model);
	}

	public StatusLogByAssessmentModel(StatusLogEntry logEntry) {
		this.logId = logEntry.getLogId();
		this.studentId = logEntry.getStudentId();
		this.activityDate = logEntry.getActivityDate();
		this.courseCode = logEntry.getCourseCode();
		this.assessmentId = logEntry.getAssessmentId();
		this.taskId = logEntry.getTaskId();
		this.submissionId = logEntry.getSubmissionId();
		this.userId = logEntry.getUserId();
		this.oldStatus = logEntry.getOldStatus();
		this.newStatus = logEntry.getNewStatus();
	}

	public StatusLogByAssessmentModel(String oldStatus, SubmissionByIdModel newSub, String userId) {
		this.logId = UUID.randomUUID();
		this.studentId = newSub.getStudentId();
		this.activityDate = DateUtil.getZonedNow();
		this.courseCode = newSub.getCourseCode();
		this.assessmentId = newSub.getAssessmentId();
		this.taskId = newSub.getTaskId();
		this.submissionId = newSub.getSubmissionId();
		this.userId = userId;
		this.oldStatus = oldStatus;
		this.newStatus = newSub.getStatus();
	}
}
