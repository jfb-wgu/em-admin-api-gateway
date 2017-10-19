package edu.wgu.dmadmin.domain.audit;

import java.util.Date;
import java.util.UUID;

import edu.wgu.dmadmin.model.audit.StatusLogModel;
import lombok.Data;

@Data
public class StatusLogEntry implements Comparable<StatusLogEntry> {
	UUID logId;
	String studentId;
	Date activityDate;
	
	String courseCode;
	UUID assessmentId;
	UUID taskId;
	UUID submissionId;
	String userId;
	String oldStatus;
	String newStatus;
	
	public StatusLogEntry(StatusLogModel model) {
		this.setLogId(model.getLogId());
		this.setActivityDate(model.getActivityDate());
		this.setAssessmentId(model.getAssessmentId());
		this.setCourseCode(model.getCourseCode());
		this.setStudentId(model.getStudentId());
		this.setSubmissionId(model.getSubmissionId());
		this.setOldStatus(model.getOldStatus());
		this.setNewStatus(model.getNewStatus());
		this.setTaskId(model.getTaskId());
		this.setUserId(model.getUserId());
	}
	
	@Override
	public int compareTo(StatusLogEntry o) {
		return this.activityDate.compareTo(o.getActivityDate());
	}
}
