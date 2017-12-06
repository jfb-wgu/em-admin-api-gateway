package edu.wgu.dmadmin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.wgu.dmadmin.domain.search.SearchCriteria;
import edu.wgu.dmadmin.domain.search.SearchResponse;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.service.SearchService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dreammachine.domain.security.Permissions;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@RestController
@RequestMapping("v1/search")
public class SearchController {

	@Autowired
	private SearchService service;

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SUBMISSION_SEARCH)
	@RequestMapping(value = { "/submissions" }, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public ResponseEntity<SearchResponse> getSubmissionsByCriteria(@RequestBody final SearchCriteria criteria) {
		SearchResponse response = new SearchResponse(criteria, this.service.search(criteria));
		return new ResponseEntity<SearchResponse>(response, HttpStatus.OK);
	}
	
	public void setSearchService(SearchService sService) {
		this.service = sService;
	}
}
