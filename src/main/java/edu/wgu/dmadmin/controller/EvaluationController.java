package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorConfirmationResponse;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorDashboardResponse;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorWorkspaceResponse;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.domain.submission.Referral;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.EvaluatorNotQualifiedException;
import edu.wgu.dmadmin.exception.IncompleteScoreReportException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.exception.WorkingEvaluationException;
import edu.wgu.dmadmin.service.EvaluatorService;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;
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

import java.util.List;
import java.util.UUID;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/evaluator")
public class EvaluationController {

    @Autowired
    private EvaluatorService evaluatorService;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.TASK_QUEUE)
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ResponseEntity<EvaluatorDashboardResponse> getEvaluatorDashboard()
            throws UserIdNotFoundException {
        String evaluatorId = this.iUtil.getUserId();
        EvaluatorDashboardResponse result = new EvaluatorDashboardResponse(this.evaluatorService.getEvaluatorDashboard(evaluatorId));
        return new ResponseEntity<EvaluatorDashboardResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole({Permissions.EVALUATION_VIEW, Permissions.EVALUATION_CLAIM})
    @RequestMapping(value = {"/submissions/{submissionId}/workspace", "/workspace/{submissionId}"}, method = RequestMethod.GET)
    public ResponseEntity<EvaluatorWorkspaceResponse> getEvaluatorWorkspace(@PathVariable final UUID submissionId) {
        EvaluatorWorkspaceResponse result = new EvaluatorWorkspaceResponse(this.evaluatorService.getEvaluatorWorkspace(submissionId));
        return new ResponseEntity<EvaluatorWorkspaceResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole({Permissions.EVALUATION_VIEW, Permissions.EVALUATION_CLAIM})
    @RequestMapping(value = {"/submissions/{submissionId}/review", "/review/{submissionId}"}, method = RequestMethod.GET)
    public ResponseEntity<EvaluatorConfirmationResponse> getEvaluatorConfirmation(@PathVariable final UUID submissionId) {
        EvaluatorConfirmationResponse result = new EvaluatorConfirmationResponse(this.evaluatorService.getEvaluatorConfirmation(submissionId));
        return new ResponseEntity<EvaluatorConfirmationResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_CLAIM)
    @RequestMapping(value = {"/submissions/{submissionId}/claim", "/claim/{submissionId}"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void claimSubmission(@PathVariable final UUID submissionId)
            throws EvaluatorNotQualifiedException, UserIdNotFoundException,
            WorkingEvaluationException, EvaluationNotFoundException, SubmissionStatusException {
        String evaluatorId = this.iUtil.getUserId();
        this.evaluatorService.claimSubmission(evaluatorId, submissionId);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_CLAIM)
    @RequestMapping(value = {"/submissions/{submissionId}/cancel", "/cancel/{submissionId}"}, method = RequestMethod.POST)
    public ResponseEntity<String> cancelEvaluation(@PathVariable final UUID submissionId, @RequestBody final String comment)
            throws UserIdNotFoundException, EvaluationStatusException {
        String evaluatorId = this.iUtil.getUserId();
        return new ResponseEntity<String>(this.evaluatorService.cancelEvaluation(evaluatorId, submissionId, comment), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_RELEASE)
    @RequestMapping(value = {"/submissions/{submissionId}/release/{retryAllowed}", "/release/{submissionId}/retry/{retryAllowed}"}, method = RequestMethod.POST)
    public ResponseEntity<String> releaseScoreReport(@PathVariable final UUID submissionId,
                                                     @PathVariable final boolean retryAllowed, @RequestBody final Comment comment)
            throws IncompleteScoreReportException, UserIdNotFoundException, EvaluationStatusException {
        String evaluatorId = this.iUtil.getUserId();
        return new ResponseEntity<String>(this.evaluatorService.releaseEvaluation(evaluatorId, submissionId, retryAllowed, comment), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_CLAIM)
    @RequestMapping(value = {"/submissions/{submissionId}/score/{aspectName}/{score}", "/score/{submissionId}/aspect/{aspectName}/score/{score}"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void saveAspectScore(@PathVariable final UUID submissionId, @PathVariable final String aspectName, @PathVariable final int score)
            throws UserIdNotFoundException, WorkingEvaluationException {
        String evaluatorId = this.iUtil.getUserId();
        this.evaluatorService.saveAspectScore(evaluatorId, submissionId, aspectName, score);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_CLAIM)
    @RequestMapping(value = {"/submissions/{submissionId}/comment/{aspectName}", "/comment/{submissionId}/aspect/{aspectName}"}, method = RequestMethod.POST)
    public ResponseEntity<Comment> saveAspectComment(@PathVariable final UUID submissionId, @PathVariable final String aspectName,
                                                     @RequestBody Comment comment)
            throws UserIdNotFoundException, WorkingEvaluationException {
        String evaluatorId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.evaluatorService.saveAspectComment(evaluatorId, submissionId, aspectName, comment));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_CLAIM)
    @RequestMapping(value = {"/submissions/{submissionId}/comment", "/comment/{submissionId}"}, method = RequestMethod.POST)
    public ResponseEntity<Comment> saveReportComment(@PathVariable final UUID submissionId, @RequestBody Comment comment)
            throws UserIdNotFoundException, WorkingEvaluationException {
        String evaluatorId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.evaluatorService.saveReportComment(evaluatorId, submissionId, comment));
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.EVALUATION_CLAIM)
    @RequestMapping(value = {"/submissions/{submissionId}/refer", "/refer/{submissionId}"}, method = RequestMethod.POST)
    public ResponseEntity<List<Referral>> saveReferral(@PathVariable final UUID submissionId, @RequestBody Referral referral)
            throws UserIdNotFoundException, SubmissionStatusException {
        String evaluatorId = this.iUtil.getUserId();
        return ResponseEntity.ok(this.evaluatorService.saveReferral(evaluatorId, submissionId, referral));
    }
}
