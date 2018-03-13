package edu.wgu.dmadmin.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.wgu.dmadmin.domain.person.Person;
import edu.wgu.dmadmin.domain.security.LdapUser;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.service.DirectoryService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Jessica Pamdeth
 */
@RestController
@RequestMapping("v1")
public class DirectoryController {

    @Autowired
    private DirectoryService service;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.DIRECTORY_SEARCH)
    @RequestMapping(value = "/users/ldap/{group}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Get the users in the specified Active Directory group.")
	@ApiImplicitParam(name = "Authorization", value = "Directory-Search permission", dataType = "string", paramType = "header", required = true)
    public ResponseEntity<Set<LdapUser>> getMembersForGroup(
    		@ApiParam(allowableValues = "DM_Admin, DM_Evaluator, DM_Publish, DM_Faculty") @PathVariable final String group) {
        return new ResponseEntity<Set<LdapUser>>(this.service.getMembersForGroup(group), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.DIRECTORY_SEARCH)
    @RequestMapping(value = "/users/ldap/{group}/missing", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Get the users in the specified Active Directory group who do not have users in EMA.")
	@ApiImplicitParam(name = "Authorization", value = "Directory-Search permission", dataType = "string", paramType = "header", required = true)
    public ResponseEntity<Set<Person>> getMissingGroupMembers(
    		@ApiParam(allowableValues = "DM_Admin, DM_Evaluator, DM_Publish, DM_Faculty") @PathVariable final String group) {
        return new ResponseEntity<Set<Person>>(this.service.getMissingUsers(group), HttpStatus.OK);
    }
    
    public void setDirectoryService(DirectoryService dService) {
    		this.service = dService;
    }
}
