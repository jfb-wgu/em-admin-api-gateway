package edu.wgu.dmadmin.domain.submission;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.model.submission.CommentModel;
import edu.wgu.dmadmin.util.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Comment implements Comparable<Comment> {
	UUID commentId;
	String userId;
	Date dateCreated;
	String comments;
	String firstName;
	String lastName;
	int attempt;
	UUID submissionId;
	
	public Comment(User user, String inComment, UUID inSubmissionId, int inAttempt) {
		this.commentId = UUID.randomUUID();
		this.userId = user.getUserId();
		this.dateCreated = DateUtil.getZonedNow();
		this.comments = inComment;
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.attempt = inAttempt;
		this.submissionId = inSubmissionId;
	}
	
	public Comment(CommentModel comment, UUID inSubmissionId) {
		this.commentId = comment.getCommentId();
		this.userId = comment.getUserId();
		this.dateCreated = comment.getDateCreated();
		this.comments = comment.getComments();
		this.firstName = comment.getFirstName();
		this.lastName = comment.getLastName();
		this.attempt = comment.getAttempt();
		this.submissionId = inSubmissionId;
	}

	@Override
	public int compareTo(Comment o) {
		return Comparator.comparing(Comment::getDateCreated)
				.reversed()
				.compare(this, o);
	}
}
