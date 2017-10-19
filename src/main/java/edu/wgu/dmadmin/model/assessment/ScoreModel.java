package edu.wgu.dmadmin.model.assessment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.FrozenValue;
import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.assessment.Score;
import edu.wgu.dmadmin.model.publish.AspectModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name = "score")
public class ScoreModel {
	String name;
	
	@Field(name="passing_score")
	int passingScore;
		
	@Field(name="assigned_score")
	int assignedScore = -1;
	
	@Field(name="previous_score")
	int previousScore = -1;
	
	@FrozenValue
	Map<UUID, CommentModel> comments;
	
	public Map<UUID, CommentModel> getComments() {
		if (comments == null) comments = new HashMap<UUID, CommentModel>();
		return comments;
	}
		
	public ScoreModel(Score aspect) {
		this.name = aspect.getName();
		this.passingScore = aspect.getPassingScore();
		this.assignedScore = aspect.getAssignedScore();
		this.previousScore = aspect.getPreviousScore();
		this.comments = aspect.getComments().stream().collect(Collectors.toMap(c -> c.getCommentId(), c -> new CommentModel(c)));
	}
	
	public ScoreModel(AspectModel aspect) {
		this.name = aspect.getName();
		this.passingScore = aspect.getPassingScore();
	}
}
