package edu.wgu.dmadmin.domain.publish;

import edu.wgu.dmadmin.model.publish.CompetencyModel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Competency {
	String code;
	String name;
	String description;
	UUID taskId;
	
	public Competency(CompetencyModel model, UUID inTaskId) {
		this.code = model.getCode();
		this.name = model.getName();
		this.description = model.getDescription();
		this.taskId = inTaskId;
	}
}
