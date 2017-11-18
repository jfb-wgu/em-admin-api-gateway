package edu.wgu.dmadmin.domain.security;

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
			throw new UserIdNotFoundException();
		}
		
		return userId;
	}
}
