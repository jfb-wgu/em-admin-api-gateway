package edu.wgu.dmadmin.repo.oracle;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;

import com.google.common.base.CharMatcher;

import edu.wgu.dreammachine.model.audit.StatusLogByAssessmentModel;
import lombok.Data;

@Data
public class StatusEntry implements Comparable<StatusEntry> {

	String studentId;
	UUID assessmentId;
	UUID taskId;
	String status;
	Date activityDate;
	String evaluatorId;
	UUID submissionId;
	long date;
	
	public StatusEntry(DRF drf, DRFTask task) {
		this.assessmentId = UUID.fromString(drf.getTitle());
		this.taskId = UUID.fromString(task.getTaskId());
		this.studentId = drf.getWguainf().getSpriden().getBannerId();
		this.status = task.getStatus();
		this.activityDate = DateUtils.truncate(DateUtils.addHours(new Date(task.getActivityDate().getTime()), -8), Calendar.SECOND);
		this.evaluatorId = task.getEvaluatorId();
		this.date = task.getActivityDate().getTime();
	}
	
	public StatusEntry(StatusLogByAssessmentModel model) {
		this.submissionId = model.getSubmissionId();
		this.assessmentId = model.getAssessmentId();
		this.taskId = model.getTaskId();
		this.studentId = model.getStudentId();
		this.status = model.getNewStatus();
		this.activityDate = DateUtils.truncate(model.getActivityDate(), Calendar.SECOND);
		this.date = model.getActivityDate().getTime();
		
		if (!model.getStudentId().equals(model.getUserId())) {
			this.evaluatorId = model.getUserId();
		}
	}
	
	@Override
	public int compareTo(StatusEntry o) {
		if (!this.studentId.equals(o.getStudentId())) return this.studentId.compareTo(o.getStudentId());
		if (!this.taskId.equals(o.getTaskId())) return this.taskId.compareTo(o.getTaskId());
		return this.activityDate.compareTo(o.getActivityDate());
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StatusEntry)) return false;
		
		StatusEntry compare = (StatusEntry)o;
		
		boolean assessmentEqual = this.getAssessmentId().equals(compare.getAssessmentId());
		boolean statusEqual = this.getStatus().equals(compare.getStatus());
		boolean studentIdEqual = this.getStudentId().equals(compare.getStudentId());
		boolean taskEqual = this.getTaskId().equals(compare.getTaskId());
		
		boolean dateEqual = this.getActivityDate().after(DateUtils.addSeconds(compare.getActivityDate(), -10)) &&
				this.getActivityDate().before(DateUtils.addSeconds(compare.getActivityDate(), 10));

		return assessmentEqual && statusEqual && studentIdEqual && taskEqual && dateEqual;
	}
	
	@Override 
	public int hashCode() {
		return Integer.parseInt(CharMatcher.DIGIT.retainFrom(this.taskId.toString())) +
				Integer.parseInt(CharMatcher.DIGIT.retainFrom(this.assessmentId.toString())) + 
				Integer.parseInt(CharMatcher.DIGIT.retainFrom(this.studentId.toString()));
	}
}
