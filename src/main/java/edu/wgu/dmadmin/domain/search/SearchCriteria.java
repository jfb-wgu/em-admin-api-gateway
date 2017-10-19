package edu.wgu.dmadmin.domain.search;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class SearchCriteria {
	private String studentId;
	private String submissionId;
	private String evaluatorFirstName;
	private String evaluatorLastName;
	private String status;
	private List<UUID> tasks;
	private String dateRange;
}
