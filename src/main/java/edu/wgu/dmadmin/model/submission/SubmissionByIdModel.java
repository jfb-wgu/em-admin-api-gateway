package edu.wgu.dmadmin.model.submission;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.submission.Submission;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_by_id", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class SubmissionByIdModel extends SubmissionModel {

	@PartitionKey(0)
	public UUID getSubmissionId() {
		return submissionId;
	}

	@PartitionKey(1)
	public String getStudentId() {
		return studentId;
	}

	@PartitionKey(2)
	public UUID getTaskId() {
		return taskId;
	}

	@PartitionKey(3)
	public int getAttempt() {
		return attempt;
	}

	public SubmissionByIdModel(Submission submission) {
		this.submissionId = submission.getSubmissionId();
		this.studentId = submission.getStudentId();
		this.taskId = submission.getTaskId();
		this.pidm = submission.getPidm();
		this.dateCreated = submission.getDateCreated();
		this.dateSubmitted = submission.getDateSubmitted();
		this.evaluatorId = submission.getEvaluatorId();
		this.evaluatorFirstName = submission.getEvaluatorFirstName();
		this.evaluatorLastName = submission.getEvaluatorLastName();
		this.evaluationId = submission.getEvaluationId();
		this.status = submission.getStatus();
		this.statusGroup = submission.getStatusGroup();
		this.taskName = submission.getTaskName();
		this.aspectCount = submission.getAspectCount();
		this.comments = submission.getComments();
		this.attempt = submission.getAttempt();
		this.assessmentId = submission.getAssessmentId();
		this.assessmentCode = submission.getAssessmentCode();
		this.assessmentName = submission.getAssessmentName();
		this.courseCode = submission.getCourseCode();
		this.courseName = submission.getCourseName();
		this.dateEstimated = submission.getDateEstimated();
		this.dateUpdated = submission.getDateUpdated();
		this.dateStarted = submission.getDateStarted();
		this.dateCompleted = submission.getDateCompleted();
		this.previousSubmissionId = submission.getPreviousSubmissionId();
		this.previousEvaluationId = submission.getPreviousEvaluationId();
		this.reviewEvaluationId = submission.getReviewEvaluationId();

		this.referrals = ListUtils.defaultIfNull(submission.getReferrals(), Collections.emptyList()).stream()
				.map(r -> new ReferralModel(r)).collect(Collectors.toList());

		this.internalComments = ListUtils.defaultIfNull(submission.getInternalComments(), Collections.emptyList())
				.stream().collect(Collectors.toMap(c -> c.getCommentId(), c -> new CommentModel(c)));
	}

	public SubmissionByIdModel(SubmissionModel model) {
		this.populate(model);
	}
}
