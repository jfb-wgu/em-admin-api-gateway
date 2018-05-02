package edu.wgu.dmadmin.repo.oracle;

import java.util.Date;
import java.util.UUID;

import edu.wgu.dmadmin.model.audit.StatusLogModel;
import lombok.Data;

@Data
public class StatusEntry implements Comparable<StatusEntry> {

	String studentId;
	Long assessmentId;
	UUID taskId;
	String status;
	Date activityDate;
	String evaluatorId;
	UUID submissionId;
	Date date;
	
	public StatusEntry(DRF drf, DRFTask task) {
		this.assessmentId = Long.parseLong(drf.getTitle());
		this.taskId = UUID.fromString(task.getTaskId());
		this.studentId = drf.getWguainf().getSpriden().getBannerId();
		this.status = task.getStatus();
		this.activityDate = task.getActivityDate();
		this.evaluatorId = task.getEvaluatorId();
		this.date = task.getActivityDate();
	}
	
	public StatusEntry(StatusLogModel model) {
		this.submissionId = model.getSubmissionId();
		this.assessmentId = model.getPamsAssessmentId();
		this.taskId = model.getTaskId();
		this.studentId = model.getStudentId();
		this.status = model.getNewStatus();
		this.activityDate = model.getActivityDate();
		this.date = model.getActivityDate();
		
		if (!model.getStudentId().equals(model.getUserId())) {
			this.evaluatorId = model.getUserId();
		}
	}
	
	@Override
	public int compareTo(StatusEntry o) {
		return this.activityDate.compareTo(o.getActivityDate());
	}
}
