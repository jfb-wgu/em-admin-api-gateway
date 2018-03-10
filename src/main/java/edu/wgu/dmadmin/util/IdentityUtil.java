package edu.wgu.dmadmin.util;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.wgu.common.domain.AuthzIdentityKeys;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;

@Component
public class IdentityUtil {
	public String getUserId() {
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
	
	public String getUserName() {
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
}
