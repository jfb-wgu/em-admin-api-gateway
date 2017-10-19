package edu.wgu.dmadmin.factory;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

public class SubmissionFactory {

	/**
	 * Begin a student submission by constructing a Submission object.
	 *
	 * If this is a first submission (no previous): attempt = 1 status =
	 * AUTHOR_SUBMISSION_STARTED
	 *
	 * If this is a resubmission (failed previous): attempt = previous + 1
	 * status = AUTHOR_RESUBMISSION_STARTED
	 *
	 * If this is resuming a cancelled submission: change status from cancelled
	 * to either AUTHOR_SUBMISSION_STARTED or AUTHOR_RESUBMISSION_STARTED as
	 * appropriate.
	 *
	 * @param studentId
	 * @param task
	 * @param previous
	 * @param pidm
	 * @throws SubmissionStatusException
	 */
	public SubmissionByIdModel getSubmission(String studentId, TaskModel task, List<? extends SubmissionModel> previous,
			Long pidm) throws SubmissionStatusException {
		
		SubmissionModel last = previous.stream().max(Comparator.reverseOrder()).orElse(null);

		if (last != null && !StatusUtil.canBeginSubmission(last.getStatus()))
			throw new SubmissionStatusException(last.getSubmissionId(), last.getStatus());

		if (last != null && StatusUtil.isCancelled(last.getStatus())) {
			last.setStatus(last.getAttempt() > 1 ? StatusUtil.AUTHOR_RESUBMISSION_STARTED : StatusUtil.AUTHOR_SUBMISSION_STARTED);
			last.setDateSubmitted(null);
			last.setDateEstimated(null);
			return new SubmissionByIdModel(last);
		}

		SubmissionByIdModel submission = new SubmissionByIdModel();

		if (last == null) {
			submission.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);
			submission.setAttempt(1);
		} else {
			submission.setStatus(StatusUtil.AUTHOR_RESUBMISSION_STARTED);
			submission.setAttempt(last.getAttempt() + 1);
			submission.setPreviousSubmissionId(last.getSubmissionId());
			submission.setPreviousEvaluationId(last.getEvaluationId());
		}

		submission.setSubmissionId(UUID.randomUUID());
		submission.setDateCreated(DateUtil.getZonedNow());
		submission.setStudentId(studentId);
		submission.setTaskId(task.getTaskId());
		submission.setTaskName(task.getTaskName());
		submission.setCourseName(task.getCourseName());
		submission.setCourseCode(task.getCourseCode());
		submission.setAssessmentName(task.getAssessmentName());
		submission.setAssessmentCode(task.getAssessmentCode());
		submission.setAspectCount(task.getRubric().getAspects().size());
		submission.setAssessmentId(task.getAssessmentId());
		submission.setPidm(pidm);

		return submission;
	}
}
