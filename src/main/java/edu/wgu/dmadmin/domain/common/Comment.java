package edu.wgu.dmadmin.domain.common;

import java.util.Comparator;
import java.util.Date;

import edu.wgu.dmadmin.domain.evaluation.Evaluation;
import edu.wgu.dmadmin.model.submission.CommentModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Comment implements Comparable<Comment> {
	String userId;
	Date dateCreated;
	Date dateUpdated;
	String comments;
	int attempt;
	int score;
	String firstName;
	String lastName;
	String type;
	String scoreReportName;
	String aspectName;
	
	public Comment(Evaluation eval, String aspectName) {
		this.setAspectName(aspectName);
		this.setAttempt(eval.getAttempt());
		this.setComments(eval.getAspects().get(aspectName).getComments());
		this.setDateCreated(eval.getDateStarted());
		this.setDateUpdated(eval.getDateUpdated());
		this.setFirstName(eval.getEvaluatorFirstName());
		this.setLastName(eval.getEvaluatorLastName());
		this.setScore(eval.getAspects().get(aspectName).getAssignedScore());
		this.setScoreReportName("score report");
		this.setType(CommentTypes.STUDENT);
		this.setUserId(eval.getEvaluatorId());
	}
	
	public Comment(CommentModel model, String inScoreReportName, String inAspectName) {
		this.userId = model.getUserId();
		this.dateCreated = model.getDateCreated();
		this.dateUpdated = model.getDateUpdated();
		this.comments = model.getComments();
		this.attempt = model.getAttempt();
		this.score = model.getScore();
		this.firstName = model.getFirstName();
		this.lastName = model.getLastName();
		this.type = model.getType();
		this.scoreReportName = inScoreReportName;
		this.aspectName = inAspectName;
	}

	@Override
	public int compareTo(Comment o) {
		return Comparator.comparing(Comment::getAttempt).thenComparing(Comment::getDateUpdated)
				.reversed().compare(this, o);
	}

	public void setAspectName(String aspectName) {
		this.aspectName = aspectName;
	}
}
