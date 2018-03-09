package edu.wgu.dmadmin.domain.submission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import edu.wgu.dmadmin.domain.evaluation.Evaluation;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.model.submission.SubmissionAttachmentModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class Submission implements Comparable<Submission> {

	String status;
	String statusGroup;
	String studentStatus;
	Date dateCreated;
	Date dateSubmitted;
	Date dateUpdated;
	Date dateEstimated;
	Date dateStarted;
	Date dateCompleted;
	UUID taskId;
	Long pidm;
	String taskName;
	UUID submissionId;
	List<SubmissionAttachment> attachments;
	String comments;
	List<Comment> internalComments;
	String evaluatorId;
	String evaluatorFirstName;
	String evaluatorLastName;
	UUID evaluationId;
	String studentId;
	int attempt;
	int aspectCount;
	UUID assessmentId;
	String assessmentCode;
	String assessmentName;
	String courseCode;
	String courseName;
	List<Referral> referrals;
	UUID previousSubmissionId;
	UUID previousEvaluationId;
	UUID reviewEvaluationId;
	List<SubmissionHistoryEntry> submissionHistory;

	@JsonGetter("studentStatus")
	public String getStudentStatus() {
		return StatusUtil.getStudentStatus(this.status, this.attempt);
	}

	@JsonGetter("referrals")
	public List<Referral> getReferrals() {
		this.referrals = ListUtils.defaultIfNull(this.referrals, new ArrayList<>());
		return this.referrals;
	}

	@JsonGetter("internalComments")
	public List<Comment> getInternalComments() {
		this.internalComments = ListUtils.defaultIfNull(this.internalComments, new ArrayList<>());
		return this.internalComments;
	}

	public void setEvalInfo(String inStatus, User user, Evaluation eval) {
		this.setEvalInfo(inStatus, user.getUserId(), user.getFirstName(), user.getLastName(), eval.getEvaluationId(),
				eval.getDateStarted());
	}

	public void setEvalInfo(String inStatus, String userId, String firstName, String lastName, UUID evalId,
			Date evalDate) {
		this.setStatus(inStatus);
		this.setEvaluatorId(userId);
		this.setEvaluatorFirstName(firstName);
		this.setEvaluatorLastName(lastName);
		this.setEvaluationId(evalId);
		this.setDateStarted(evalDate);
	}

	public Submission(SubmissionModel model, List<SubmissionAttachmentModel> attachments) {
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
		this.setPreviousSubmissionId(model.getPreviousSubmissionId());
		this.setPreviousEvaluationId(model.getPreviousEvaluationId());
		this.setReviewEvaluationId(model.getReviewEvaluationId());
		this.setAttachments(attachments.stream().filter(a -> !a.isSoftDelete()).map(a -> new SubmissionAttachment(a))
				.collect(Collectors.toList()));

		this.setInternalComments(MapUtils.emptyIfNull(model.getInternalComments()).values().stream()
				.map(c -> new Comment(c, this.submissionId)).collect(Collectors.toList()));

		this.setReferrals(CollectionUtils.emptyIfNull(model.getReferrals()).stream()
				.map(r -> new Referral(r, this.submissionId)).collect(Collectors.toList()));
	}

	@Override
	public int compareTo(Submission o) {
		return o.getAttempt() - this.getAttempt();
	}
}
