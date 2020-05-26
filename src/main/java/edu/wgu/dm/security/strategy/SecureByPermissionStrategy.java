package edu.wgu.dm.security.strategy;

import edu.wgu.boot.auth.Role;
import edu.wgu.boot.auth.authz.domain.AuthorizationInfo;
import edu.wgu.boot.auth.authz.domain.AuthzIdentityKeys;
import edu.wgu.boot.auth.authz.strategy.AuthorizationStrategy;
import edu.wgu.dm.repository.SecurityRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecureByPermissionStrategy implements AuthorizationStrategy {

    private final SecurityRepo repo;

    @Override
    public boolean isAuthorized(final HttpServletRequest request, final AuthorizationInfo authorizationInformation) {

        Optional<AuthzIdentityKeys> identityKeys = AuthzIdentityKeys.retrieveFrom(request);
        if (!identityKeys.isPresent()) {
            log.error("AuthzIdentityKeys missing from the request.");
            return false;
        }
        AuthzIdentityKeys keys = identityKeys.get();
        if (keys.getRoles() == null || !keys.getRoles()
                                            .contains(Role.EMPLOYEE)) {
            return false;
        }
        String userId = keys.getBannerId();
        if (StringUtils.isBlank(userId)) {
            throw new RuntimeException("UserId Not Found  "+userId);
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
