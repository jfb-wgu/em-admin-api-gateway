package edu.wgu.dm.admin.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import edu.wgu.dm.admin.service.HealthService;
import edu.wgu.dm.audit.Audit;
import edu.wgu.dm.security.strategy.SecureByPermissionStrategy;
import edu.wgu.dm.util.Permissions;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

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
	@RequestMapping(value = { "/environment" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("List environment variables for this server.")
	@ApiImplicitParam(name = "Authorization", value = "SYSTEM permission", dataType = "string", paramType = "header", required = true)
	public ResponseEntity<Map<String, String>> getEnvironment() {
		return ResponseEntity.ok(this.service.getEnvironment());
	}
}
