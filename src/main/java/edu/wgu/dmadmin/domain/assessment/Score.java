package edu.wgu.dmadmin.domain.assessment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.wgu.dmadmin.domain.publish.Anchor;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.publish.AspectModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Score implements Comparable<Score> {
	String name;
	String description;
	int passingScore;
	int assignedScore = -1;
	int previousScore = -1;
	List<Comment> comments;
	List<Anchor> anchors;
	int order;
	String lrURL;

	public Score(ScoreModel model) {
		this.name = model.getName();
		this.passingScore = model.getPassingScore();
		this.assignedScore = model.getAssignedScore();
		this.previousScore = model.getPreviousScore();
		this.comments = model.getComments().values().stream().map(comment -> new Comment(comment)).collect(Collectors.toList());
	}

	public Score(AspectModel model) {
		this.name = model.getName();
		this.passingScore = model.getPassingScore();
		this.order =  model.getOrder();
		this.description = model.getDescription();
		this.lrURL = model.getLrURL();
		
		if (model.getAnchors()!= null) {
			this.anchors = model.getAnchors().stream().map(anchor -> new Anchor(anchor)).collect(Collectors.toList());
		}
	}
	    
    @JsonGetter("comments")
    public List<Comment> getComments() {
    	if (comments == null) return new ArrayList<>();
    	
    	Collections.sort(comments);
    	return comments;
    }
    
    @JsonGetter("anchors")
    public List<Anchor> getAnchors() {
    	if (anchors == null) return Collections.emptyList();
    	
    	Collections.sort(anchors);
    	return anchors;
    }

	@Override
	public int compareTo(Score o) {
		return this.order - o.getOrder();
	}
}
