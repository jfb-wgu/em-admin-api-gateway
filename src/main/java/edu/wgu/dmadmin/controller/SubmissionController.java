package edu.wgu.dmadmin.controller;

import edu.wgu.common.domain.Role;
import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.service.StudentWorkService;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.IgnoreAuthorization;
import edu.wgu.security.authz.annotation.Secured;
import edu.wgu.security.authz.strategy.SecureByRolesStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class SubmissionController {

    @Autowired
    StudentWorkService studentWorkService;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/submission/task/{taskId}", method = RequestMethod.POST)
    public ResponseEntity<Submission> beginSubmission(@PathVariable final UUID taskId) throws UserIdNotFoundException, SubmissionStatusException {
        String bannerId = this.iUtil.getUserId();
        Long pidm = this.iUtil.getUserPidm();
        return ResponseEntity.ok(this.studentWorkService.beginSubmission(bannerId, taskId, pidm));
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/submission/{submissionId}/comment", method = RequestMethod.POST)
    public ResponseEntity<Submission> addComments(@PathVariable final UUID submissionId, @RequestBody(required = false) String comments)
            throws UserIdNotFoundException, SubmissionStatusException {
        String bannerId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.studentWorkService.addComments(bannerId, submissionId, comments));
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/submission/{submissionId}", method = RequestMethod.POST)
    public ResponseEntity<Submission> saveSubmission(@PathVariable final UUID submissionId, @RequestBody Submission submission)
            throws SubmissionStatusException, UserIdNotFoundException {
        String bannerId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.studentWorkService.saveSubmission(bannerId, submissionId, submission));
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/submission/{submissionId}/cancel", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Submission> cancelSubmission(@PathVariable final UUID submissionId)
            throws SubmissionStatusException, UserIdNotFoundException {
        String bannerId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.studentWorkService.cancelSubmission(bannerId, submissionId));
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = {"/submission/{submissionId}/queue", "queue/{submissionId}"}, method = RequestMethod.POST)
    public ResponseEntity<Submission> submitForEvaluation(@PathVariable final UUID submissionId, @RequestBody(required = false) String comments)
            throws SubmissionStatusException, UserIdNotFoundException {
        String bannerId = this.iUtil.getUserId();
        Submission submission = this.studentWorkService.submitForEvaluation(bannerId, submissionId, comments);
        return ResponseEntity.ok(submission);
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/submissions", method = RequestMethod.GET)
    public ResponseEntity<List<Submission>> getSubmissions() throws UserIdNotFoundException {
        String bannerId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.studentWorkService.getSubmissions(bannerId));
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/submissions/{submissionId}", method = RequestMethod.GET)
    public ResponseEntity<Submission> getSubmission(@PathVariable final UUID submissionId)
            throws UserIdNotFoundException {
        String bannerId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.studentWorkService.getSubmission(bannerId, submissionId));
    }

    @Deprecated
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.SERVICE)
    @RequestMapping(value = "/student/{studentId}/submission/{submissionId}/attachments", method = RequestMethod.POST)
    public ResponseEntity<?> addAttachment(@PathVariable final String studentId, @PathVariable final UUID submissionId, @RequestBody AttachmentModel attachment)
            throws SubmissionStatusException {
        this.studentWorkService.addAttachment(studentId, submissionId, attachment);
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.SERVICE)
    @RequestMapping(value = "/student/{studentId}/submission/{submissionId}/attachments/{title}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeAttachment(@PathVariable final String studentId, @PathVariable final UUID submissionId, @PathVariable final String title)
            throws SubmissionStatusException {
        this.studentWorkService.removeAttachment(studentId, submissionId, title);
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.SERVICE)
    @RequestMapping(value = "/student/{studentId}/submission/{submissionId}/attachments", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeAttachments(@PathVariable final String studentId, @PathVariable final UUID submissionId) throws SubmissionStatusException {
        this.studentWorkService.removeAttachments(studentId, submissionId);
        return ResponseEntity.ok().build();
    }

    public void setStudentWorkService(StudentWorkService service) {
        this.studentWorkService = service;
    }

    public void setIdentityUtil(IdentityUtil utility) {
        this.iUtil = utility;
    }
}
