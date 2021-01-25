package edu.wgu.dm.util;

import com.nimbusds.jwt.SignedJWT;
import edu.wgu.boot.auth.authz.domain.AuthzIdentityKeys;
import edu.wgu.dm.exception.UserIdNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IdentityUtil {

    @Autowired
    @Getter
    private HttpServletRequest request;

    public List<String> getUserFirstAndLastName() {
        try {
            Map<String, Object> json = extractJwtToken();
            String firstName = (String) json.get("givenName");
            String lastName = (String) json.get("sn");
            return Arrays.asList(firstName, lastName);
        } catch (Exception e) {
            log.error("Error in IdentityUtil.getUserFirstAndLastName()", e);
            return new ArrayList<>();
        }
    }

    public String getUserId() {
        return getWguIdentity().getBannerId();
    }

    public String getUserName() {
        return getWguIdentity().getUsername();
    }

    public Set<String> getUserRoles() {
        return getWguIdentity().getRoles();
    }

    public Long getUserPidm() {
        return getWguIdentity().getPidm();
    }

    public boolean isStudent() {
        boolean isStudent = false;
        AuthzIdentityKeys identity = getWguIdentity();
        isStudent = identity.getRoles()
                            .stream()
                            .anyMatch("Student"::equalsIgnoreCase);
        return isStudent;
    }

    public boolean isEmployee() {
        boolean isEmployee = false;
        AuthzIdentityKeys identity = getWguIdentity();
        isEmployee = identity.getRoles()
                             .stream()
                             .anyMatch("Employee"::equalsIgnoreCase);
        return isEmployee;
    }

    public AuthzIdentityKeys getWguIdentity() {
        return AuthzIdentityKeys.retrieveFrom(this.request)
                                .orElseThrow(() -> new UserIdNotFoundException("Could not get user identity."));

    }

    private Map<String, Object> extractJwtToken() throws ParseException {
        String auth = this.request.getHeader("authorization");
        String jwtToken = auth.substring(6);
        return SignedJWT.parse(jwtToken)
                        .getPayload()
                        .toJSONObject();
    }
}
