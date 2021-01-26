package edu.wgu.dm.controller;

import edu.wgu.boot.auth.authz.annotation.HasAnyRole;
import edu.wgu.boot.auth.authz.annotation.IgnoreAuthorization;
import edu.wgu.boot.auth.authz.annotation.Secured;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.service.SecureByPermissionStrategy;
import edu.wgu.dm.service.UserInfoService;
import edu.wgu.dm.util.IdentityUtil;
import edu.wgu.dm.util.Permissions;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("v1")
public class UserInfoController {

    private final UserInfoService service;
    private final IdentityUtil iUtil;

    public UserInfoController(UserInfoService service, IdentityUtil iUtil) {
        this.service = service;
        this.iUtil = iUtil;
    }

    @IgnoreAuthorization
    @GetMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("View details about the current user.")
    @ApiImplicitParam(name = "Authorization", value = "All authenticated", dataType = "string", paramType = "header",
                      required = true)
    public ResponseEntity<Person> getPerson(HttpServletRequest request) throws ParseException {
        return ResponseEntity.ok(this.service.getPersonFromRequest(request, this.iUtil.getUserId()));
    }

    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_SEARCH)
    @GetMapping(value = "/person/{bannerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("View details about the specified user.")
    @ApiImplicitParam(name = "Authorization", value = "User-Search permission", dataType = "string",
                      paramType = "header", required = true)
    public ResponseEntity<Person> getPerson(@PathVariable final String bannerId) {
        return ResponseEntity.ok(this.service.getPersonByUserId(bannerId));
    }
}
