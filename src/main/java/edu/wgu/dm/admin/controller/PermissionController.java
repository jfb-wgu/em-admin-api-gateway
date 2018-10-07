package edu.wgu.dm.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import edu.wgu.dm.admin.service.PermissionService;
import edu.wgu.dm.audit.Audit;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.security.strategy.SecureByPermissionStrategy;
import edu.wgu.dm.util.Permissions;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Jessica Pamdeth
 */
@RestController
@Api("Permission management services.  Modifying an existing permission may affect all users for the permission.")
@RequestMapping("v1/admin")
public class PermissionController {

    @Autowired
    private PermissionService service;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @GetMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("List all permissions.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<List<Permission>> getPermissions() {
        return ResponseEntity.ok(this.service.getPermissions());
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @GetMapping(value = "/permissions/{permissionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Get details for specified permission.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<Permission> getPermission(@PathVariable final Long permissionId) {
        return ResponseEntity.ok(this.service.getPermission(permissionId));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PostMapping(value = "/permissions")
    @ApiOperation("Add one or more permissions.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
            paramType = "header", required = true)
    public void addPermissions(@RequestBody Permission[] permissions) {
        this.service.savePermissions(permissions);
    }
}
