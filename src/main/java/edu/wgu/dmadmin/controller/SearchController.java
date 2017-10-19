package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.audit.StatusLogResponse;
import edu.wgu.dmadmin.domain.search.SearchCriteria;
import edu.wgu.dmadmin.domain.search.SearchResponse;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.service.SearchService;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.SUBMISSION_SEARCH)
    @RequestMapping(value = {"", "/submissions"}, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ResponseEntity<SearchResponse> getSubmissionsByCriteria(@RequestBody final SearchCriteria criteria) {
        SearchResponse response = new SearchResponse(criteria, this.searchService.search(criteria));
        return new ResponseEntity<SearchResponse>(response, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.SUBMISSION_SEARCH)
    @RequestMapping(value = "/logs/assessment/{assessmentId}", method = RequestMethod.GET)
    public ResponseEntity<StatusLogResponse> getLogsForAssessment(@PathVariable final UUID assessmentId) {
        StatusLogResponse result = new StatusLogResponse(this.searchService.getStatusLogByAssessment(assessmentId));
        return new ResponseEntity<StatusLogResponse>(result, HttpStatus.OK);
    }
}
