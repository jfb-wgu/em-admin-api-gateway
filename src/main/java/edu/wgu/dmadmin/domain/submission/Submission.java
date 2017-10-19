package edu.wgu.dmadmin.domain.submission;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Submission implements Comparable<Submission> {
	
	@JsonInclude(value=Include.NON_EMPTY)
    String status;

    @JsonInclude(value=Include.NON_EMPTY)
    String statusGroup;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String studentStatus;

    @JsonInclude(value=Include.NON_EMPTY)
    Date dateCreated;
    
    @JsonInclude(value=Include.NON_EMPTY)
    Date dateSubmitted;
    
    @JsonInclude(value=Include.NON_EMPTY)
    Date dateUpdated;
    
    @JsonInclude(value=Include.NON_EMPTY)
    Date dateEstimated;
    
    @JsonInclude(value=Include.NON_EMPTY)
    Date dateStarted;
    
    @JsonInclude(value=Include.NON_EMPTY)
    Date dateCompleted;

    @JsonInclude(value=Include.NON_EMPTY)
    UUID taskId;

    @JsonInclude(value=Include.NON_EMPTY)
    Long pidm;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String taskName;
    
    @JsonInclude(value=Include.NON_EMPTY)
    UUID submissionId;
    
    @JsonInclude(value=Include.NON_EMPTY)
    List<Attachment> attachments;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String comments;
    
    @JsonInclude(value=Include.NON_EMPTY)
    List<Comment> internalComments;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String evaluatorId;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String evaluatorFirstName;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String evaluatorLastName;
    
    @JsonInclude(value=Include.NON_EMPTY)
    UUID evaluationId;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String studentId;
    
    @JsonInclude(value=Include.NON_EMPTY)
    int attempt;
    
    @JsonInclude(value=Include.NON_EMPTY)
    int aspectCount;
    
    @JsonInclude(value=Include.NON_EMPTY)
    UUID assessmentId;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String assessmentCode;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String assessmentName;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String courseCode;
    
    @JsonInclude(value=Include.NON_EMPTY)
    String courseName;
    
    @JsonInclude(value=Include.NON_EMPTY)
    List<Referral> referrals;
    
    @JsonInclude(value=Include.NON_EMPTY)
    UUID previousSubmissionId;
    
    @JsonInclude(value=Include.NON_EMPTY)
    UUID previousEvaluationId;
    
    @JsonInclude(value=Include.NON_EMPTY)
    UUID reviewEvaluationId;
    
    @JsonInclude(value=Include.NON_EMPTY)
    List<SubmissionHistoryEntry> submissionHistory;
    
    @JsonGetter("studentStatus")
    public String getStudentStatus() {
    	return StatusUtil.getStudentStatus(status, attempt);
    }

    public Submission(SubmissionModel model) {
    	this.setSubmissionId(model.getSubmissionId());
    	this.setStudentId(model.getStudentId());
    	this.setAttempt(model.getAttempt());
    	this.setTaskId(model.getTaskId());
    	this.setTaskName(model.getTaskName());
    	this.setPidm(model.getPidm());
    	this.setAspectCount(model.getAspectCount());
        this.setCourseCode(model.getCourseCode());
        this.setCourseName(model.getCourseName());
        this.setAssessmentId(model.getAssessmentId());
        this.setAssessmentCode(model.getAssessmentCode());
        this.setAssessmentName(model.getAssessmentName());
        this.setComments(model.getComments());
        this.setInternalComments(model.getInternalCommentsNS().values().stream().map(c -> new Comment(c)).collect(Collectors.toList()));
        this.setAttachments(model.getAttachmentsNS().values().stream().map(a -> new Attachment(a, studentId, submissionId)).collect(Collectors.toList()));
        this.setStatus(model.getStatus());
        this.setStatusGroup(model.getStatusGroup());
        this.setDateCreated(model.getDateCreated());
        this.setDateSubmitted(model.getDateSubmitted());
        this.setDateEstimated(model.getDateEstimated());
        this.setDateStarted(model.getDateStarted());
        this.setDateUpdated(model.getDateUpdated());
        this.setDateCompleted(model.getDateCompleted());
        this.setEvaluatorId(model.getEvaluatorId());
        this.setEvaluatorFirstName(model.getEvaluatorFirstName());
        this.setEvaluatorLastName(model.getEvaluatorLastName());
        this.setEvaluationId(model.getEvaluationId());
        this.setReferrals(model.getReferralsNS().stream().map(r -> new Referral(r)).collect(Collectors.toList()));
        this.setPreviousSubmissionId(model.getPreviousSubmissionId());
        this.setPreviousEvaluationId(model.getPreviousEvaluationId());
        this.setReviewEvaluationId(model.getReviewEvaluationId());
    }
    
    @Override
    public int compareTo(Submission o) {
        return this.dateSubmitted.compareTo(o.dateSubmitted);
    }
}
