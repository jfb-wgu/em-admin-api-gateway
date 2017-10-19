package edu.wgu.dmadmin.model.assessment;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.google.common.base.CharMatcher;

import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class EvaluationModel implements Comparable<EvaluationModel> {
	@Column(name="evaluation_id")
	UUID evaluationId;
	
	@Column(name="evaluator_id")
	String evaluatorId;
	
	@Column(name="submission_id")
	UUID submissionId;
	
	String status;
	
	@Column(name="minutes_spent")
	int minutesSpent;
	
	@Column(name="date_started")
	Date dateStarted;
	
	@Column(name="date_completed")
	Date dateCompleted;
	
	@Column(name="date_updated")
	Date dateUpdated;
		
	@Column(name="attachments")
	List<Attachment> attachments;
	
	@Column(name="score_report")
	@Frozen
	ScoreReportModel scoreReport;

	@Column(name="task_id")
	UUID taskId;
	
	@Column(name="student_id")
	String studentId;
	
	int attempt;
	
    @Column(name="evaluator_first_name")
    String evaluatorFirstName;
    
    @Column(name="evaluator_last_name")
    String evaluatorLastName;
	
	public ScoreReportModel getScoreReport() {
		if (scoreReport == null) scoreReport = new ScoreReportModel();
		return scoreReport;
	}
	
	/**
	 * To preserve previously passing scores, save the old assigned score to
	 * the previousScore field so the UI will know to lock it.  Previously
	 * failing scores are reset to -1 so they will be graded again.  All comments
	 * are preserved.
	 * 
	 * @param model
	 */
	public void importScoreReport(ScoreReportModel model) {
		model.getScores().forEach((scoreName, score) -> {
			if (score.getAssignedScore() >= score.getPassingScore()) {
				score.setPreviousScore(score.getAssignedScore());
			} else {
				score.setPreviousScore(-1);
				score.setAssignedScore(-1);
			}
		});
		
		this.scoreReport = model;
	}

	/**
	 * When an evaluation is taken over by a new evaluator, copy the existing score report into the new one.
	 * If there were any comments from the previous evaluator, copy them into new objects and mark the old
	 * ones INTERNAL to hide them from the student.
	 * 
	 * @param current The source EvaluationModel that will be imported
	 * @param sub The SubmissionModel being evaluated
	 * @param user The UserModel that will be taking over the evaluation
	 */
	public void assignScoreReport(EvaluationModel current, SubmissionModel sub, UserModel user) {
		current.getScoreReport().getScores().forEach((name, score) -> {
			List<CommentModel> scoreComments = score.getComments().values().stream().filter(c -> c.getAttempt() == sub.getAttempt() && c.getUserId().equals(current.getEvaluatorId())).collect(Collectors.toList());

			for (CommentModel comment : scoreComments) {
				CommentModel copiedComment = new CommentModel(comment, user.getUserId(), user.getFirstName(), user.getLastName());
				score.getComments().put(copiedComment.getCommentId(), copiedComment);
				comment.setType(CommentTypes.INTERNAL);
			}		
		});

		List<CommentModel> reportComments = current.getScoreReport().getComments().values().stream().filter(c -> c.getAttempt() == sub.getAttempt() && c.getUserId().equals(current.getEvaluatorId())).collect(Collectors.toList());

		for (CommentModel comment : reportComments) {
			CommentModel copiedComment = new CommentModel(comment, user.getUserId(), user.getFirstName(), user.getLastName());
			current.getScoreReport().getComments().put(copiedComment.getCommentId(), copiedComment);
			comment.setType(CommentTypes.INTERNAL);
		}	

		this.scoreReport = current.getScoreReport();
	}
	
	/**
	 * 	Make sure all aspect comments have the scores set.  Depending on the order of commenting and scoring
	 *  they might not have had it embedded already.
	 */
	public void setCommentScores() {
		this.getScoreReport().getScores().values().forEach(score -> {
			Optional<CommentModel> existing = score.getComments().values().stream()
					.filter(c -> c.getAttempt() == this.getAttempt() && c.getUserId().equals(this.getEvaluatorId()))
					.findFirst();
			if (existing.isPresent()) existing.get().setScore(score.getAssignedScore());
		});
	}
	
	public void complete(String newStatus) {
		this.setStatus(newStatus);
		this.setDateCompleted(DateUtil.getZonedNow());
		this.setMinutesSpent(Math.toIntExact(TimeUnit.MILLISECONDS.toMinutes(this.getDateCompleted().getTime() - this.getDateStarted().getTime())));
		this.setCommentScores();
	}
	
	public EvaluationModel(UserModel evaluator, SubmissionModel submission) {
		this.setEvaluationId(UUID.randomUUID());
		this.setEvaluatorId(evaluator.getUserId());
		this.setSubmissionId(submission.getSubmissionId());
		this.setTaskId(submission.getTaskId());
		this.setStudentId(submission.getStudentId());
		this.setAttempt(submission.getAttempt());
		this.setDateStarted(DateUtil.getZonedNow());
		this.setEvaluatorFirstName(evaluator.getFirstName());
		this.setEvaluatorLastName(evaluator.getLastName());
	}
		
	public void populate(EvaluationModel evaluation) {
		this.evaluationId = evaluation.getEvaluationId();
		this.evaluatorId = evaluation.getEvaluatorId();
		this.submissionId = evaluation.getSubmissionId();
		this.status = evaluation.getStatus();
		this.minutesSpent = evaluation.getMinutesSpent();
		this.dateStarted = evaluation.getDateStarted();
		this.dateCompleted = evaluation.getDateCompleted();
		this.dateUpdated = evaluation.getDateUpdated();
		this.attachments = evaluation.getAttachments();
		this.taskId = evaluation.getTaskId();
		this.studentId = evaluation.getStudentId();
		this.attempt = evaluation.getAttempt();
		this.scoreReport = evaluation.getScoreReport();
		this.evaluatorFirstName = evaluation.getEvaluatorFirstName();
		this.evaluatorLastName = evaluation.getEvaluatorLastName();
	}

	@Override
	public int compareTo(EvaluationModel o) {
		return this.getDateUpdated().compareTo(o.getDateUpdated());
	}
	
	@Override
	public boolean equals(Object e) {
		if (e == null) return false;
		return e instanceof EvaluationModel && this.getEvaluationId().equals(((EvaluationModel)e).getEvaluationId());
	}
	
	public int hashCode() {
		return Integer.parseInt(CharMatcher.DIGIT.retainFrom(this.evaluationId.toString()));
	}
}
