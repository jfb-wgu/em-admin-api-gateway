package edu.wgu.dmadmin.domain.security;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import edu.wgu.common.domain.AuthzIdentityKeys;
import edu.wgu.dmadmin.config.ApplicationContextHolder;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.security.authz.domain.AuthorizationInfo;
import edu.wgu.security.authz.strategy.AuthorizationStrategy;

public class SecureByPermissionStrategy implements AuthorizationStrategy {

	@Override
	public boolean isAuthorized(final HttpServletRequest request, final AuthorizationInfo authorizationInformation) {
		CassandraRepo cassandraRepo = ApplicationContextHolder.getContext().getBean(CassandraRepo.class);

		String userId = null;
		UserModel user = null;
		
		Optional<AuthzIdentityKeys> identityKeys = AuthzIdentityKeys.retrieveFrom(request);
		
		if (identityKeys.isPresent()) {
			userId = identityKeys.get().getBannerId();
		}
		
		if (userId != null) {
			user = cassandraRepo.getPermissionsForUser(userId);
		}

		if (user == null) return false;

		Set<String> userPermissions = user.getPermissions();

		Set<String> validRoles = authorizationInformation.getRoles().stream()
				.filter(role -> userPermissions.contains(role)).collect(Collectors.toSet());
		return validRoles != null && !validRoles.isEmpty();
	}
}
