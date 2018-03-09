package edu.wgu.dmadmin.domain.publish;

import edu.wgu.dmadmin.model.publish.AnchorModel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Anchor implements Comparable<Anchor> {
	String name;
	String description;
	int score;
	String aspectName;
	
	public Anchor(AnchorModel model) {
		this.name = model.getName();
		this.description = model.getDescription();
		this.score = model.getScore(); 
	}

	@Override
	public int compareTo(Anchor o) {
		return this.score - o.getScore();
	}
}
