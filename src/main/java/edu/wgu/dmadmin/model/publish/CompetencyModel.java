package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.publish.Competency;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name="competency")
public class CompetencyModel {
	String code;
	String name;
	String description;
	
	public CompetencyModel(Competency competency) {
		this.code = competency.getName();
		this.name = competency.getCode();
		this.description = competency.getDescription();
	}
}
