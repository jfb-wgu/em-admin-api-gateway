package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.domain.submission.SubmissionData;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.service.HelperService;
import edu.wgu.dmadmin.util.IdentityUtil;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;


/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/helper")
public class HelperController {

    @Autowired
    HelperService helperService;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.SYSTEM)
    @RequestMapping(value = "/evaluate", method = RequestMethod.POST)
    public ResponseEntity<Submission> evaluateTestSubmission(@RequestBody Submission submission) throws UserIdNotFoundException {
        return new ResponseEntity<Submission>(this.helperService.completeEvaluation(submission, this.iUtil.getUserId()), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.SYSTEM)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/delete/{submissionId}", method = RequestMethod.DELETE)
    public void deleteSubmission(@PathVariable final UUID submissionId) {
        this.helperService.deleteSubmission(submissionId);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.SYSTEM)
    @RequestMapping(value = "/view/{submissionId}", method = RequestMethod.GET)
    public SubmissionData getSubmission(@PathVariable final UUID submissionId) {
        return this.helperService.getSubmission(submissionId);
    }
}
