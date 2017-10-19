package edu.wgu.dmadmin.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.IncompleteScoreReportException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.exception.WorkingEvaluationException;
import edu.wgu.dmadmin.model.assessment.CommentModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByEvaluatorModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

/**
 * Created by john.buchanan on 5/9/17.
 */
@Service
public class SubmissionUtilityService {

	CassandraRepo cassandraRepo;

    PublishAcademicActivityService publishAcademicActivityService;

    @Value("${dm.academicActivity.title:dm.assessment.}")
    String academicActivityTitle;

    @Value("${dm.academicActivity.submit.for.evaluation:submit.for.evaluation}")
    String academicActivitySubmitForEvaluation;

    @Value("${dm.academicActivity.return.for.revision:return.for.revision}")
    String academicActivityReturnForRevision;

    @Value("${dm.academicActivity.passed:passed}")
    String academicActivityPassed;

	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}

    @Autowired
    public void setPublishAcademicActivityService(PublishAcademicActivityService service) {
        this.publishAcademicActivityService = service;
    }

	public List<Comment> getInternalComments(UUID submissionId) {
		SubmissionByIdModel submission = cassandraRepo.getInternalComments(submissionId)
				.orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		return submission.getInternalCommentsNS().entrySet().stream()
				.map(internalComment -> new Comment(internalComment.getValue())).collect(Collectors.toList());
	}

	public List<Comment> updateInternalComments(String bannerId, UUID submissionId, List<Comment> comments) {

		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId)
				.orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		UserByIdModel user = cassandraRepo.getUser(bannerId).orElseThrow(() -> new UserNotFoundException(bannerId));
		Map<UUID, CommentModel> internalComments = submission.getInternalCommentsNS();

		String oldStatus = submission.getStatus();

		comments.stream().map(comment -> {
			if (comment.getCommentId() == null) {
				comment.setCommentId(UUID.randomUUID());
				comment.setDateCreated(DateUtil.getZonedNow());
			}
			if (comment.getType() == null) {
				comment.setType(CommentTypes.INTERNAL);
			}
			if (comment.getUserId() == null || comment.getFirstName() == null || comment.getLastName() == null) {
				comment.setUserId(bannerId);
				comment.setFirstName(user.getFirstName());
				comment.setLastName(user.getLastName());
			}
			comment.setAttempt(submission.getAttempt());
			comment.setDateUpdated(DateUtil.getZonedNow());
			return comment;
		}).forEach(comment -> {
			if (internalComments.keySet().contains(comment.getCommentId())) {
				CommentModel commentModel = internalComments.get(comment.getCommentId());
				if (commentModel.getUserId() == null) {
					commentModel.setUserId(comment.getUserId());
				}
				if (commentModel.getUserId().equalsIgnoreCase(comment.getUserId())) {
					commentModel.setDateUpdated(DateUtil.getZonedNow());
					commentModel.setAttempt(comment.getAttempt());
					commentModel.setType(comment.getType());
					commentModel.setComments(comment.getComments());
					commentModel.setFirstName(comment.getFirstName());
					commentModel.setLastName(comment.getLastName());
					internalComments.put(commentModel.getCommentId(), commentModel);
				}
			} else {
				internalComments.put(comment.getCommentId(), new CommentModel(comment));
			}
		});

		submission.setInternalComments(internalComments);
		cassandraRepo.saveSubmission(submission, bannerId, oldStatus);
		return internalComments.entrySet().stream().map(internalComment -> new Comment(internalComment.getValue()))
				.collect(Collectors.toList());
	}

	/**
	 * Check the permissions of the user to clear the specific hold on the
	 * requested submission. If the status is an attempt hold, set it to needs
	 * revision, otherwise return to the previous status.
	 *
	 * @param String userId
	 * @param UUID submissionId
	 * @param Comment comment
	 * @return String newStatus
	 * @throws SubmissionStatusException
	 */
	public String clearSubmissionHold(String userId, UUID submissionId, Comment comment)
			throws SubmissionStatusException {

		UserModel user = cassandraRepo.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));

		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(submissionId)
				.orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		String oldStatus = submission.getStatus();

		String status = "";
		if (user.getPermissions().contains(Permissions.ALL_CLEAR)
				|| user.getPermissions().contains(Permissions.getPermissionforStatus(submission.getStatus()))) {
			if (StatusUtil.AUTHOR_WORK_EVALUATED.equals(submission.getStatus())) {
				status = StatusUtil.AUTHOR_WORK_NEEDS_REVISION;
			} else {
				status = cassandraRepo.getLastStatusForSubmission(submission.getStudentId(),
						submission.getSubmissionId());
			}

			CommentModel newComment = new CommentModel(user.getUserId(), user.getFirstName(), user.getLastName(),
					comment.getComments(), submission.getAttempt(), -1, comment.getType());
			submission.getInternalCommentsNS().put(newComment.getCommentId(), newComment);

			submission.setStatus(status);
			cassandraRepo.saveSubmission(submission, userId, oldStatus);
		} else {
			throw new SubmissionStatusException("No permissions to clear hold for this submission.");
		}

		return status;
	}

	public String cancelEvaluation(String userId, SubmissionByIdModel submission, Comment comments)
			throws EvaluationNotFoundException, EvaluationStatusException {

		EvaluationByIdModel evaluation = cassandraRepo.getEvaluationById(submission.getEvaluationId())
				.orElseThrow(() -> new EvaluationNotFoundException(submission.getEvaluationId()));
		String oldStatus = submission.getStatus();

		if (!StatusUtil.WORKING.equals(evaluation.getStatus()))
			throw new EvaluationStatusException(evaluation.getStatus(), StatusUtil.WORKING);

		evaluation.complete(StatusUtil.CANCELLED);

		CommentModel comment = new CommentModel(userId, evaluation.getEvaluatorFirstName(),
				evaluation.getEvaluatorLastName(), comments.getComments(), submission.getAttempt(), -1,
				comments.getType());
		submission.getInternalCommentsNS().put(comment.getCommentId(), comment);

		submission.cancelEvaluation();
		cassandraRepo.saveSubmission(submission, evaluation, userId, oldStatus, true);

		return cassandraRepo.getSubmissionStatus(submission.getSubmissionId()).get().getStatus();
	}

    public String releaseEvaluation(String userId, SubmissionByIdModel submission, boolean retry, Comment comment)
            throws IncompleteScoreReportException, EvaluationStatusException {

        UserModel user = cassandraRepo.getUserQualifications(userId).orElseThrow(() -> new UserNotFoundException(userId));
        EvaluationByIdModel evaluation = cassandraRepo.getEvaluationById(submission.getEvaluationId())
                .orElseThrow(() -> new EvaluationNotFoundException(submission.getEvaluationId()));
        String oldStatus = submission.getStatus();

        if (!StatusUtil.WORKING.equals(evaluation.getStatus()))
            throw new EvaluationStatusException(evaluation.getStatus(), StatusUtil.WORKING);

        ScoreReportModel scoreReport = evaluation.getScoreReport();
        Set<String> unscored = scoreReport.getUnscoredAspects();
        if (!unscored.isEmpty()) throw new IncompleteScoreReportException(unscored);

        scoreReport.setReportComment(user.getUserId(), user.getFirstName(), user.getLastName(), comment,
                evaluation.getAttempt());
        evaluation.complete(StatusUtil.COMPLETED);

        submission.setStatus(StatusUtil.getReleaseStatus(scoreReport.isPassed(), retry));
        submission.setDateCompleted(evaluation.getDateCompleted());
        cassandraRepo.saveSubmission(submission, evaluation, user.getUserId(), oldStatus, true);

        if (StatusUtil.isFailed(submission.getStatus())) {
            publishAcademicActivityService.publishAcademicActivity(submission, academicActivityTitle + academicActivityReturnForRevision);
        } else {
            publishAcademicActivityService.publishAcademicActivity(submission,  academicActivityTitle + academicActivityPassed);
        }

        return cassandraRepo.getSubmissionStatus(submission.getSubmissionId()).get().getStatus();
    }

	public String releaseReviewEvaluation(String userId, EvaluationByIdModel evaluation, boolean retry, Comment comment)
			throws EvaluationStatusException, IncompleteScoreReportException {
		UserModel user = cassandraRepo.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));
		SubmissionByIdModel submission = cassandraRepo.getSubmissionById(evaluation.getSubmissionId())
				.orElseThrow(() -> new SubmissionNotFoundException(evaluation.getSubmissionId()));
		String oldStatus = submission.getStatus();

		if (!StatusUtil.EVALUATION_EDITED.equals(submission.getStatus()))
			throw new EvaluationStatusException(submission.getStatus(), StatusUtil.WORKING);

		ScoreReportModel scoreReport = evaluation.getScoreReport();
		Set<String> unscored = scoreReport.getUnscoredAspects();
		if (!unscored.isEmpty())
			throw new IncompleteScoreReportException(
					"The following aspects have not been scored: " + unscored.toString());

		scoreReport.setReportComment(user.getUserId(), user.getFirstName(), user.getLastName(), comment,
				submission.getAttempt());
		evaluation.complete(StatusUtil.COMPLETED);

		submission.setEvaluation(StatusUtil.getReleaseStatus(scoreReport.isPassed(), retry),
				user, evaluation);

		submission.setDateCompleted(evaluation.getDateCompleted());
		cassandraRepo.saveSubmission(submission, evaluation, user.getUserId(), oldStatus, true);

		return cassandraRepo.getSubmissionStatus(submission.getSubmissionId()).get().getStatus();
	}

	public EvaluationBySubmissionModel getEvaluationForSubmission(SubmissionModel submission, UserModel evaluator) {

		EvaluationBySubmissionModel evaluation = new EvaluationBySubmissionModel(evaluator, submission);

		if (submission.getPreviousEvaluationId() != null) {
			EvaluationModel previous = cassandraRepo.getEvaluationById(submission.getPreviousEvaluationId())
					.orElseThrow(() -> new EvaluationNotFoundException(submission.getPreviousEvaluationId()));
			evaluation.importScoreReport(previous.getScoreReport());
		} else {
			RubricModel rubric = cassandraRepo.getTaskRubric(submission.getTaskId())
					.orElseThrow(() -> new TaskNotFoundException(submission.getTaskId())).getRubric();
			evaluation.setScoreReport(new ScoreReportModel(rubric));
		}

		return evaluation;
	}

	public EvaluationModel getWorkingEvaluation(String evaluatorId, UUID submissionId) throws WorkingEvaluationException {
		List<EvaluationByEvaluatorModel> evaluations = 
				cassandraRepo.getEvaluationByEvaluaatorAndSubmission(evaluatorId, StatusUtil.WORKING, submissionId);
		if (evaluations.size() == 1) {
			return evaluations.get(0);
		} else {
			throw new WorkingEvaluationException(submissionId, evaluations.size());
		}
	}
}
