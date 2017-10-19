package edu.wgu.dmadmin.domain.publish;

import edu.wgu.dmadmin.model.publish.CompetencyModel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Competency {
	String code;
	String name;
	String description;
	
	public Competency(CompetencyModel model) {
		this.code = model.getCode();
		this.name = model.getName();
		this.description = model.getDescription();
	}
}
