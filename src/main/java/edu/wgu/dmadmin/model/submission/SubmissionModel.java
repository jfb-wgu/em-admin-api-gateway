package edu.wgu.dmadmin.model.submission;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.FrozenValue;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubmissionModel implements Comparable<SubmissionModel> {

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
	
	@Override
	public int compareTo(SubmissionModel o) {
		return o.getAttempt() - this.getAttempt();
	}
}
