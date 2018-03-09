package edu.wgu.dmadmin.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.service.PermissionService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dreammachine.domain.security.Permission;
import edu.wgu.dreammachine.domain.security.Permissions;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@RestController
@RequestMapping("v1/admin")
public class PermissionController {

	@Autowired
	private PermissionService service;

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.ROLE_CREATE)
	@RequestMapping(value = "/permissions", method = RequestMethod.GET)
	public ResponseEntity<List<Permission>> getPermissions() {
		return ResponseEntity.ok(this.service.getPermissions());
	}
	
	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.ROLE_CREATE)
	@RequestMapping(value = "/permissions/{permissionId}", method = RequestMethod.GET)
	public ResponseEntity<Permission> getPermission(@PathVariable final UUID permissionId) {
		return ResponseEntity.ok(this.service.getPermission(permissionId));
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.ROLE_CREATE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/permissions", method = RequestMethod.POST)
	public void addPermissions(@RequestBody Permission[] permissions) {
		this.service.savePermissions(permissions);
	}
	
	public void setPermissionService(PermissionService pService) {
		this.service = pService;
	}
}
