package edu.wgu.dmadmin.controller;

import java.util.Arrays;
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

import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.user.User;
import edu.wgu.dmadmin.domain.user.UserListResponse;
import edu.wgu.dmadmin.domain.user.UserResponse;
import edu.wgu.dmadmin.service.UserManagementService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dreammachine.domain.security.SecureByPermissionStrategy;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1")
public class UserManagementController {

	@Autowired
	private UserManagementService userService;

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_SEARCH)
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
	public ResponseEntity<UserResponse> getUser(@PathVariable final String userId) {
		UserResponse result = new UserResponse(this.userService.getUser(userId));
		return new ResponseEntity<UserResponse>(result, HttpStatus.OK);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_CREATE)
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void addUsers(@RequestBody User[] users) {
		this.userService.addUsers(Arrays.asList(users));
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_CREATE)
	@RequestMapping(value = "/users/{username}", method = RequestMethod.POST)
	public ResponseEntity<User> createUser(@PathVariable String username) {
		User result = new User(this.userService.createUser(username));
		return new ResponseEntity<User>(result, HttpStatus.OK);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_DELETE)
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteEvaluator(@PathVariable final String userId) {
		this.userService.deleteUser(userId);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_SEARCH)
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<UserListResponse> getAllUsers() {
		UserListResponse result = new UserListResponse(this.userService.getUsers());
		return new ResponseEntity<UserListResponse>(result, HttpStatus.OK);
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_SEARCH)
	@RequestMapping(value = "/users/task/{taskId}", method = RequestMethod.GET)
	public ResponseEntity<UserListResponse> getUsersForTask(@PathVariable final UUID taskId) {
		UserListResponse result = new UserListResponse(this.userService.getUsersForTask(taskId));
		return new ResponseEntity<UserListResponse>(result, HttpStatus.OK);
	}
}
