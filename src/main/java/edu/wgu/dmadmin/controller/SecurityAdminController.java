package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.security.Permission;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.Role;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.service.SecurityService;
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

import java.util.List;
import java.util.UUID;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/admin")
public class SecurityAdminController {

    @Autowired
    private SecurityService adminService;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    public ResponseEntity<List<Permission>> getPermissions() {
        return ResponseEntity.ok(this.adminService.getPermissions());
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/permissions", method = RequestMethod.POST)
    public void addPermissions(@RequestBody Permission[] permissions) {
        this.adminService.savePermissions(permissions);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @RequestMapping(value = "/roles", method = RequestMethod.POST)
    public List<Role> addRoles(@RequestBody Role[] roles) {
        return this.adminService.saveRoles(roles);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(this.adminService.getRoles());
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @RequestMapping(value = "/roles/{roleId}", method = RequestMethod.GET)
    public ResponseEntity<Role> getRole(@PathVariable final UUID roleId) {
        return ResponseEntity.ok(this.adminService.getRole(roleId));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/roles/{roleId}", method = RequestMethod.DELETE)
    public void deleteRole(@PathVariable final UUID roleId) {
        this.adminService.deleteRole(roleId);
    }
}
