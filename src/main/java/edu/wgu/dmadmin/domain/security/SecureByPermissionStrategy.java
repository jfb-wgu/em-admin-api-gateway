package edu.wgu.dmadmin.domain.security;

import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import edu.wgu.common.domain.AuthzIdentityKeys;
import edu.wgu.common.domain.Role;
import edu.wgu.dmadmin.config.ApplicationContextHolder;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.security.authz.domain.AuthorizationInfo;
import edu.wgu.security.authz.strategy.AuthorizationStrategy;

public class SecureByPermissionStrategy implements AuthorizationStrategy {

	@Override
	public boolean isAuthorized(final HttpServletRequest request, final AuthorizationInfo authorizationInformation) {
		Optional<AuthzIdentityKeys> identityKeys = AuthzIdentityKeys.retrieveFrom(request);

		if (identityKeys.isPresent()) {
			AuthzIdentityKeys keys = identityKeys.get();

			if (keys.getRoles() == null || !keys.getRoles().contains(Role.EMPLOYEE))
				return false;

			String userId = keys.getBannerId();

			if (userId != null) {
				CassandraRepo cassandraRepo = ApplicationContextHolder.getContext().getBean(CassandraRepo.class);
				RequestBean requestBean = ApplicationContextHolder.getContext().getBean(RequestBean.class);

				Optional<UserModel> user = cassandraRepo.getUserModel(userId);

				if (user.isPresent()) {
					cassandraRepo.updateLastLogin(userId);
					requestBean.setUser(user.get());
					Set<String> userPermissions = user.get().getPermissions();
					requestBean.setAuthRoles(authorizationInformation.getRoles());
					return authorizationInformation.getRoles().stream().filter(role -> userPermissions.contains(role))
							.count() > 0;
				}
			}
		}

		return false;
	}
}
