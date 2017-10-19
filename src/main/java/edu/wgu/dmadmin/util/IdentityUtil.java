package edu.wgu.dmadmin.util;

import edu.wgu.common.domain.AuthzIdentityKeys;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.Set;

@Component
public class IdentityUtil {
	public String getUserId() throws UserIdNotFoundException {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		String userId = null;
		
		Optional<AuthzIdentityKeys> identityKeys = AuthzIdentityKeys.retrieveFrom(attributes.getRequest());
		if (identityKeys.isPresent()) {
			userId = identityKeys.get().getBannerId();
		} else {
			throw new UserIdNotFoundException("Could not get ID from request.");
		}
		
		return userId;
	}
	
	public String getUserName() throws UserIdNotFoundException {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		String userId = null;
		
		Optional<AuthzIdentityKeys> identityKeys = AuthzIdentityKeys.retrieveFrom(attributes.getRequest());
		if (identityKeys.isPresent()) {
			userId = identityKeys.get().getUsername();
		} else {
			throw new UserIdNotFoundException("Could not get username from request.");
		}
		
		return userId;
	}
	
	public Set<String> getUserRoles() throws UserIdNotFoundException {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		Set<String> roles = null;
		
		Optional<AuthzIdentityKeys> identityKeys = AuthzIdentityKeys.retrieveFrom(attributes.getRequest());
		if (identityKeys.isPresent()) {
			roles = identityKeys.get().getRoles();
		} else {
			throw new UserIdNotFoundException("Could not get roles from request.");
		}
		
		return roles;
	}

	public Long getUserPidm() throws UserIdNotFoundException {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		Long pidm = null;

		Optional<AuthzIdentityKeys> identityKeys = AuthzIdentityKeys.retrieveFrom(attributes.getRequest());
		if (identityKeys.isPresent()) {
			pidm = identityKeys.get().getPidm();
		} else {
			throw new UserIdNotFoundException("Could not get pidm from request.");
		}

		return pidm;
	}
}
