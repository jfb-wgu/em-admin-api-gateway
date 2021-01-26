package edu.wgu.dm.service;

import edu.wgu.dm.util.IdentityUtil;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * implementation of {@link AuditorAware}. Normally you would access the applications security subsystem to return the
 * current user.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final Logger log = LoggerFactory.getLogger(AuditorAwareImpl.class);
    private final IdentityUtil iUtil;

    public AuditorAwareImpl(IdentityUtil iUtil) {
        this.iUtil = iUtil;
    }

    public Optional<String> getCurrentAuditor() {
        String bannerId = "NA";
        try {
            if (checkIfHttpServletRequestBoundToThread()) {
                bannerId = this.iUtil.getUserId();
            }
        } catch (Exception e) {
            log.error("Error reading banner id from the request:", e);
        }
        log.debug("User requesting changes: {}", bannerId);
        return Optional.of(bannerId);
    }

    /**
     * Make sure request is bound to thread and authorization param exist Unit test could not mock static class hence
     * the method to return the boolean
     *
     * @return true if the request is bound to current thread
     */
    public boolean checkIfHttpServletRequestBoundToThread() {
        return RequestContextHolder.getRequestAttributes() != null && this.iUtil.getRequest() != null
                   && this.iUtil.getRequest()
                                .getHeader("authorization") != null;
    }
}
