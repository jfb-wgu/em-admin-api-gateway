package edu.wgu.dmadmin.domain.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.ScoredAspect;
import edu.wgu.dmadmin.domain.submission.Referral;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import lombok.Data;

@Data
public class EvaluatorConfirmation {
	private int attempt;
	private UUID submissionId;
	private List<ScoredAspect> aspects;
	private String evaluatorId;
	private String submissionStatus;
	private String evaluationStatus;
	
	@JsonProperty("isPassingGrade")
	private boolean passingGrade;
	
	private Comment comments;
	private List<Referral> referralsSubmitted;
	private Date dateSubmitted;
	
	public List<ScoredAspect> getAspects() {
		if (aspects == null) aspects = new ArrayList<ScoredAspect>();
		
		Collections.sort(aspects);
		return aspects;
	}
	
	public EvaluatorConfirmation(SubmissionModel submission, RubricModel rubric, EvaluationModel evaluation) {
		this.setAttempt(submission.getAttempt());
		this.setDateSubmitted(submission.getDateSubmitted());
		this.setSubmissionId(submission.getSubmissionId());
		this.setEvaluatorId(submission.getEvaluatorId());
		this.setReferralsSubmitted(submission.getReferralsNS().stream().map(r -> new Referral(r)).collect(Collectors.toList()));
		this.setSubmissionStatus(submission.getStatus());
		this.setEvaluationStatus(evaluation.getStatus());
		
		ScoreReportModel scoreReport = evaluation.getScoreReport();
		this.setPassingGrade(scoreReport.isPassed());
		
		Optional<CommentModel> scoreReportComment = scoreReport.getComments().values().stream()
				.filter(c -> c.getAttempt() == submission.getAttempt() && c.getUserId().equals(evaluation.getEvaluatorId()))
				.findFirst();
		if (scoreReportComment.isPresent()) {
			this.setComments(new Comment(scoreReportComment.get()));
		}

		rubric.getAspects().forEach(aspect -> {
			ScoreModel score = scoreReport.getScores().get(aspect.getName());
			this.getAspects().add(new ScoredAspect(score, aspect, evaluation.getEvaluatorId(), submission.getAttempt()));
		});
	}
}
