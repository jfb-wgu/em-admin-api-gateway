package edu.wgu.dmadmin.domain.submission;

import java.util.Date;
import java.util.UUID;

import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DashboardSubmission implements Comparable<DashboardSubmission>, QualifyingSubmission {
		
    String status;
    String statusGroup;
    String searchStatus;
    Date dateCreated;
    Date dateSubmitted;
    Date dateUpdated;
    Date dateEstimated;
    Date dateStarted;
    Date dateCompleted;

    UUID taskId;
    String taskName;
    UUID submissionId;
    String studentId;
    int attempt;
    String assessmentCode;
    String assessmentName;
    String courseCode;
    String courseName;
    int aspects;
    int studentFiles;
    String evaluatorId;
    String evaluatorFirstName;
    String evaluatorLastName;

    public DashboardSubmission(SubmissionModel submission) {    	
        this.setStatus(submission.getStatus());
        this.setStatusGroup(submission.getStatusGroup());
        this.setTaskId(submission.getTaskId());
        this.setDateCreated(submission.getDateCreated());
        this.setDateSubmitted(submission.getDateSubmitted());
        this.setDateUpdated(submission.getDateUpdated());
        this.setDateStarted(submission.getDateStarted());
        this.setDateCompleted(submission.getDateCompleted());
        this.setDateEstimated(submission.getDateEstimated());
        this.setSubmissionId(submission.getSubmissionId());
        this.setStudentId(submission.getStudentId());
        this.setTaskName(submission.getTaskName());
        this.setAttempt(submission.getAttempt());
        this.setCourseCode(submission.getCourseCode());
        this.setCourseName(submission.getCourseName());
        this.setAssessmentCode(submission.getAssessmentCode());
        this.setAssessmentName(submission.getAssessmentName());
        this.setStudentFiles(submission.getAttachmentsNS().size());
        this.setAspects(submission.getAspectCount());
        this.setEvaluatorId(submission.getEvaluatorId());
        this.setEvaluatorFirstName(submission.getEvaluatorFirstName());
        this.setEvaluatorLastName(submission.getEvaluatorLastName());
        this.setSearchStatus(StatusUtil.getSearchStatus(submission.getStatus()));
    }
    
    @Override
    public int compareTo(DashboardSubmission o) {
    	if (StatusUtil.isHeld(this.status) && !StatusUtil.isHeld(o.status)) {
    		return -1;
    	} else if (StatusUtil.isHeld(o.status) && !StatusUtil.isHeld(this.status)) {
    		return 1;
    	} else {
    		return this.dateSubmitted.compareTo(o.dateSubmitted);
    	}
    }
}
