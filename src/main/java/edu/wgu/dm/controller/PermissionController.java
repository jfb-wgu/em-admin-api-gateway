package edu.wgu.dm.controller;

import edu.wgu.boot.auth.authz.annotation.HasAnyRole;
import edu.wgu.boot.auth.authz.annotation.Secured;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.service.PermissionService;
import edu.wgu.dm.service.SecureByPermissionStrategy;
import edu.wgu.dm.util.Permissions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RestController
@Api("Permission management services. Modifying an existing permission may affect all users for the permission.")
@RequestMapping("v1/admin")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService service;

    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @GetMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("List all permissions.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
                      paramType = "header", required = true)
    public ResponseEntity<List<Permission>> getPermissions() {
        return ResponseEntity.ok(this.service.getPermissions());
    }


    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @GetMapping(value = "/permissions/{permissionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Get details for specified permission.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
                      paramType = "header", required = true)
    public ResponseEntity<Permission> getPermission(@PathVariable final Long permissionId) {
        return ResponseEntity.ok(this.service.getPermission(permissionId));
    }


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
