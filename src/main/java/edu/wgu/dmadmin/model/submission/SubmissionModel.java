package edu.wgu.dmadmin.model.submission;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.FrozenValue;
import com.datastax.driver.mapping.annotations.Transient;
import edu.wgu.dmadmin.domain.submission.QualifyingSubmission;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.util.StatusUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SubmissionModel implements Comparable<SubmissionModel>, QualifyingSubmission {

    @Column(name = "submission_id")
    UUID submissionId;
    
    @Column(name = "student_id")
    String studentId;
    
    int attempt;

    @Column(name = "task_id")
    UUID taskId;

    @Column(name = "pidm")
    Long pidm;

    @Column(name = "task_name")
    String taskName;
    
    @Column(name="aspect_count")
    int aspectCount;
    
    @Column(name = "course_code")
    String courseCode;
    
    @Column(name="course_name")
    String courseName;

    @Column(name = "assessment_id")
    UUID assessmentId;
    
    @Column(name = "assessment_code")
    String assessmentCode;
    
    @Column(name="assessment_name")
    String assessmentName;
    
    String comments;
    
    @Column(name="internal_comments")
    @FrozenValue
    Map<UUID, CommentModel> internalComments;
    
    @FrozenValue
    Map<String, AttachmentModel> attachments;
    
    String status;
    
    @Column(name="status_group")
    String statusGroup;
    
    @Column(name = "date_created")
    Date dateCreated;
    
    @Column(name = "date_submitted")
    Date dateSubmitted;
    
    @Column(name = "date_updated")
    Date dateUpdated;
    
    @Column(name="date_estimated")
    Date dateEstimated;
    
    @Column(name="date_started")
    Date dateStarted;
    
    @Column(name="date_completed")
    Date dateCompleted;
    
    @Column(name = "evaluator_id")
    String evaluatorId;
    
    @Column(name="evaluator_first_name")
    String evaluatorFirstName;
    
    @Column(name="evaluator_last_name")
    String evaluatorLastName;
    
    @Column(name="evaluation_id")
    UUID evaluationId;

    @Frozen
    List<ReferralModel> referrals;
    
    @Column(name = "previous_submission_id")
    UUID previousSubmissionId;
    
    @Column(name = "previous_evaluation_id")
    UUID previousEvaluationId;
    
    @Column(name = "review_evaluation_id")
    UUID reviewEvaluationId;
    
    /**
     * Some Null Safe methods for dealing with the data collections.
     */
    
    @Transient
    public Map<String, AttachmentModel> getAttachmentsNS() {
    	if (attachments == null) attachments = new HashMap<String, AttachmentModel>();
    	return attachments;
    }
    
    @Transient
    public List<ReferralModel> getReferralsNS() {
    	if (referrals == null) referrals = new ArrayList<ReferralModel>();
    	return referrals;
    }
    
    @Transient
    public Map<UUID, CommentModel> getInternalCommentsNS() {
    	if (internalComments == null) internalComments = new HashMap<UUID, CommentModel>();
    	return internalComments;
    }
    
    /**
     * Utility method to set evaluation data in the submission.
     * 
     * @param inStatus
     * @param UserModel
     * @param EvaluationModel
     */
    public void setEvaluation(String inStatus, UserModel user, EvaluationModel evaluation) {
		this.setStatus(inStatus);
		this.setEvaluatorId(user.getUserId());
		this.setEvaluationId(evaluation.getEvaluationId());
		this.setDateStarted(evaluation.getDateStarted());
		this.setEvaluatorFirstName(user.getFirstName());
		this.setEvaluatorLastName(user.getLastName());
    }

    /**
     * Utility method to clear evaluation data from the submission.
     */
    public void cancelEvaluation() {
		this.setStatus(StatusUtil.EVALUATION_CANCELLED);
		this.setEvaluatorId(null);
		this.setEvaluationId(null);
		this.setDateStarted(null);
		this.setEvaluatorFirstName(null);
		this.setEvaluatorLastName(null);
    }

    public void populate(SubmissionModel model) {
    	this.setSubmissionId(model.getSubmissionId());
    	this.setStudentId(model.getStudentId());
    	this.setAttempt(model.getAttempt());
        this.setTaskId(model.getTaskId());
        this.setPidm(model.getPidm());
        this.setTaskName(model.getTaskName());
        this.setAspectCount(model.getAspectCount());
        this.setCourseCode(model.getCourseCode());
        this.setCourseName(model.getCourseName());
        this.setAssessmentId(model.getAssessmentId());
        this.setAssessmentCode(model.getAssessmentCode());
        this.setAssessmentName(model.getAssessmentName());
        this.setComments(model.getComments());
        this.setInternalComments(model.getInternalComments());
        this.setAttachments(model.getAttachments());
        this.setStatus(model.getStatus());
        this.setStatusGroup(model.getStatusGroup());
        this.setDateCreated(model.getDateCreated());
        this.setDateSubmitted(model.getDateSubmitted());
        this.setDateStarted(model.getDateStarted());
        this.setDateCompleted(model.getDateCompleted());
        this.setDateUpdated(model.getDateUpdated());
        this.setDateEstimated(model.getDateEstimated());
        this.setEvaluatorId(model.getEvaluatorId());
        this.setEvaluationId(model.getEvaluationId());
        this.setEvaluatorFirstName(model.getEvaluatorFirstName());
        this.setEvaluatorLastName(model.getEvaluatorLastName());
        this.setReferrals(model.getReferrals());
        this.setPreviousSubmissionId(model.getPreviousSubmissionId());
        this.setPreviousEvaluationId(model.getPreviousEvaluationId());
        this.setReviewEvaluationId(model.getReviewEvaluationId());
    }

	@Override
	public int compareTo(SubmissionModel o) {
		return o.getAttempt() - this.getAttempt();
	}
}
