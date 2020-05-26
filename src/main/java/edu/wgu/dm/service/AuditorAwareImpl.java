package edu.wgu.dm.service;

import edu.wgu.dm.util.IdentityUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * implementation of {@link AuditorAware}. Normally you would access the applications security subsystem to return the current user.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final IdentityUtil iUtil;

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
     * Make sure request is bound to thread and authorization param exist Unit test could not mock static class hence the method to return the boolean
     *
     * @return true if the request is bound to current thread
     */
    public boolean checkIfHttpServletRequestBoundToThread() {
        return RequestContextHolder.getRequestAttributes() != null && this.iUtil.getRequest() != null
                   && this.iUtil.getRequest()
                                .getHeader("authorization") != null;
    }
}
