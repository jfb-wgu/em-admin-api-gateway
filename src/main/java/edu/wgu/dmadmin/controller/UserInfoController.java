package edu.wgu.dmadmin.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.wgu.dmadmin.domain.security.IdentityUtil;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.service.UserInfoService;
import edu.wgu.dmaudit.audit.Audit;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.IgnoreAuthorization;
import edu.wgu.security.authz.annotation.Secured;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1")
public class UserInfoController {

	@Autowired
	private UserInfoService service;

	@Autowired
	private IdentityUtil iUtil;

	@Audit
	@IgnoreAuthorization
	@RequestMapping(value = "/person", method = RequestMethod.GET)
	public ResponseEntity<Person> getPerson(HttpServletRequest request) throws ParseException {
		return ResponseEntity.ok(this.service.getPersonFromRequest(request, this.iUtil.getUserId()));
	}

	@Audit
	@Secured(strategies = { SecureByPermissionStrategy.class })
	@HasAnyRole(Permissions.USER_SEARCH)
	@RequestMapping(value = "/person/{bannerId}", method = RequestMethod.GET)
	public ResponseEntity<Person> getPerson(@PathVariable final String bannerId) {
		return ResponseEntity.ok(this.service.getPersonByUserId(bannerId));
	}

	public void setUserInfoService(UserInfoService uiService) {
		this.service = uiService;
	}
}
