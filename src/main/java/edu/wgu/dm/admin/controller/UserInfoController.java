package edu.wgu.dm.admin.controller;

import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.wgu.dm.admin.service.UserInfoService;
import edu.wgu.dm.audit.Audit;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.security.strategy.SecureByPermissionStrategy;
import edu.wgu.dm.util.IdentityUtil;
import edu.wgu.dm.util.Permissions;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.IgnoreAuthorization;
import edu.wgu.security.authz.annotation.Secured;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Jessica Pamdeth
 */
@RestController
@RequestMapping("v1")
public class UserInfoController {

    @Autowired
    private UserInfoService service;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @IgnoreAuthorization
    @GetMapping(value = "/person", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("View details about the current user.")
    @ApiImplicitParam(name = "Authorization", value = "All authenticated", dataType = "string", paramType = "header",
            required = true)
    public ResponseEntity<Person> getPerson(HttpServletRequest request) throws ParseException {
        return ResponseEntity.ok(this.service.getPersonFromRequest(request, this.iUtil.getUserId()));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @GetMapping(value = "/person/{bannerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("View details about the specified user.")
    @ApiImplicitParam(name = "Authorization", value = "User-Search permission", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<Person> getPerson(@PathVariable final String bannerId) {
        return ResponseEntity.ok(this.service.getPersonByUserId(bannerId));
    }
}
