package edu.wgu.dm.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import edu.wgu.dm.admin.service.UserManagementService;
import edu.wgu.dm.audit.Audit;
import edu.wgu.dm.dto.security.BulkCreateResponse;
import edu.wgu.dm.dto.security.BulkUsers;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserListResponse;
import edu.wgu.dm.dto.security.UserResponse;
import edu.wgu.dm.security.strategy.SecureByPermissionStrategy;
import edu.wgu.dm.util.IdentityUtil;
import edu.wgu.dm.util.Permissions;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Jessica Pamdeth
 */
@RestController
@RequestMapping("v1")
public class UserManagementController {

    @Autowired
    private UserManagementService service;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("View the specified EMA user.")
    @ApiImplicitParam(name = "Authorization", value = "User-Search permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<UserResponse> getUser(@PathVariable final String userId) {
        // TODO return simple User.
        UserResponse result = new UserResponse(this.service.getUser(userId));
        return ResponseEntity.ok(result);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_CREATE)
    @PostMapping(value = "/users")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ApiOperation("Save or update an EMA user.")
    @ApiImplicitParam(name = "Authorization", value = "User-Create permission", dataType = "string",
            paramType = "header", required = true)
    public void saveUser(@RequestBody User user) {
        this.service.saveUser(this.iUtil.getUserId(), user);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_CREATE)
    @PostMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Create or retrieve a user skeleton from WGU username.")
    @ApiImplicitParam(name = "Authorization", value = "User-Create permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<User> createUser(@PathVariable String username) {
        User result = this.service.createUser(username);
        return ResponseEntity.ok(result);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_CREATE)
    @PostMapping(value = "/users/bulk", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Create multiple users from WGU usernames.  Existing user information will be preserved.")
    @ApiImplicitParam(name = "Authorization", value = "User-Create permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<BulkCreateResponse> createUsers(@RequestBody BulkUsers users) {
        BulkCreateResponse result = this.service.createUsers(this.iUtil.getUserId(), users);
        return ResponseEntity.ok(result);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_DELETE)
    @DeleteMapping(value = "/users/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ApiOperation("Remove a user from EMA.")
    @ApiImplicitParam(name = "Authorization", value = "User-Delete permission", dataType = "string",
            paramType = "header", required = true)
    public void deleteUser(@PathVariable final String userId) {
        this.service.deleteUser(userId);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("List all EMA users.")
    @ApiImplicitParam(name = "Authorization", value = "User-Search permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<UserListResponse> getAllUsers() {
        UserListResponse result = new UserListResponse(this.service.getUsers());
        return ResponseEntity.ok(result);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @GetMapping(value = "/users/task/{taskId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("List all EMA users who are qualified on the specified task.")
    @ApiImplicitParam(name = "Authorization", value = "User-Search permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<UserListResponse> getUsersForTask(@PathVariable final Long taskId) {
        UserListResponse result = new UserListResponse(this.service.getUsersForTask(taskId));
        return ResponseEntity.ok(result);
    }
}
