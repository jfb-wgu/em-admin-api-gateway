package edu.wgu.dmadmin.controller;

import java.util.List;
import java.util.UUID;

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

import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.service.RoleService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dreammachine.domain.security.Permissions;
import edu.wgu.dreammachine.domain.security.Role;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/admin")
public class RoleController {

	@Autowired
	private RoleService service;

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.ROLE_CREATE)
	@RequestMapping(value = "/roles", method = RequestMethod.POST)
	public List<Role> addRoles(@RequestBody Role[] roles) {
		return this.service.saveRoles(roles);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.ROLE_CREATE)
	@RequestMapping(value = "/roles", method = RequestMethod.GET)
	public ResponseEntity<List<Role>> getRoles() {
		return ResponseEntity.ok(this.service.getRoles());
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.ROLE_CREATE)
	@RequestMapping(value = "/roles/{roleId}", method = RequestMethod.GET)
	public ResponseEntity<Role> getRole(@PathVariable final UUID roleId) {
		return ResponseEntity.ok(this.service.getRole(roleId));
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.ROLE_CREATE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/roles/{roleId}", method = RequestMethod.DELETE)
	public void deleteRole(@PathVariable final UUID roleId) {
		this.service.deleteRole(roleId);
	}
	
	public void setRoleService(RoleService rService) {
		this.service = rService;
	}
}
