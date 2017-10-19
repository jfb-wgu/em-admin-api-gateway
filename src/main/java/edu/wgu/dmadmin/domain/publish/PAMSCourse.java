package edu.wgu.dmadmin.domain.publish;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PAMSCourse implements Comparable<PAMSCourse> {
	Long id;
	String code;
	int reviewStatus;
	String courseStudytable;
	Long courseStudyId;
	int majorVersion;
	Date publishDate;
	String title;
	Date launchDate;
	
	@Override
	public int compareTo(PAMSCourse o) {
		return o.majorVersion - this.majorVersion;
	}
}
