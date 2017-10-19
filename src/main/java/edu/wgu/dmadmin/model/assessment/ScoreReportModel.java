package edu.wgu.dmadmin.model.assessment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.FrozenValue;
import com.datastax.driver.mapping.annotations.Transient;
import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.ScoreReport;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.util.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name = "score_report")
public class ScoreReportModel {
	String name;
	String description;

	@Frozen
	Map<String, ScoreModel> scores;

	@FrozenValue
	Map<UUID, CommentModel> comments;

	boolean passed;

	public boolean isPassed() {
		Set<ScoreModel> failed = scores.values().stream().filter(s -> s.getAssignedScore() < s.getPassingScore())
				.collect(Collectors.toSet());
		return failed.size() == 0 ? true : false;
	}

	public Map<UUID, CommentModel> getComments() {
		if (comments == null)
			comments = new HashMap<UUID, CommentModel>();
		return comments;
	}

	public Map<String, ScoreModel> getScores() {
		if (scores == null)
			scores = new HashMap<String, ScoreModel>();
		return scores;
	}

	@Transient
	public Set<String> getUnscoredAspects() {
		return scores.values().stream().filter(s -> s.getAssignedScore() < 0).map(s -> s.getName())
				.collect(Collectors.toSet());
	}

	/**
	 * Update or add a comment to the evaluation aspect
	 * 
	 * @param user
	 * @param comment
	 * @param aspectName
	 * @param attempt
	 * @return
	 */
	public CommentModel setAspectComment(Comment comment, String aspectName, int attempt, String userId, String firstName, String lastName) {
		
		ScoreModel aspect = this.getScores().get(aspectName);
		
		if (comment.getCommentId() != null && StringUtils.isBlank(comment.getComments())) {
			aspect.getComments().remove(comment.getCommentId());
			return null;
		} else if (comment.getCommentId() != null) {
			CommentModel existing = aspect.getComments().get(comment.getCommentId());
			existing.setComments(comment.getComments());
			existing.setScore(aspect.getAssignedScore());
			existing.setDateUpdated(DateUtil.getZonedNow());
			existing.setType(comment.getType());
			return existing;
		} else {
			CommentModel newComment = new CommentModel(userId, firstName, lastName, comment.getComments(), attempt, aspect.getAssignedScore(), comment.getType());
			aspect.getComments().put(newComment.getCommentId(), newComment);
			return newComment;
		}
	}

	/**
	 * Update or add a comment to the score report.
	 * 
	 * @param user
	 * @param comment
	 * @param attempt
	 * @return
	 */
	public CommentModel setReportComment(String userId, String firstName, String lastName, Comment comment,
			int attempt) {
		if (comment.getCommentId() != null && StringUtils.isBlank(comment.getComments())) {
			getComments().remove(comment.getCommentId());
			return null;
		} else if (comment.getCommentId() != null) {
			CommentModel existing = this.getComments().get(comment.getCommentId());
			existing.setComments(comment.getComments());
			existing.setType(comment.getType());
			existing.setDateUpdated(DateUtil.getZonedNow());
			return existing;
		} else {
			CommentModel newComment = new CommentModel(userId, firstName, lastName, comment.getComments(), attempt, -1,
					comment.getType());
			this.getComments().put(newComment.getCommentId(), newComment);
			return newComment;
		}
	}

	public ScoreReportModel(ScoreReport report) {
		this.name = report.getName();
		this.description = report.getDescription();
		this.passed = report.isPassed();
		this.scores = report.getScores().stream().collect(Collectors.toMap(s -> s.getName(), s -> new ScoreModel(s)));
		this.comments = report.getComments().stream()
				.collect(Collectors.toMap(c -> c.getCommentId(), c -> new CommentModel(c)));
	}

	public ScoreReportModel(RubricModel model) {
		this.name = model.getName();
		this.description = model.getDescription();
		this.scores = model.getAspects().stream().collect(Collectors.toMap(a -> a.getName(), a -> new ScoreModel(a)));
	}
}
