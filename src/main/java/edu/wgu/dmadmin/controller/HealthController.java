package edu.wgu.dmadmin.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.service.HealthService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dreamcatcher.domain.model.AssessmentModel;
import edu.wgu.dreammachine.domain.security.Permissions;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/health")
public class HealthController {
	
	@Autowired
	private HealthService service;

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SYSTEM)
	@RequestMapping(value = { "/assessments" }, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public ResponseEntity<List<AssessmentModel>> getMissingAssessmentRecords(@RequestBody final List<UUID> assessmentIds) {
		return ResponseEntity.ok(this.service.compareDRFData(assessmentIds));
	}
	
	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SYSTEM)
	@RequestMapping(value = { "/assessments/{date}" }, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public ResponseEntity<List<AssessmentModel>> getMissingAssessmentRecords(@PathVariable @DateTimeFormat(iso=ISO.DATE) final Date date) {
		return ResponseEntity.ok(this.service.compareDRFData(date));
	}
}
