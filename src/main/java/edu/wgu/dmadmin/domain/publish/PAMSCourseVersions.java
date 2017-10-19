package edu.wgu.dmadmin.domain.publish;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PAMSCourseVersions {
	private List<PAMSCourse> versions;

	public PAMSCourseVersions(List<PAMSCourse> list) {
		this.versions = list;
	}

	@JsonGetter("versions")
	public List<PAMSCourse> getVersions() {
		if (versions == null) return Collections.emptyList();

		Collections.sort(versions);
		return versions;
	}
	
	public PAMSCourse getCurrent() {
		return this.getVersions().get(0);
	}
}
