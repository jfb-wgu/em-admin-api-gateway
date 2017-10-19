package edu.wgu.dmadmin.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorConfirmation;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorWorkspace;
import edu.wgu.dmadmin.domain.submission.WorkspaceSubmission;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.EvaluatorNotQualifiedException;
import edu.wgu.dmadmin.exception.IncompleteScoreReportException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByEvaluatorModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

@Service
public class EvaluationAdminService {

    CassandraRepo cassandraRepo;
    SubmissionUtilityService submissionUtility;
    EvaluatorService evaluatorService;

    @Autowired
    public void setCassandraRepo(CassandraRepo repo) {
        this.cassandraRepo = repo;
    }

    @Autowired
    public void setSubmissionUtility(SubmissionUtilityService service) {
        this.submissionUtility = service;
    }
    
    @Autowired
    public void setEvaluatorService(EvaluatorService service) {
        this.evaluatorService = service;
    }

    /**
     * Assign a submission to an evaluator.
     * 
     * Check all evaluations that have been opened for this submission and collect information:
     *   - is there a currently working evaluation that will have to be cancelled?
     *   - is there a cancelled evaluation for the specified evaluator to resume?
     *   - which evaluation record has the most recent data?
     * 
     * The new or resumed evaluation then imports either the latest evaluation for this submission or 
     * the completed evaluation for the previous attempt.  Save the new or resumed evaluation and 
     * update the submission record with the now current evaluator information.
     *
     * @param evaluatorId  String identifier for the evaluator
     * @param submissionId UUID identifier for the submission
     * @param userId  String identifier for the admin performing the action
     * @param comment  Comment object explaining the action
     * 
     * @throws EvaluatorNotQualifiedException
     */
    public EvaluatorWorkspace assignEvaluation(String userId, String evaluatorId, UUID submissionId, Comment comment)
            throws EvaluatorNotQualifiedException, SubmissionStatusException {

		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		if (StatusUtil.isEvaluated(submission.getStatus())) throw new SubmissionStatusException("Cannot assign a completed submission.");
		String oldStatus = submission.getStatus();

		// if this submission is already assigned to the indicated evaluator we're done.
		if (evaluatorId.equals(submission.getEvaluatorId())) return evaluatorService.getEvaluatorWorkspace(submissionId);

		// get the UserModels
		List<UserByIdModel> users = cassandraRepo.getUsersById(Arrays.asList(userId, evaluatorId));
		UserModel evaluator = users.stream().filter(u -> u.getUserId().equals(evaluatorId)).findFirst().orElseThrow(() -> new UserNotFoundException(evaluatorId));
		UserModel admin = users.stream().filter(u -> u.getUserId().equals(userId)).findFirst().orElseThrow(() -> new UserNotFoundException(userId));
		
		if (!evaluator.getTasks().contains(submission.getTaskId())) throw new EvaluatorNotQualifiedException(evaluatorId, submissionId);

		// figure out any existing evaluations so we know what to import
		EvaluationModel current = null;
		EvaluationModel byUser = null;
		EvaluationModel latest = null;

		List<EvaluationBySubmissionModel> evaluations = cassandraRepo.getEvaluationsBySubmission(submissionId);
		if (CollectionUtils.isNotEmpty(evaluations)) {
			current = evaluations.stream().filter(e -> e.getStatus().equals(StatusUtil.WORKING)).findFirst().orElse(null);
			byUser = evaluations.stream().filter(e -> e.getEvaluatorId().equals(evaluatorId)).findFirst().orElse(null);
			latest = evaluations.stream().max((e1, e2) -> e1.compareTo(e2)).orElseThrow(() -> new EvaluationNotFoundException(submissionId));
		}
		
		// if there is a current working submission, cancel it
		if (current != null) {
			current.complete(StatusUtil.CANCELLED);
			cassandraRepo.saveEvaluation(new EvaluationByIdModel(current));
		}

		// set up our new evaluation, resuming an old one if it exists
		EvaluationModel evaluation = null;
		if (byUser == null) {
			evaluation = new EvaluationModel(evaluator, submission);
		} else {
			evaluation = byUser;
			evaluation.setDateCompleted(null);
		}
		
		// if there are not any evaluations for this submission yet, either import the score report from the previous attempt or get it from the task rubric
		if (CollectionUtils.isEmpty(evaluations)) {
			if (submission.getPreviousEvaluationId() != null) {
				EvaluationModel previous = cassandraRepo.getEvaluationById(submission.getPreviousEvaluationId()).orElseThrow(() -> new EvaluationNotFoundException(submission.getPreviousEvaluationId()));
				evaluation.importScoreReport(previous.getScoreReport());
			} else {
				RubricModel rubric = cassandraRepo.getTaskRubric(submission.getTaskId()).orElseThrow(() -> new TaskNotFoundException(submission.getTaskId())).getRubric();
				evaluation.setScoreReport(new ScoreReportModel(rubric));
			}
		} else if (latest != null && !latest.equals(byUser)) {
			evaluation.assignScoreReport(latest, submission, evaluator);
		}
		
		evaluation.setStatus(StatusUtil.WORKING);
		submission.setEvaluation(StatusUtil.EVALUATION_TAKEN_OVER, evaluator, evaluation);
		CommentModel model = new CommentModel(admin.getUserId(), admin.getFirstName(), admin.getLastName(), comment.getComments(), submission.getAttempt(), -1, comment.getType());
		submission.getInternalCommentsNS().put(model.getCommentId(), model);
		cassandraRepo.saveSubmission(submission, new EvaluationByIdModel(evaluation), userId, oldStatus, true);

		return evaluatorService.getEvaluatorWorkspace(submissionId);
	}
	
	public String cancelEvaluation(String userId, UUID submissionId, Comment comments) 
			throws EvaluationStatusException, SubmissionStatusException {
		
		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId));
        if (submission.getEvaluationId() == null) throw new SubmissionStatusException("No working evaluation found.");
        
        return submissionUtility.cancelEvaluation(userId, submission, comments);
	}
	
	public String releaseEvaluation(String userId, UUID submissionId, boolean retryAllowed, Comment comments) 
			throws EvaluationStatusException, IncompleteScoreReportException, SubmissionStatusException {
		
		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		if (submission.getEvaluationId() != null) {
			return submissionUtility.releaseEvaluation(userId, submission, retryAllowed, comments);
		} else {
			throw new SubmissionStatusException("No working evaluation found.");
		}
    }

	public String releaseReviewEvaluation(String evaluatorId, UUID submissionId, boolean retryAllowed, Comment comments)
            throws EvaluationStatusException, IncompleteScoreReportException {

		EvaluationModel evaluation = getWorkingEvaluation(evaluatorId, submissionId);
		return submissionUtility.releaseReviewEvaluation(evaluatorId, new EvaluationByIdModel(evaluation), retryAllowed, comments);
    }

    public UUID reviewEvaluation(String userId, UUID submissionId)
            throws SubmissionStatusException, EvaluationStatusException {

    	SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId));
        if (submission.getEvaluationId() == null) throw new EvaluationStatusException("No current evaluation found.");
        String oldStatus = submission.getStatus();

        if (!(StatusUtil.canReview(submission.getStatus()))) {
            throw new SubmissionStatusException(submissionId, submission.getStatus(), Arrays.asList(StatusUtil.AUTHOR_WORK_EVALUATED, StatusUtil.AUTHOR_WORK_NEEDS_REVISION, StatusUtil.EVALUATION_RELEASED));
        }
        
        List<EvaluationByEvaluatorModel> evaluations = cassandraRepo.getEvaluationByEvaluaatorAndSubmission(userId, StatusUtil.WORKING, submissionId);
        if (evaluations.size() > 0) throw new EvaluationStatusException("This user already has a working evaluation.");
        
        EvaluationByIdModel evaluation = cassandraRepo.getEvaluationById(submission.getEvaluationId()).orElseThrow(() -> new EvaluationNotFoundException(submission.getEvaluationId()));

        if (!(evaluation.getStatus().equals(StatusUtil.COMPLETED))) {
            throw new EvaluationStatusException(evaluation.getEvaluationId(), evaluation.getStatus(), Arrays.asList(StatusUtil.COMPLETED));
        }
        
        submission.setStatus(StatusUtil.EVALUATION_EDITED);

        UserModel user = cassandraRepo.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));

        EvaluationByIdModel reviewEvaluation = new EvaluationByIdModel(evaluation);

        reviewEvaluation.assignScoreReport(evaluation, submission, user);
        reviewEvaluation.setEvaluationId(UUID.randomUUID());
        reviewEvaluation.setEvaluatorId(userId);
        reviewEvaluation.setEvaluatorFirstName(user.getFirstName());
        reviewEvaluation.setEvaluatorLastName(user.getLastName());
        reviewEvaluation.setStatus(StatusUtil.WORKING);
        reviewEvaluation.setDateStarted(DateUtil.getZonedNow());

        cassandraRepo.saveSubmission(submission, reviewEvaluation, userId, oldStatus, true);
        return reviewEvaluation.getEvaluationId();
    }
    
	/**
	 * Get the information required to populate the evaluator workspace screen.
	 * 
	 * @param evaluatorId
	 * @param submissionId
	 * @return EvaluatorWorkspace
	 */
	public EvaluatorWorkspace getReviewWorkspace(String evaluatorId, UUID submissionId) {

		SubmissionModel submission = cassandraRepo.getSubmissionById(submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		TaskModel task = cassandraRepo.getTaskBasics(submission.getTaskId()).orElseThrow(() -> new TaskNotFoundException(submission.getTaskId()));
		EvaluationModel evaluation = getWorkingEvaluation(evaluatorId, submissionId);

		EvaluatorWorkspace workspace = new EvaluatorWorkspace(submission, task, evaluation);

		List<WorkspaceSubmission> previous = cassandraRepo.getSubmissionByStudentByTask(submission.getStudentId(), submission.getTaskId()).stream()
				.filter(s -> !s.getSubmissionId().equals(submission.getSubmissionId()))
				.map(sub -> new WorkspaceSubmission(sub, evaluation)).collect(Collectors.toList());
		workspace.setPreviousSubmissions(previous);

		return workspace;
	}
	
	/**
	 * Get the information required to populate the evaluator confirmation screen.
	 * 
	 * @param submissionId
	 * @return EvaluatorConfirmation
	 */
	public EvaluatorConfirmation getReviewConfirmation(String evaluatorId, UUID submissionId) {
		
		SubmissionModel submission = cassandraRepo.getConfirmationSubmission(submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		RubricModel rubric = cassandraRepo.getTaskRubric(submission.getTaskId()).orElseThrow(() -> new TaskNotFoundException(submission.getTaskId())).getRubric();
		EvaluationModel evaluation = getWorkingEvaluation(evaluatorId, submissionId);
		
		EvaluatorConfirmation confirmation = new EvaluatorConfirmation(submission, rubric, evaluation);
		return confirmation;		
	}
    
    protected EvaluationModel getWorkingEvaluation(String evaluatorId, UUID submissionId) {
        List<EvaluationByEvaluatorModel> evaluations = cassandraRepo.getEvaluationByEvaluaatorAndSubmission(evaluatorId, StatusUtil.WORKING, submissionId);
        if (evaluations.size() == 1) {
        	return evaluations.get(0);
        } else {
        	throw new EvaluationNotFoundException(evaluatorId, submissionId);
        }
    }
}
