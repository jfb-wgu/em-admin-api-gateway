package edu.wgu.dmadmin.model.submission;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.common.CommentTypes;
import edu.wgu.dmadmin.domain.submission.Comment;
import edu.wgu.dmadmin.util.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name = "comment")
public class CommentModel {

	@Field(name = "comment_id")
	UUID commentId;

	@Field(name = "user_id")
	String userId;

	@Field(name = "first_name")
	String firstName;

	@Field(name = "last_name")
	String lastName;

	@Field(name = "date_created")
	Date dateCreated;

	@Field(name = "date_updataed")
	Date dateUpdated;

	String comments;
	int attempt;
	int score;
	String type = CommentTypes.INTERNAL;

	public CommentModel(Comment comment) {
		this.commentId = comment.getCommentId();
		this.userId = comment.getUserId();
		this.dateCreated = comment.getDateCreated();
		this.comments = comment.getComments();
		this.firstName = comment.getFirstName();
		this.lastName = comment.getLastName();
		this.attempt = comment.getAttempt();
	}

	public CommentModel(String userId, String firstName, String lastName, String comments, int attempt, int score,
			String type) {
		this.setCommentId(UUID.randomUUID());
		this.setAttempt(attempt);
		this.setScore(score);
		this.setComments(comments);
		this.setDateCreated(DateUtil.getZonedNow());
		this.setDateUpdated(this.getDateCreated());
		this.setType(type);
		this.setUserId(userId);
		this.setFirstName(firstName);
		this.setLastName(lastName);
	}
}
