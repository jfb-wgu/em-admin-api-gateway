package edu.wgu.dmadmin.domain.publish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.wgu.dmadmin.model.publish.AspectModel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Aspect implements Comparable<Aspect> {
	String name;
	String description;
	List<Anchor> anchors;
	int passingScore;
	int order;
	String lrURL;

	public Aspect(AspectModel model) {
		this.name = model.getName();
		this.description = model.getDescription();
		this.passingScore = model.getPassingScore();
		this.order = model.getOrder();
		this.lrURL = model.getLrURL();
		this.anchors = model.getAnchors().stream().map(anchor -> new Anchor(anchor)).collect(Collectors.toList());
	}

	@JsonGetter("anchors")
	public List<Anchor> getAnchors() {
		this.anchors = ListUtils.defaultIfNull(this.anchors, new ArrayList<>());
		Collections.sort(this.anchors);
		return this.anchors;
	}

	@Override
	public int compareTo(Aspect o) {
		return this.order - o.getOrder();
	}
}
