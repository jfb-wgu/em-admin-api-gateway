package edu.wgu.dmadmin.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.repo.oracle.StatusEntry;
import edu.wgu.dmadmin.service.HealthService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dreamcatcher.domain.model.AssessmentModel;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@RestController
@RequestMapping("v1/health")
public class HealthController {
	
	@Autowired
	private HealthService service;

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SYSTEM)
	@RequestMapping(value = { "/assessments" }, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public ResponseEntity<List<StatusEntry>> getMissingAssessmentRecords(@RequestBody final List<UUID> assessmentIds) {
		return ResponseEntity.ok(this.service.compareDRFData(assessmentIds));
	}
	
	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SYSTEM)
	@RequestMapping(value = { "/assessments/{date}" }, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public ResponseEntity<List<StatusEntry>> getMissingAssessmentRecords(@PathVariable @DateTimeFormat(iso=ISO.DATE) final Date date) {
		return ResponseEntity.ok(this.service.compareDRFData(date));
	}
	
	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SYSTEM)
	@RequestMapping(value = { "/assessments/{assessmentId}/student/{studentId}" }, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public ResponseEntity<AssessmentModel> sendAssessmentUpdate(@PathVariable final UUID assessmentId, @PathVariable final String studentId) {
		return ResponseEntity.ok(this.service.resendAssessmentUpdate(studentId, assessmentId));
	}
	
	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.SYSTEM)
	@RequestMapping(value = { "/environment" }, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public ResponseEntity<Map<String, String>> getEnvironment() {
		return ResponseEntity.ok(this.service.getEnvironment());
	}
}
