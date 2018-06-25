package edu.wgu.dmadmin.domain.report;

import java.util.UUID;
import edu.wgu.dmadmin.model.publish.CompetencyModel;
import lombok.Data;
import lombok.NoArgsConstructor;

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
