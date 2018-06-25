package edu.wgu.dmadmin.model.publish;

import java.util.ArrayList;
import java.util.List;
import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.UDT;
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
		if (this.anchors == null) this.anchors = new ArrayList<AnchorModel>();
		return this.anchors;
	}
}
