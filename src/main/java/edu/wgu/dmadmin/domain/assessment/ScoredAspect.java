package edu.wgu.dmadmin.domain.assessment;

import java.util.Optional;

import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.publish.AnchorModel;
import edu.wgu.dmadmin.model.publish.AspectModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoredAspect implements Comparable<ScoredAspect> {
	String aspectName;
	String aspectDescription;
	String anchorName;
	String anchorDescription;
	int passingScore;
	int assignedScore;
	int previousScore;
	Comment comment;
	int order;
	
	/**
	 * Provides an object summarizing the efforts of the evaluator in scoring and
	 * commenting on a single aspect of the rubric.  Blends the anchor information
	 * from the task definition with the scoring work of the evaluator.
	 * 
	 * @param score
	 * @param aspect
	 * @param evaluatorId
	 * @param attempt
	 */
	public ScoredAspect(ScoreModel score, AspectModel aspect, String evaluatorId, int attempt) {
		this.setPassingScore(score.getPassingScore());
		this.setAssignedScore(score.getAssignedScore());
		this.setPreviousScore(score.getPreviousScore());
		
		this.setAspectName(aspect.getName());
		this.setAspectDescription(aspect.getDescription());
		this.setOrder(aspect.getOrder());
		
		Optional<AnchorModel> anchor = aspect.getAnchors().stream()
				.filter(a -> a.getScore() == score.getAssignedScore())
				.findFirst();
		if (anchor.isPresent()) {
			this.setAnchorName(anchor.get().getName());
			this.setAnchorDescription(anchor.get().getDescription());
		}
		
		Optional<CommentModel> scoreComment = score.getComments().values().stream()
				.filter(c -> c.getUserId().equals(evaluatorId) && c.getAttempt() == attempt)
				.findFirst();
		if (scoreComment.isPresent()) {
			this.setComment(new Comment(scoreComment.get()));
		}
	}

	@Override
	public int compareTo(ScoredAspect o) {
		return this.order - o.getOrder();
	}
}
