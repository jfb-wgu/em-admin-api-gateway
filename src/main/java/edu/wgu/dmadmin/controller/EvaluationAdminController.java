package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorConfirmationResponse;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorWorkspaceResponse;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.EvaluatorNotQualifiedException;
import edu.wgu.dmadmin.exception.IncompleteScoreReportException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.service.EvaluationAdminService;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/admin")
public class EvaluationAdminController {

    @Autowired
    private EvaluationAdminService adminService;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_ASSIGN)
    @RequestMapping(value = "/submissions/{submissionId}/assign/{evaluatorId}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<EvaluatorWorkspaceResponse> assignEvaluator(@PathVariable final UUID submissionId, @PathVariable final String evaluatorId, @RequestBody Comment comments)
            throws EvaluatorNotQualifiedException, SubmissionStatusException, UserIdNotFoundException {
        EvaluatorWorkspaceResponse result = new EvaluatorWorkspaceResponse(this.adminService.assignEvaluation(this.iUtil.getUserId(), evaluatorId, submissionId, comments));
        return new ResponseEntity<EvaluatorWorkspaceResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_ASSIGN)
    @RequestMapping(value = "/submissions/{submissionId}/cancel", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void cancelEvaluation(@PathVariable final UUID submissionId, @RequestBody Comment comments)
            throws UserIdNotFoundException, EvaluationStatusException, SubmissionStatusException {
        this.adminService.cancelEvaluation(this.iUtil.getUserId(), submissionId, comments);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.MASTER_RELEASE)
    @RequestMapping(value = "/submissions/{submissionId}/release/{retryAllowed}", method = RequestMethod.POST)
    public ResponseEntity<String> releaseEvaluation(@PathVariable final UUID submissionId, @PathVariable final boolean retryAllowed, @RequestBody Comment comments)
            throws UserIdNotFoundException, EvaluationStatusException, IncompleteScoreReportException, SubmissionStatusException {
        return ResponseEntity.ok(this.adminService.releaseEvaluation(this.iUtil.getUserId(), submissionId, retryAllowed, comments));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_MODIFY)
    @RequestMapping(value = "/submissions/{submissionId}/createReview", method = RequestMethod.POST)
    @ApiOperation(value = "This endpoint allows admins with the Evaluation Modify permission to review and modify an evaluation. A new evaluation will be created by copying the one under review.")
    public UUID createEvaluationForReview(@PathVariable final UUID submissionId)
            throws UserIdNotFoundException, SubmissionStatusException, EvaluationStatusException {
        return this.adminService.reviewEvaluation(this.iUtil.getUserId(), submissionId);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_MODIFY)
    @RequestMapping(value = "/submissions/{submissionId}/releaseReview/{retryAllowed}", method = RequestMethod.POST)
    @ApiOperation(value = "This endpoint allows admins with the Evaluation Modify permission to relsease an Evaluation Review to the student.")
    public void relseaseReviewEvaluation(@PathVariable final UUID submissionId, @PathVariable final boolean retryAllowed, @RequestBody Comment comments)
            throws UserIdNotFoundException, EvaluationStatusException, IncompleteScoreReportException {
        this.adminService.releaseReviewEvaluation(this.iUtil.getUserId(), submissionId, retryAllowed, comments);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_MODIFY)
    @RequestMapping(value = "/submissions/{submissionId}/workspace", method = RequestMethod.GET)
    public ResponseEntity<EvaluatorWorkspaceResponse> getEvaluatorWorkspace(@PathVariable final UUID submissionId)
            throws UserIdNotFoundException {
        EvaluatorWorkspaceResponse result = new EvaluatorWorkspaceResponse(this.adminService.getReviewWorkspace(this.iUtil.getUserId(), submissionId));
        return new ResponseEntity<EvaluatorWorkspaceResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_MODIFY)
    @RequestMapping(value = "/submissions/{submissionId}/review", method = RequestMethod.GET)
    public ResponseEntity<EvaluatorConfirmationResponse> getEvaluatorConfirmation(@PathVariable final UUID submissionId)
            throws UserIdNotFoundException {
        EvaluatorConfirmationResponse result = new EvaluatorConfirmationResponse(this.adminService.getReviewConfirmation(this.iUtil.getUserId(), submissionId));
        return new ResponseEntity<EvaluatorConfirmationResponse>(result, HttpStatus.OK);
    }

    void setEvaluationAdminService(EvaluationAdminService service) {
        this.adminService = service;
    }

    void setIdentityUtil(IdentityUtil utility) {
        this.iUtil = utility;
    }
}
