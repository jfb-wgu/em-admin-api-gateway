package edu.wgu.dmadmin.util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.wgu.common.domain.AuthzIdentityKeys;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;

import static org.junit.Assert.assertEquals;

public class IdentityUtilTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	IdentityUtil util = new IdentityUtil();
	
	AuthzIdentityKeys keys = new AuthzIdentityKeys();
	Long pidm = new Long(1324345);
	String bannerId = "student";
	String username = "username";
	Set<String> roles = new HashSet<String>();
	
	MockHttpServletRequest request = new MockHttpServletRequest();
	
	@Before
	public void initialize() {
		this.roles.add("testing");
		
		this.keys.setBannerId(bannerId);
		this.keys.setPidm(pidm);
		this.keys.setRoles(roles);
		this.keys.setUsername(username);

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}
	
	@Test
	public void testGetUserId() throws UserIdNotFoundException {
		request.setAttribute(AuthzIdentityKeys.AUTHZ_IDENTITY_KEYS, this.keys);
		String result = this.util.getUserId();
		assertEquals(result, this.bannerId);
	}
	
	@Test
	public void testGetUserName() throws UserIdNotFoundException {
		request.setAttribute(AuthzIdentityKeys.AUTHZ_IDENTITY_KEYS, this.keys);
		String result = this.util.getUserName();
		assertEquals(result, this.username);
	}
	
	@Test
	public void testGetUserRoles() throws UserIdNotFoundException {
		request.setAttribute(AuthzIdentityKeys.AUTHZ_IDENTITY_KEYS, this.keys);
		Set<String> result = this.util.getUserRoles();
		assertEquals(result, this.roles);
	}
	
	@Test
	public void testGetUserPidm() throws UserIdNotFoundException {
		request.setAttribute(AuthzIdentityKeys.AUTHZ_IDENTITY_KEYS, this.keys);
		Long result = this.util.getUserPidm();
		assertEquals(result, this.pidm);
	}
	
	@Test
	public void testGetUserIdNoKeys() throws UserIdNotFoundException {
		thrown.expect(UserIdNotFoundException.class);
		this.util.getUserId();
	}
	
	@Test
	public void testGetUserNameNoKeys() throws UserIdNotFoundException {
		thrown.expect(UserIdNotFoundException.class);
		this.util.getUserName();
	}
	
	@Test
	public void testGetUserRolesNoKeys() throws UserIdNotFoundException {
		thrown.expect(UserIdNotFoundException.class);
		this.util.getUserRoles();
	}
	
	@Test
	public void testGetUserPidmNoKeys() throws UserIdNotFoundException {
		thrown.expect(UserIdNotFoundException.class);
		this.util.getUserPidm();
	}
}
