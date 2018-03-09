package edu.wgu.dmadmin.domain.security;

import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import edu.wgu.common.domain.AuthzIdentityKeys;
import edu.wgu.dmadmin.config.ApplicationContextHolder;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.security.authz.domain.AuthorizationInfo;
import edu.wgu.security.authz.strategy.AuthorizationStrategy;

public class SecureByPermissionStrategy implements AuthorizationStrategy {
	
	@Override
	public boolean isAuthorized(final HttpServletRequest request, final AuthorizationInfo authorizationInformation) {
		CassandraRepo cassandraRepo = ApplicationContextHolder.getContext().getBean(CassandraRepo.class);
		RequestBean requestBean = ApplicationContextHolder.getContext().getBean(RequestBean.class);

		String userId = null;
		Optional<UserByIdModel> user = null;
		Optional<AuthzIdentityKeys> identityKeys = AuthzIdentityKeys.retrieveFrom(request);

		if (identityKeys.isPresent()) {
			userId = identityKeys.get().getBannerId();
		}

		if (userId != null) {
			user = cassandraRepo.getUserModel(userId);
			
			if (user.isPresent()) {
				cassandraRepo.updateLastLogin(userId);
				requestBean.setUser(user.get());
				Set<String> userPermissions = user.get().getPermissions();
				requestBean.setAuthRoles(authorizationInformation.getRoles());
				return authorizationInformation.getRoles().stream()
						.filter(role -> userPermissions.contains(role))
						.count() > 0;
			}
		}
		return false;
	}
}
