package edu.wgu.dmadmin.model.submission;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

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
	String type;
}
