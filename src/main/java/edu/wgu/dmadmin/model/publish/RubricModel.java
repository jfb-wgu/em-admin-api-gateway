package edu.wgu.dmadmin.model.publish;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.publish.Rubric;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@UDT(keyspace = "dm", name="rubric")
public class RubricModel {
	String name;
	String description;
	
	@Frozen
	List<AspectModel> aspects;
	
	public List<AspectModel> getAspects() {
		if (aspects == null) aspects = new ArrayList<AspectModel>();
		return aspects;
	}
	
	public RubricModel(Rubric rubric) {
		if (rubric == null) return;
		this.name = rubric.getName();
		this.description = rubric.getDescription();
		this.aspects = rubric.getAspects().stream().map(aspect -> new AspectModel(aspect)).collect(Collectors.toList());
	}
}
