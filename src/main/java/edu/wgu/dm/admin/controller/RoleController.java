package edu.wgu.dm.admin.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import edu.wgu.boot.auth.authz.annotation.HasAnyRole;
import edu.wgu.boot.auth.authz.annotation.Secured;
import edu.wgu.dm.admin.service.RoleService;
import edu.wgu.dm.audit.Audit;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.security.strategy.SecureByPermissionStrategy;
import edu.wgu.dm.util.IdentityUtil;
import edu.wgu.dm.util.Permissions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author Jessica Pamdeth
 */
@RestController
@Api("Role management services.  Modifying an existing role will affect any users with the role.")
@RequestMapping("v1/admin")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class RoleController {

    private RoleService service;

    private IdentityUtil iUtil;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @PostMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Add one or more roles.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
            paramType = "header", required = true)
    // TODO Change this to take a single Role rather than an array.
    public ResponseEntity<List<Role>> saveRoles(@RequestBody Role[] roles) {
        return ResponseEntity.ok(this.service.saveRoles(this.iUtil.getUserId(), roles));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("List all roles.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(this.service.getRoles());
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @GetMapping(value = "/roles/{roleId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Get details for specified role.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<Role> getRole(@PathVariable final Long roleId) {
        return ResponseEntity.ok(this.service.getRole(roleId));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.ROLE_CREATE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/roles/{roleId}")
    @ApiOperation("Delete specified role.")
    @ApiImplicitParam(name = "Authorization", value = "Role-Create permission", dataType = "string",
            paramType = "header", required = true)
    public void deleteRole(@PathVariable final Long roleId) {
        this.service.deleteRole(roleId);
    }
}
