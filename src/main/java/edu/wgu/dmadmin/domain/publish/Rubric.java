package edu.wgu.dmadmin.domain.publish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.wgu.dmadmin.model.publish.RubricModel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Rubric {
	String name;
	String description;
	List<Aspect> aspects;

	@JsonGetter("aspects")
	public List<Aspect> getAspects() {
		this.aspects = ListUtils.defaultIfNull(this.aspects, new ArrayList<>());
		Collections.sort(this.aspects);
		return this.aspects;
	}

	public Rubric(RubricModel model) {
		this.name = model.getName();
		this.description = model.getDescription();
		this.aspects = model.getAspects().stream().map(aspect -> new Aspect(aspect)).collect(Collectors.toList());
	}
}
