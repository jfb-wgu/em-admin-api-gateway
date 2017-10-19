package edu.wgu.dmadmin.domain.assessment;

import java.util.Date;
import java.util.UUID;

import edu.wgu.dmadmin.model.assessment.CommentModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Comment implements Comparable<Comment> {
	UUID commentId;
	String userId;
	Date dateCreated;
	Date dateUpdated;
	
	String comments;
	int attempt;
	int score;
	String firstName;
	String lastName;
	String type;
	
	public Comment(String comments, String type) {
		this.setComments(comments);
		this.setType(type);
	}
	
	public Comment(CommentModel comment) {
		if (comment == null) return;
		
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

	@Override
	public int compareTo(Comment o) {
		if (this.getAttempt() == o.getAttempt()) {
			return o.getDateCreated().compareTo(this.getDateCreated());
		} else {
			return o.attempt - this.attempt;
		}
	}
}
