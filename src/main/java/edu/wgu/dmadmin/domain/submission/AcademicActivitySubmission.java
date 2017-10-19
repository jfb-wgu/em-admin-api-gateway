package edu.wgu.dmadmin.domain.submission;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class AcademicActivitySubmission {
    String status;
    Date dateCreated;
    Date dateSubmitted;
    Date dateUpdated;
    Date dateEstimated;
    Date dateStarted;
    Date dateCompleted;
    String taskId;
    String taskName;
    String submissionId;
    String assessmentCode;
    String studentId;
    Long pidm;
    String assessmentId;
    String evaluationId;
    String evaluatorId;
    String comments;

    public AcademicActivitySubmission(Submission submission) {
        this.status = submission.getStatus();
        this.dateCreated = submission.getDateCreated();
        this.dateSubmitted = submission.getDateSubmitted();
        this.dateUpdated = submission.getDateUpdated();
        this.dateEstimated = submission.getDateEstimated();
        this.dateStarted = submission.getDateStarted();
        this.dateCompleted = submission.getDateCompleted();
        this.taskId = submission.getTaskId() == null ? null : submission.getTaskId().toString();
        this.taskName = submission.getTaskName();
        this.submissionId = submission.getSubmissionId() == null ? null : submission.getSubmissionId().toString();
        this.assessmentCode = submission.getAssessmentCode();
        this.studentId = submission.getStudentId();
        this.pidm = submission.getPidm();
        this.assessmentId = submission.getAssessmentId() == null ? null : submission.getAssessmentId().toString();
        this.evaluationId = submission.getEvaluationId() == null ? null : submission.getEvaluationId().toString();
        this.evaluatorId = submission.getEvaluatorId();
        this.comments = submission.getComments();


    }
}
