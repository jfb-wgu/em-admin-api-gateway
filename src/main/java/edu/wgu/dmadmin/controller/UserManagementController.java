package edu.wgu.dmadmin.controller;

import java.util.Arrays;
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

import edu.wgu.dmadmin.domain.security.BulkCreateResponse;
import edu.wgu.dmadmin.domain.security.BulkUsers;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.domain.security.UserListResponse;
import edu.wgu.dmadmin.domain.security.UserResponse;
import edu.wgu.dmadmin.service.UserManagementService;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

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
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_SEARCH)
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
	public ResponseEntity<UserResponse> getUser(@PathVariable final String userId) {
		UserResponse result = new UserResponse(this.service.getUser(userId));
		return ResponseEntity.ok().body(result);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_CREATE)
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void addUsers(@RequestBody User[] users) {
		this.service.addUsers(this.iUtil.getUserId(), Arrays.asList(users));
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_CREATE)
	@RequestMapping(value = "/users/{username}", method = RequestMethod.POST)
	public ResponseEntity<User> createUser(@PathVariable String username) {
		User result = new User(this.service.createUser(username));
		return ResponseEntity.ok().body(result);
	}
	
	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_CREATE)
	@RequestMapping(value = "/users/bulk", method = RequestMethod.POST)
	public ResponseEntity<BulkCreateResponse> createUsers(@RequestBody BulkUsers users) {
		BulkCreateResponse result = this.service.createUsers(this.iUtil.getUserId(), users);
		return ResponseEntity.ok().body(result);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_DELETE)
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable final String userId) {
		this.service.deleteUser(userId);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_SEARCH)
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<UserListResponse> getAllUsers() {
		UserListResponse result = new UserListResponse(this.service.getUsers());
		return ResponseEntity.ok().body(result);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_SEARCH)
	@RequestMapping(value = "/users/task/{taskId}", method = RequestMethod.GET)
	public ResponseEntity<UserListResponse> getUsersForTask(@PathVariable final UUID taskId) {
		UserListResponse result = new UserListResponse(this.service.getUsersForTask(taskId));
		return ResponseEntity.ok().body(result);
	}
	
	public void setUserManagementService(UserManagementService umService) {
		this.service = umService;
	}
	
	public void setIdentityUtil(IdentityUtil util) {
		this.iUtil = util;
	}
}
