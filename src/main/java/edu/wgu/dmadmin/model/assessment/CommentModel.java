package edu.wgu.dmadmin.model.assessment;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.util.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name="comment")
public class CommentModel {
	
	@Field(name="comment_id")
	UUID commentId;
	
	@Field(name="user_id")
	String userId;
	
	@Field(name="first_name")
	String firstName;
	
	@Field(name="last_name")
	String lastName;
	
	@Field(name="date_created")
	Date dateCreated;

	@Field(name="date_updataed")
	Date dateUpdated;
	
	String comments;
	int attempt;
	int score;
	String type;

	public CommentModel(Comment comment) {
		this.commentId = comment.getCommentId();
		this.userId = comment.getUserId();
		this.dateCreated = comment.getDateCreated();
		this.dateUpdated = comment.getDateUpdated();
		this.comments = comment.getComments();
		this.attempt = comment.getAttempt();
		this.score = comment.getScore();
		this.firstName = comment.getFirstName();
		this.lastName = comment.getLastName();
		this.type = comment.getType();
	}

	/**
	 * When assigning a score report to a new evaluator, the old comments are imported as
	 * new comments for the new evaluator.  This keeps the old information while allowing
	 * the new evaluator to be able to edit the comment.
	 * 
	 * @param comment  The CommentModel from the old evaluator
	 * @param user  The UserModel for the new evaluator
	 */
	public CommentModel(CommentModel comment, String userId, String firstName, String lastName) {
		this.commentId = UUID.randomUUID();
		this.userId = userId;
		this.dateCreated = DateUtil.getZonedNow();
		this.dateUpdated = this.dateCreated;
		this.comments = comment.getComments();
		this.attempt = comment.getAttempt();
		this.score = comment.getScore();
		this.firstName = firstName;
		this.lastName = lastName;
		this.type = comment.getType();
	}
	
	public CommentModel(String userId, String firstName, String lastName, String comments, int attempt, int score, String type) {
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
