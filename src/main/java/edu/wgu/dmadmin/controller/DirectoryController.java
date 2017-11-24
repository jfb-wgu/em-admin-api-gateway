package edu.wgu.dmadmin.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.wgu.dmadmin.domain.security.LdapUser;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.service.DirectoryService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dreammachine.domain.security.Permissions;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1")
public class DirectoryController {

    @Autowired
    private DirectoryService service;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.DIRECTORY_SEARCH)
    @RequestMapping(value = "/users/ldap/{group}", method = RequestMethod.GET)
    public ResponseEntity<Set<LdapUser>> getMembersForGroup(@PathVariable final String group) {
        return new ResponseEntity<Set<LdapUser>>(this.service.getMembersForGroup(group), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.DIRECTORY_SEARCH)
    @RequestMapping(value = "/users/ldap/{group}/missing", method = RequestMethod.GET)
    public ResponseEntity<Set<Person>> getMissingGroupMembers(@PathVariable final String group) {
        return new ResponseEntity<Set<Person>>(this.service.getMissingUsers(group), HttpStatus.OK);
    }
    
    public void setDirectoryService(DirectoryService dService) {
    		this.service = dService;
    }
}
