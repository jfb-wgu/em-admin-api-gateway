package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.publish.Anchor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name="anchor")
public class AnchorModel {
	String name;
	String description;
	int score;
	
	public AnchorModel(Anchor anchor) {
		this.name = anchor.getName();
		this.description = anchor.getDescription();
		this.score = anchor.getScore();
	}
}
