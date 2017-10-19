package edu.wgu.dmadmin.domain.search;

import java.util.List;

import edu.wgu.dmadmin.domain.submission.DashboardSubmission;

import lombok.Data;

@Data
public class SearchResponse {
	private SearchCriteria criteria;
	private List<DashboardSubmission> submissions;

	public SearchResponse(SearchCriteria criteria, List<DashboardSubmission> submissions) {
		this.criteria = criteria;
		this.submissions = submissions;
	}
}
