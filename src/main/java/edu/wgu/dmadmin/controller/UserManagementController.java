package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.evaluator.UserListResponse;
import edu.wgu.dmadmin.domain.evaluator.UserResponse;
import edu.wgu.dmadmin.domain.security.LdapUser;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.Person;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.service.DirectoryService;
import edu.wgu.dmadmin.service.UserManagementService;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.IgnoreAuthorization;
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

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1")
public class UserManagementController {

    @Autowired
    private UserManagementService userService;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/person", method = RequestMethod.GET)
    public ResponseEntity<Person> getPerson(HttpServletRequest request) throws UserIdNotFoundException, ParseException {
        return ResponseEntity.ok(this.userService.getPersonFromRequest(request, this.iUtil.getUserId()));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @RequestMapping(value = "/person/bannerId/{bannerId}", method = RequestMethod.GET)
    public ResponseEntity<Person> getPerson(@PathVariable final String bannerId) {
        return ResponseEntity.ok(this.userService.getPersonByUserId(bannerId));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> getUser(@PathVariable final String userId) {
        UserResponse result = new UserResponse(this.userService.getUser(userId));
        return new ResponseEntity<UserResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_CREATE)
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void addUsers(@RequestBody User[] users) {
        this.userService.addUsers(Arrays.asList(users));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_CREATE)
    @RequestMapping(value = "/users/{username}", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@PathVariable String username) {
        User result = new User(this.userService.createUser(username));
        return new ResponseEntity<User>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_DELETE)
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteEvaluator(@PathVariable final String userId) {
        this.userService.deleteUser(userId);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<UserListResponse> getAllUsers() {
        UserListResponse result = new UserListResponse(this.userService.getUsers());
        return new ResponseEntity<UserListResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @RequestMapping(value = "/users/task/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<UserListResponse> getUsersForTask(@PathVariable final UUID taskId) {
        UserListResponse result = new UserListResponse(this.userService.getUsersForTask(taskId));
        return new ResponseEntity<UserListResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.DIRECTORY_SEARCH)
    @RequestMapping(value = "/users/ldap/{group}", method = RequestMethod.GET)
    public ResponseEntity<Set<LdapUser>> getMembersForGroup(@PathVariable final String group) {
        return new ResponseEntity<Set<LdapUser>>(this.directoryService.getMembersForGroup(group), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.DIRECTORY_SEARCH)
    @RequestMapping(value = "/users/ldap/{group}/missing", method = RequestMethod.GET)
    public ResponseEntity<Set<Person>> getMissingGroupMembers(@PathVariable final String group) {
        return new ResponseEntity<Set<Person>>(this.userService.getMissingUsers(group), HttpStatus.OK);
    }
}
