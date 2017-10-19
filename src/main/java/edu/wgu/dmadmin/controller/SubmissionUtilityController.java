package edu.wgu.dmadmin.controller;

import edu.wgu.common.domain.Role;
import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.service.EmailService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.IgnoreAuthorization;
import edu.wgu.security.authz.annotation.Secured;
import edu.wgu.security.authz.strategy.SecureByRolesStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by john.buchanan on 5/9/17.
 */
@Component
@RestController
@RequestMapping("v1")
public class SubmissionUtilityController {

    @Autowired
    SubmissionUtilityService submissionUtilityService;

    @Autowired
    EmailService emailService;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/submissions/{submissionID}/comments", method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> getComments(@PathVariable final UUID submissionID) {
        return ResponseEntity.ok(this.submissionUtilityService.getInternalComments(submissionID));
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/submissions/{submissionID}/comments", method = RequestMethod.POST)
    public ResponseEntity<List<Comment>> setComments(@PathVariable final UUID submissionID, @RequestBody List<Comment> comments)
            throws UserIdNotFoundException {
        String bannerId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.submissionUtilityService.updateInternalComments(bannerId, submissionID, comments));
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/submissions/{submissionID}/comment", method = RequestMethod.POST)
    public ResponseEntity<List<Comment>> setComment(@PathVariable final UUID submissionID, @RequestBody Comment comment)
            throws UserIdNotFoundException {
        String bannerId = this.iUtil.getUserId();
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        return ResponseEntity.ok(this.submissionUtilityService.updateInternalComments(bannerId, submissionID, comments));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole({Permissions.ALL_CLEAR, Permissions.LEAD_CLEAR, Permissions.ARTICULATION_CLEAR, Permissions.ORIGINALITY_CLEAR, Permissions.ATTEMPTS_CLEAR, Permissions.OPEN_CLEAR})
    @RequestMapping(value = "/submissions/{submissionId}/clear", method = RequestMethod.POST)
    public ResponseEntity<String> clearSubmissionHold(@PathVariable final UUID submissionId, @RequestBody Comment comment)
            throws SubmissionStatusException, UserIdNotFoundException {
        String bannerId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.submissionUtilityService.clearSubmissionHold(bannerId, submissionId, comment));
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/submissions/sendTestEmail", method = RequestMethod.GET)
    public void sendTestEmail() {
        this.emailService.sendTestEmail();
    }
}
