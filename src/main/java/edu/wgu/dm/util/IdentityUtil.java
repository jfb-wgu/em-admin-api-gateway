package edu.wgu.dm.util;

import edu.wgu.boot.auth.authz.domain.AuthzIdentityKeys;
import edu.wgu.dm.exception.UserIdNotFoundException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentityUtil {

    @Autowired
    private HttpServletRequest request;

    public String getUserId() {
        return getWguIdentity().getBannerId();
    }

    public AuthzIdentityKeys getWguIdentity() {
        return AuthzIdentityKeys.retrieveFrom(this.request)
                                .orElseThrow(() -> new UserIdNotFoundException("Could not get user identity."));

    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
