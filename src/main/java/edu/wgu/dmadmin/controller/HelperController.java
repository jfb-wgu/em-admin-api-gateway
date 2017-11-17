package edu.wgu.dmadmin.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.service.HelperService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dreammachine.domain.security.SecureByPermissionStrategy;
import edu.wgu.dreammachine.domain.submission.SubmissionData;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/helper")
public class HelperController {

	@Autowired
	HelperService helperService;

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SYSTEM)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/delete/{submissionId}", method = RequestMethod.DELETE)
	public void deleteSubmission(@PathVariable final UUID submissionId) {
		this.helperService.deleteSubmission(submissionId);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SYSTEM)
	@RequestMapping(value = "/view/{submissionId}", method = RequestMethod.GET)
	public SubmissionData getSubmission(@PathVariable final UUID submissionId) {
		return this.helperService.getSubmission(submissionId);
	}
}
