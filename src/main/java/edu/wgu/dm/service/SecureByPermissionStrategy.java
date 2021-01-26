package edu.wgu.dm.service;

import edu.wgu.boot.auth.Role;
import edu.wgu.boot.auth.authz.domain.AuthorizationInfo;
import edu.wgu.boot.auth.authz.domain.AuthzIdentityKeys;
import edu.wgu.boot.auth.authz.strategy.AuthorizationStrategy;
import edu.wgu.dm.repository.SecurityRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecureByPermissionStrategy implements AuthorizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(SecureByPermissionStrategy.class);
    private final SecurityRepo repo;

    public SecureByPermissionStrategy(SecurityRepo repo) {
        this.repo = repo;
    }

    @Override
    public boolean isAuthorized(final HttpServletRequest request, final AuthorizationInfo authorizationInformation) {

        AuthzIdentityKeys identityKeys = AuthzIdentityKeys.retrieveFrom(request)
                                                          .orElse(null);
        if (identityKeys == null || identityKeys.getRoles() == null || !identityKeys.getRoles()
                                                                                    .contains(Role.EMPLOYEE)) {
            log.error("AuthzIdentityKeys is missing or is Invalid in the request.");
            return false;
        }
        String userId = identityKeys.getBannerId();
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("UserId Not Found  " + userId);
        }
        List<String> allowedPermissions = new ArrayList<>(authorizationInformation.getRoles());
        allowedPermissions.add("SYSTEM"); // SYSTEM can access all functions.
        int matchedPermissions = this.repo.countMatchingPermissionsForUser(userId, allowedPermissions);
        if (matchedPermissions > 0) {
            this.repo.updateLastLogin(userId);
            return true;
        }
        return false;
    }
}
