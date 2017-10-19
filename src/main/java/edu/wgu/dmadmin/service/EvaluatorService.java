package edu.wgu.dmadmin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.common.exception.AuthorizationException;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorConfirmation;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorDashboard;
import edu.wgu.dmadmin.domain.evaluator.EvaluatorWorkspace;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.domain.submission.Referral;
import edu.wgu.dmadmin.domain.submission.WorkspaceSubmission;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.EvaluatorNotQualifiedException;
import edu.wgu.dmadmin.exception.IncompleteScoreReportException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.exception.WorkingEvaluationException;
import edu.wgu.dmadmin.factory.SubmissionLockFactory;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByEvaluatorModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.ReferralModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionLockModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

@Service
public class EvaluatorService {

	private CassandraRepo cassandraRepo;
	SubmissionUtilityService submissionUtility;
	private SubmissionLockFactory lockFactory;

	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}

	@Autowired
	public void setSubmissionUtilityService(SubmissionUtilityService submissionUtility) {
		this.submissionUtility = submissionUtility;
	}
	
	@Autowired
	public void setSubmissionLockFactory(SubmissionLockFactory factory) {
		this.lockFactory = factory;
	}

	/**
	 * Get all the information needed to build the evaluator dashboard screen
	 * showing evaluator name with working and pending submissions.
	 * 
	 * @param evaluatorId
	 *            String identifier for the evaluator
	 * @return EvaluatorDashboard object for conversion to JSON
	 */
	public EvaluatorDashboard getEvaluatorDashboard(String evaluatorId) {
		User user = new User(
				cassandraRepo.getUser(evaluatorId).orElseThrow(() -> new UserNotFoundException(evaluatorId)));

		EvaluatorDashboard dashboard = new EvaluatorDashboard(user.getFirstName(), user.getLastName());

		List<EvaluationByEvaluatorModel> workingEvaluations = cassandraRepo
				.getSubmissionsByStatusAndEvaluator(evaluatorId, StatusUtil.WORKING);
		
		if (workingEvaluations.size() > 0) {
			List<UUID> submissionIds = workingEvaluations.stream().map(e -> e.getSubmissionId()).collect(Collectors.toList());
			List<SubmissionByIdModel> workingSubmissions = cassandraRepo.getSubmissionsById(submissionIds);
			dashboard.setWorkingQueue(workingSubmissions);
		}

		List<String> statuses = StatusUtil.getStatusesForQueues(user.getQueues());
		List<SubmissionModel> pendingSubmissions = new ArrayList<SubmissionModel>();

		if (CollectionUtils.isNotEmpty(statuses)) {
			if (CollectionUtils.isNotEmpty(user.getTasks())) {
				pendingSubmissions.addAll(cassandraRepo.getSubmissionsByStatusesAndTasks(statuses,
						user.getTasks().stream().collect(Collectors.toList())));
			} else {
				pendingSubmissions.addAll(cassandraRepo.getSubmissionsByStatuses(statuses));
			}
		}

		dashboard.setPendingQueue(pendingSubmissions);

		return dashboard;
	}

	/**
	 * Get the information required to populate the evaluator workspace screen.
	 * 
	 * @param evaluatorId
	 * @param submissionId
	 * @return EvaluatorWorkspace
	 */
	public EvaluatorWorkspace getEvaluatorWorkspace(UUID submissionId) {

		SubmissionModel submission = cassandraRepo.getSubmissionById(submissionId)
				.orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		TaskModel task = cassandraRepo.getTaskBasics(submission.getTaskId())
				.orElseThrow(() -> new TaskNotFoundException(submission.getTaskId()));
		EvaluationModel evaluation = cassandraRepo.getEvaluationById(submission.getEvaluationId())
				.orElseThrow(() -> new EvaluationNotFoundException(submission.getEvaluationId()));

		EvaluatorWorkspace workspace = new EvaluatorWorkspace(submission, task, evaluation);

		List<WorkspaceSubmission> previous = cassandraRepo
				.getSubmissionByStudentByTask(submission.getStudentId(), submission.getTaskId()).stream()
				.filter(s -> !s.getSubmissionId().equals(submission.getSubmissionId()))
				.map(sub -> new WorkspaceSubmission(sub, evaluation)).collect(Collectors.toList());
		workspace.setPreviousSubmissions(previous);

		return workspace;
	}

	/**
	 * Get the information required to populate the evaluator confirmation
	 * screen.
	 * 
	 * @param submissionId
	 * @return EvaluatorConfirmation
	 */
	public EvaluatorConfirmation getEvaluatorConfirmation(UUID submissionId) {

		SubmissionModel submission = cassandraRepo.getConfirmationSubmission(submissionId)
				.orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		RubricModel rubric = cassandraRepo.getTaskRubric(submission.getTaskId())
				.orElseThrow(() -> new TaskNotFoundException(submission.getTaskId())).getRubric();
		EvaluationModel evaluation = cassandraRepo.getEvaluationById(submission.getEvaluationId())
				.orElseThrow(() -> new EvaluationNotFoundException(submission.getEvaluationId()));

		EvaluatorConfirmation confirmation = new EvaluatorConfirmation(submission, rubric, evaluation);
		return confirmation;
	}

	/**
	 * Create or resume an evaluation for a qualified submission. User must have
	 * the queue permission and task settings to qualify to grade the specified
	 * submission. The submission must not have any current WORKING evaluations.
	 * 
	 * To alleviate race conditions in claiming submissions, create a lock record
	 * and test to see if the current user has the first lock.
	 * 
	 * @param String userId
	 * @param UUID submissionId
	 * 
	 * @throws EvaluatorNotQualifiedException
	 * @throws WorkingEvaluationException 
	 * @throws SubmissionStatusException
	 */
	public void claimSubmission(String userId, UUID submissionId) throws EvaluatorNotQualifiedException,
			WorkingEvaluationException, SubmissionStatusException {

		SubmissionLockModel lock = lockFactory.getSubmissionLock(submissionId, userId);
		
		try {
			cassandraRepo.saveSubmissionLock(lock);

			SubmissionLockModel winner = cassandraRepo.getSubmissionLocks(submissionId).stream()
					.min(Comparator.naturalOrder())
					.orElseThrow(() -> new IllegalStateException("Lock failed"));

			if (winner.getLockId().equals(lock.getLockId())) {

				SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId)
						.orElseThrow(() -> new SubmissionNotFoundException(submissionId));
				String oldStatus = submission.getStatus();

				UserModel user = cassandraRepo.getUserQualifications(userId)
						.orElseThrow(() -> new UserNotFoundException(userId));

				List<EvaluationBySubmissionModel> evaluations = cassandraRepo.getEvaluationsBySubmission(submissionId);
				if (evaluations.stream().filter(e -> e.getStatus().equals(StatusUtil.WORKING)).count() > 0) {
					throw new WorkingEvaluationException(submissionId);
				}

				if (this.isQualified(user, oldStatus, submission.getTaskId())) {
					EvaluationModel evaluation = evaluations.stream()
							.filter(e -> e.getEvaluatorId().equals(userId))
							.filter(f -> f.getStatus().equals(StatusUtil.CANCELLED))
							.findFirst()
							.orElseGet(() -> submissionUtility.getEvaluationForSubmission(submission, user));

					evaluation.setDateCompleted(null);
					evaluation.setStatus(StatusUtil.WORKING);
					submission.setEvaluation(StatusUtil.EVALUATION_BEGUN, user, evaluation);
					cassandraRepo.saveSubmission(submission, new EvaluationByIdModel(evaluation), userId, oldStatus, true);
				} else {
					throw new EvaluatorNotQualifiedException(userId, submissionId);
				}
			} else {
				throw new SubmissionStatusException("Failed to acquire lock on submission " +  submissionId);
			}
		} finally {
			cassandraRepo.deleteSubmissionLock(lock);
		}
	}

	public String releaseEvaluation(String evaluatorId, UUID submissionId, boolean retryAllowed, Comment reportComment)
			throws EvaluationNotFoundException, IncompleteScoreReportException,	EvaluationStatusException {

		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId)
				.orElseThrow(() -> new SubmissionNotFoundException(submissionId));

		if (!evaluatorId.equals(submission.getEvaluatorId()))
			throw new AuthorizationException("This submission can be released by an Admin or "
					+ submission.getEvaluatorFirstName() + " " + submission.getEvaluatorLastName());

		return submissionUtility.releaseEvaluation(evaluatorId, submission, retryAllowed, reportComment);
	}

	public String cancelEvaluation(String evaluatorId, UUID submissionId, String comments)
			throws EvaluationNotFoundException, EvaluationStatusException {

		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId)
				.orElseThrow(() -> new SubmissionNotFoundException(submissionId));

		if (!evaluatorId.equals(submission.getEvaluatorId()))
			throw new AuthorizationException("This submission can be cancelled by an Admin or "
					+ submission.getEvaluatorFirstName() + " " + submission.getEvaluatorLastName());

		return submissionUtility.cancelEvaluation(evaluatorId, submission, new Comment(comments, CommentTypes.CANCEL));
	}

	public void saveAspectScore(String evaluatorId, UUID submissionId, String aspectName, int score)
			throws WorkingEvaluationException {

		EvaluationModel evaluation = submissionUtility.getWorkingEvaluation(evaluatorId, submissionId);
		evaluation.getScoreReport().getScores().get(aspectName).setAssignedScore(score);
		cassandraRepo.saveScoreReport(evaluation);
	}

	public Comment saveAspectComment(String evaluatorId, UUID submissionId, String aspectName, Comment comment)
			throws WorkingEvaluationException {

		EvaluationModel evaluation = submissionUtility.getWorkingEvaluation(evaluatorId, submissionId);
		CommentModel result = evaluation.getScoreReport()
				.setAspectComment(comment, aspectName, evaluation.getAttempt(), evaluatorId,
						evaluation.getEvaluatorFirstName(), evaluation.getEvaluatorLastName());
		cassandraRepo.saveScoreReport(evaluation);
		return new Comment(result);
	}

	public Comment saveReportComment(String evaluatorId, UUID submissionId, Comment comment)
			throws WorkingEvaluationException {

		EvaluationModel evaluation = submissionUtility.getWorkingEvaluation(evaluatorId, submissionId);
		Comment result = new Comment(
				evaluation.getScoreReport().setReportComment(evaluatorId, evaluation.getEvaluatorFirstName(),
						evaluation.getEvaluatorLastName(), comment, evaluation.getAttempt()));
		cassandraRepo.saveScoreReport(evaluation);
		return result;
	}

	public List<Referral> saveReferral(String evaluatorId, UUID submissionId, Referral referral)
			throws SubmissionStatusException {

		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId)
				.orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		String oldStatus = submission.getStatus();

		if (StatusUtil.isInEvaluation(submission.getStatus())) {
			referral.setCreatedBy(evaluatorId);
			referral.setDateCreated(DateUtil.getZonedNow());
			submission.getReferralsNS().add(new ReferralModel(referral));
			cassandraRepo.saveSubmission(submission, evaluatorId, oldStatus);
			return submission.getReferralsNS().stream().map(r -> new Referral(r))
					.collect(Collectors.toList());
		} else {
			throw new SubmissionStatusException("Submission must be in evaluation status.");
		}
	}

	public boolean isQualified(UserModel user, String status, UUID taskId) {
		return user.getPermissions().contains(StatusUtil.getQueueForStatus(status))
				&& (user.getTasks().isEmpty() || user.getTasks().contains(taskId));
	}
}
