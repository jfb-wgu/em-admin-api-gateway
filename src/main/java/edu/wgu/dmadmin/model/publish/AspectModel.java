package edu.wgu.dmadmin.model.publish;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.publish.Aspect;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name="aspect")
public class AspectModel {
	String name;
	String description;
	
	@Frozen
	List<AnchorModel> anchors;

	@Field(name="passing_score")
	int passingScore;
	
	@Field(name="aspect_order")
	int order;
	
	@Field(name="lr_url")
	String lrURL;
	
	public List<AnchorModel> getAnchors() {
		if (anchors == null) anchors = new ArrayList<AnchorModel>();
		return anchors;
	}
		
	public AspectModel(Aspect aspect) {
		this.name = aspect.getName();
		this.description = aspect.getDescription();
		this.passingScore = aspect.getPassingScore();
		this.order = aspect.getOrder();
		this.lrURL = aspect.getLrURL();
		this.anchors = aspect.getAnchors().stream().map(anchor -> new AnchorModel(anchor)).collect(Collectors.toList());
	}
}
