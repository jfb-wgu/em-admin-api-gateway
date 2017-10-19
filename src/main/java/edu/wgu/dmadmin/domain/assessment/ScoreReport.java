package edu.wgu.dmadmin.domain.assessment;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.wgu.dmadmin.domain.publish.Anchor;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreReport {
	String name;
	String description;
	
	@JsonProperty("aspects")
	List<Score> scores;
	
	List<Comment> comments;
	boolean passed;
	    
    @JsonGetter("passed")
    public boolean isPassed() {
    	Set<Score> failed = this.scores.stream().filter(s -> s.getAssignedScore() < s.getPassingScore()).collect(Collectors.toSet());
    	return failed.size() == 0 ? true : false;
    }
 
    @JsonGetter("comments")
    public List<Comment> getComments() {
    	if (comments == null) return Collections.emptyList();
    	
    	Collections.sort(comments);
    	return comments;
    }
    
    @JsonGetter("aspects")
    public List<Score> getScores() {
    	if (scores == null) return Collections.emptyList();
    	
    	Collections.sort(scores);
    	return scores;
    }
    
	public ScoreReport(RubricModel model) {
		if (model == null) return;
		
		this.name = model.getName();
		this.description = model.getDescription();
		this.scores = model.getAspects().stream().map(aspect -> new Score(aspect)).collect(Collectors.toList());	
	}
	
	public ScoreReport(ScoreReportModel model) {
		this.name = model.getName();
		this.description = model.getDescription();
		this.passed = model.isPassed();
		this.scores = model.getScores().values().stream().map(aspect -> new Score(aspect)).collect(Collectors.toList());
		this.comments = model.getComments().values().stream().map(comment -> new Comment(comment)).collect(Collectors.toList());
	}

	/**
	 * Copy the evaluation information into the domain object for return to the student.
	 * 
	 * Only the current scores and comments should be displayed for the student.
	 * 
	 * @param model
	 * @param submission
	 */
	public void prepareScoreReport(ScoreReportModel model, SubmissionModel submission) {
		this.setComments(model.getComments().values().stream()
				.filter(c -> c.getAttempt() == submission.getAttempt() && c.getUserId().equals(submission.getEvaluatorId()) && CommentTypes.STUDENT.equals(c.getType()))
				.map(comment -> new Comment(comment))
				.collect(Collectors.toList()));
		
		scores.forEach(score -> {
			ScoreModel scoredAspect = model.getScores().get(score.getName());
			
			score.setAssignedScore(scoredAspect.getAssignedScore());
			score.setComments(scoredAspect.getComments().values().stream()
					.filter(c -> c.getAttempt() == submission.getAttempt() && c.getUserId().equals(submission.getEvaluatorId()) && CommentTypes.STUDENT.equals(c.getType()))
					.map(c -> new Comment(c)).collect(Collectors.toList()));
		});
	}
	
	public void setRubric(RubricModel model) {
		model.getAspects().forEach(aspect -> {
			Score score = scores.stream()
				.filter(s -> s.getName().equals(aspect.getName()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Aspect [" + aspect.getName() + "] was not found."));
			
			score.setOrder(aspect.getOrder());
			score.setDescription(aspect.getDescription());
			score.setLrURL(aspect.getLrURL());
			score.setAnchors(aspect.getAnchors().stream().map(anchor -> new Anchor(anchor)).collect(Collectors.toList()));	
		});
	}
}
