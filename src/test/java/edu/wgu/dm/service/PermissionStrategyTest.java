package edu.wgu.dm.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import edu.wgu.boot.auth.authz.domain.AuthorizationInfo;
import edu.wgu.boot.auth.authz.domain.AuthzIdentityKeys;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.exception.UserIdNotFoundException;
import edu.wgu.dm.repository.SecurityRepo;
import edu.wgu.dm.util.Permissions;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(MockitoJUnitRunner.class)
public class PermissionStrategyTest {

    SecureByPermissionStrategy strategy;

    @Mock
    AuthorizationInfo info;

    @Mock
    User user;

    Set<String> reqRoles = new HashSet<>();
    Set<String> hasRoles = new HashSet<>();

    @Mock
    SecurityRepo repo;

    AuthzIdentityKeys keys = new AuthzIdentityKeys();
    Long pidm = 1324345L;
    String bannerId = "student";
    String username = "username";
    Set<String> roles = new HashSet<String>();

    MockHttpServletRequest request = new MockHttpServletRequest();

    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);

        this.strategy = new SecureByPermissionStrategy(this.repo);
        this.roles.add("employee");

        this.keys.setBannerId(this.bannerId);
        this.keys.setPidm(this.pidm);
        this.keys.setRoles(this.roles);
        this.keys.setUsername(this.username);

        this.request.setAttribute(AuthzIdentityKeys.AUTHZ_IDENTITY_KEYS, this.keys);

        when(this.info.getRoles()).thenReturn(this.reqRoles);
    }


    @Test
    public void testIsAuthorized() {
        // arrange
        this.reqRoles.add(Permissions.ATTEMPTS_CLEAR);
        this.hasRoles.add(Permissions.ATTEMPTS_CLEAR);
        when(this.repo.countMatchingPermissionsForUser(this.bannerId,
                                                       Arrays.asList(Permissions.ATTEMPTS_CLEAR,
                                                                     Permissions.SYSTEM))).thenReturn(1);

        // act
        boolean result = this.strategy.isAuthorized(this.request, this.info);

        // assert
        assertTrue(result);
    }

    @Test
    public void testIsNotAuthorized() {
        // arrange
        this.reqRoles.add(Permissions.ATTEMPTS_CLEAR);
        this.hasRoles.add(Permissions.ATTEMPTS_CLEAR);
        when(this.repo.countMatchingPermissionsForUser(this.bannerId,
                                                       Arrays.asList(Permissions.ATTEMPTS_CLEAR,
                                                                     Permissions.SYSTEM))).thenReturn(0);

        // act
        boolean result = this.strategy.isAuthorized(this.request, this.info);

        // assert
        assertFalse(result);
    }

    @Test
    public void testIsNotEmployee() {
        // arrange
        this.reqRoles.add(Permissions.ALL_CLEAR);
        this.hasRoles.add(Permissions.ATTEMPTS_CLEAR);
        this.keys.setRoles(Collections.emptySet());

        // act
        boolean result = this.strategy.isAuthorized(this.request, this.info);

        // assert
        assertFalse(result);
    }

    @Test
    public void testIsStudent() {
        // arrange
        this.reqRoles.add(Permissions.ALL_CLEAR);
        this.hasRoles.add(Permissions.ATTEMPTS_CLEAR);
        Set<String> student = new HashSet<>();
        student.add("student");
        this.keys.setRoles(student);

        // act
        boolean result = this.strategy.isAuthorized(this.request, this.info);

        // assert
        assertFalse(result);
    }

    @Test
    public void testIsAuthorizedNoUser() {
        // act
        boolean result = this.strategy.isAuthorized(this.request, this.info);

        // assert
        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAuthorizedNoUserId() {
        // arrange
        this.keys.setBannerId(null);

        // act
        this.strategy.isAuthorized(this.request, this.info);
    }

    @Test
    public void testIsAuthorizedNoKeys() {
        // arrange
        this.request.removeAttribute(AuthzIdentityKeys.AUTHZ_IDENTITY_KEYS);

        this.reqRoles.add(Permissions.ATTEMPTS_CLEAR);
        this.hasRoles.add(Permissions.ATTEMPTS_CLEAR);

        // act
        boolean result = this.strategy.isAuthorized(this.request, this.info);

        // assert
        assertFalse(result);
    }

    @Test
    public void testIsAuthorizedNoPermissions() {
        // arrange
        this.request.removeAttribute(AuthzIdentityKeys.AUTHZ_IDENTITY_KEYS);
        this.reqRoles.add(Permissions.ATTEMPTS_CLEAR);

        // act
        boolean result = this.strategy.isAuthorized(this.request, this.info);

        // assert
        assertFalse(result);
    }
}
