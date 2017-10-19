package edu.wgu.dmadmin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import edu.wgu.dmadmin.domain.security.Permissions;

public class StatusUtil {

	public static final String AUTHOR_SUBMISSION_STARTED = "-2";
	public static final String AUTHOR_RESUBMISSION_STARTED = "-4";
	public static final String AUTHOR_WORK_SUBMITTED = "2";
	public static final String EVALUATION_METHOD_CHANGED = "3";
	public static final String AUTHOR_WORK_RESUBMITTED = "4";
	public static final String AUTHOR_WORK_EVALUATED = "8";
	public static final String AUTHOR_WORK_NEEDS_REVISION = "16";
	public static final String EVALUATION_BEGUN = "32";
	public static final String EVALUATION_CANCELLED = "33";
	public static final String EVALUATION_TAKEN_OVER = "34";
	public static final String EVALUATION_EDITED = "35";
	public static final String EVALUATION_METHOD_CHANGED_AFTER_PUBLICATION = "36";
	public static final String SUBMISSION_CANCELLED = "37";
	public static final String ALL_EVALUATIONS_CANCELLED = "40";
	public static final String EVALUATION_RELEASED = "64";
	public static final String LEAD_HOLD = "128";
	public static final String ORIGINALITY_HOLD = "256";
	public static final String ARTICULATION_HOLD = "512";
	public static final String OPEN_HOLD = "1024";

	public static final String NOT_STARTED = "NOT STARTED";
	public static final String STARTED = "STARTED";
	public static final String PENDING = "PENDING";
	public static final String WORKING = "WORKING";
	public static final String COMPLETED = "COMPLETED";
	public static final String CANCELLED = "CANCELLED";
	public static final String UNKNOWN = "UNKNOWN";
	public static final String HOLD = "HOLD";

	public static final String DISPLAY_STARTED = "Started";
	public static final String DISPLAY_WORKING = "In Evaluation";
	public static final String DISPLAY_IN_PROGRESS = "In Progress";
	public static final String DISPLAY_MENTOR = "Mentor Required";
	public static final String DISPLAY_REVISION = "Revision Needed";
	public static final String DISPLAY_PASSED = "Passed";
	public static final String DISPLAY_CANCELLED = "Cancelled";
	public static final String DISPLAY_WORK_SUBMITTED = "Work Submitted";
	public static final String DISPLAY_WORK_RESUBMITTED = "Work Resubmitted";
	public static final String DISPLAY_EVALUATION_REVIEW = "Evaluation Review";
	
	private static List<String> ordered = Arrays.asList(
			SUBMISSION_CANCELLED, AUTHOR_RESUBMISSION_STARTED, AUTHOR_SUBMISSION_STARTED, AUTHOR_WORK_SUBMITTED, AUTHOR_WORK_RESUBMITTED, 
			EVALUATION_CANCELLED, EVALUATION_BEGUN, EVALUATION_TAKEN_OVER, LEAD_HOLD, ORIGINALITY_HOLD, ARTICULATION_HOLD, 
			OPEN_HOLD, AUTHOR_WORK_EVALUATED, AUTHOR_WORK_NEEDS_REVISION, EVALUATION_EDITED, EVALUATION_RELEASED);

	/*
	 * The following are a series of convenience methods to isolate the status
	 * logic into this class.
	 */

	public static boolean isStarted(String status) {
		return AUTHOR_SUBMISSION_STARTED.equals(status) || AUTHOR_RESUBMISSION_STARTED.equals(status);
	}

	public static boolean isCancelled(String status) {
		return SUBMISSION_CANCELLED.equals(status);
	}
	
	public static boolean isPending(String status) {
		return (AUTHOR_WORK_SUBMITTED.equals(status) || AUTHOR_WORK_RESUBMITTED.equals(status)
				|| EVALUATION_CANCELLED.equals(status));
	}
	
	public static boolean isEvaluationCancelled(String status) {
		return EVALUATION_CANCELLED.equals(status);
	}

	public static boolean isSubmitted(String status) {
		return (AUTHOR_WORK_SUBMITTED.equals(status) || AUTHOR_WORK_RESUBMITTED.equals(status));
	}

	public static boolean isInEvaluation(String status) {
		return (EVALUATION_BEGUN.equals(status) || EVALUATION_TAKEN_OVER.equals(status));
	}

	public static boolean isEvaluated(String status) {
		return (AUTHOR_WORK_NEEDS_REVISION.equals(status) || EVALUATION_RELEASED.equals(status)
				|| AUTHOR_WORK_EVALUATED.equals(status) || EVALUATION_EDITED.equals(status));
	}

	public static boolean canReview(String status) {
		return (AUTHOR_WORK_NEEDS_REVISION.equals(status) || EVALUATION_RELEASED.equals(status)
				|| AUTHOR_WORK_EVALUATED.equals(status));
	}

	public static boolean isPassed(String status) {
		return (EVALUATION_RELEASED.equals(status));
	}

	public static boolean isFailed(String status) {
		return (AUTHOR_WORK_NEEDS_REVISION.equals(status) || AUTHOR_WORK_EVALUATED.equals(status));
	}

	public static boolean isHeld(String status) {
		return LEAD_HOLD.equals(status) || ORIGINALITY_HOLD.equals(status) || ARTICULATION_HOLD.equals(status)
				|| OPEN_HOLD.equals(status);
	}

	public static boolean canBeginSubmission(String status) {
		return isCancelled(status) || isFailed(status);
	}
	
	public static boolean hasBeenSubmitted(String status) {
		return ordered.indexOf(status) > ordered.indexOf(AUTHOR_SUBMISSION_STARTED);
	}
	
	public static boolean hasBeenClaimed(String status) {
		return ordered.indexOf(status) > ordered.indexOf(EVALUATION_CANCELLED);
	}
	
	public static boolean hasBeenEvaluated(String status) {
		return ordered.indexOf(status) > ordered.indexOf(OPEN_HOLD);
	}

	/*
	 * The following are a series of convenience methods for determining display
	 * values for various status codes.
	 */

	public static String getStatusGroup(String status) {
		String group;

		switch (status) {
		case SUBMISSION_CANCELLED:
			group = CANCELLED;
			break;
		case LEAD_HOLD:
		case ORIGINALITY_HOLD:
		case ARTICULATION_HOLD:
		case OPEN_HOLD:
			group = HOLD;
			break;
		case AUTHOR_RESUBMISSION_STARTED:
		case AUTHOR_SUBMISSION_STARTED:
			group = STARTED;
			break;
		case AUTHOR_WORK_SUBMITTED:
		case AUTHOR_WORK_RESUBMITTED:
		case EVALUATION_CANCELLED:
			group = PENDING;
			break;
		case EVALUATION_BEGUN:
		case EVALUATION_TAKEN_OVER:
			group = WORKING;
			break;
		case AUTHOR_WORK_EVALUATED:
		case AUTHOR_WORK_NEEDS_REVISION:
		case EVALUATION_RELEASED:
		case EVALUATION_EDITED:
			group = COMPLETED;
			break;
		case EVALUATION_METHOD_CHANGED:
		case EVALUATION_METHOD_CHANGED_AFTER_PUBLICATION:
			group = UNKNOWN;
			break;
		default:
			throw new IllegalArgumentException("Invalid status: " + status);
		}

		return group;
	}

	public static String getSearchStatus(String status) {
		String display = UNKNOWN;

		switch (status) {
		case AUTHOR_SUBMISSION_STARTED:
		case AUTHOR_RESUBMISSION_STARTED:
		case AUTHOR_WORK_SUBMITTED:
		case AUTHOR_WORK_RESUBMITTED:
		case SUBMISSION_CANCELLED:
			display = DISPLAY_STARTED;
			break;
		case EVALUATION_BEGUN:
		case EVALUATION_TAKEN_OVER:
			display = DISPLAY_WORKING;
			break;
		case AUTHOR_WORK_EVALUATED:
			display = DISPLAY_MENTOR;
			break;
		case AUTHOR_WORK_NEEDS_REVISION:
			display = DISPLAY_REVISION;
			break;
		case EVALUATION_RELEASED:
			display = DISPLAY_PASSED;
			break;
		case EVALUATION_CANCELLED:
			display = DISPLAY_CANCELLED;
			break;
		case EVALUATION_EDITED:
			display = DISPLAY_EVALUATION_REVIEW;
			break;
		case LEAD_HOLD:
		case ORIGINALITY_HOLD:
		case ARTICULATION_HOLD:
		case OPEN_HOLD:
			display = HOLD;
			break;
		}

		return display;
	}

	public static List<String> getStatusesForQueues(Set<String> queues) {
		List<String> statuses = new ArrayList<String>();

		queues.forEach(queue -> {
			switch (queue) {
			case Permissions.LEAD_QUEUE:
				statuses.add(LEAD_HOLD);
				break;
			case Permissions.ORIGINALITY_QUEUE:
				statuses.add(ORIGINALITY_HOLD);
				break;
			case Permissions.ARTICULATION_QUEUE:
				statuses.add(ARTICULATION_HOLD);
				break;
			case Permissions.OPEN_QUEUE:
				statuses.add(OPEN_HOLD);
				break;
			case Permissions.TASK_QUEUE:
				statuses.add(AUTHOR_WORK_SUBMITTED);
				statuses.add(AUTHOR_WORK_RESUBMITTED);
				statuses.add(EVALUATION_CANCELLED);
				break;
			default:
				throw new IllegalArgumentException("Not a valid queue permission [" + queue + "]");
			}
		});

		return statuses;
	}

	public static String getQueueForStatus(String status) {
		String queue = UNKNOWN;

		switch (status) {
		case LEAD_HOLD:
			queue = Permissions.LEAD_QUEUE;
			break;
		case ORIGINALITY_HOLD:
			queue = Permissions.ORIGINALITY_QUEUE;
			break;
		case ARTICULATION_HOLD:
			queue = Permissions.ARTICULATION_QUEUE;
			break;
		case OPEN_HOLD:
			queue = Permissions.OPEN_QUEUE;
			break;
		case AUTHOR_WORK_SUBMITTED:
		case AUTHOR_WORK_RESUBMITTED:
		case EVALUATION_CANCELLED:
			queue = Permissions.TASK_QUEUE;
			break;
		}

		return queue;
	}

	public static String getSubmittedStatus(String status) {
		switch (status) {
		case AUTHOR_SUBMISSION_STARTED:
			return AUTHOR_WORK_SUBMITTED;
		case AUTHOR_RESUBMISSION_STARTED:
			return AUTHOR_WORK_RESUBMITTED;
		default:
			throw new IllegalArgumentException("Not a valid start status [" + status + "]");
		}
	}

	public static String getStudentStatus(String status, int attempt) {
		String studentStatus = null;

		switch (status) {
		case EVALUATION_BEGUN:
		case EVALUATION_CANCELLED:
		case EVALUATION_TAKEN_OVER:
		case ALL_EVALUATIONS_CANCELLED:
			studentStatus = attempt > 1 ? DISPLAY_WORK_RESUBMITTED : DISPLAY_WORK_SUBMITTED;
			break;
		case AUTHOR_WORK_SUBMITTED:
			studentStatus = DISPLAY_WORK_SUBMITTED;
			break;
		case AUTHOR_WORK_RESUBMITTED:
			studentStatus = DISPLAY_WORK_RESUBMITTED;
			break;
		case AUTHOR_WORK_EVALUATED:
		case AUTHOR_WORK_NEEDS_REVISION:
		case AUTHOR_RESUBMISSION_STARTED:
			studentStatus = DISPLAY_REVISION;
			break;
		case EVALUATION_RELEASED:
			studentStatus = DISPLAY_PASSED;
			break;
		case AUTHOR_SUBMISSION_STARTED:
			studentStatus = DISPLAY_IN_PROGRESS;
			break;
		case SUBMISSION_CANCELLED:
			studentStatus = attempt > 1 ? DISPLAY_REVISION : DISPLAY_IN_PROGRESS;
			break;
		case EVALUATION_EDITED:
			studentStatus = UNKNOWN;
			break;
		default:
			studentStatus = UNKNOWN;
		}

		return studentStatus;
	}

	public static String getReleaseStatus(boolean isPassed, boolean isRetry) {
		String status = UNKNOWN;

		if (isPassed) {
			status = EVALUATION_RELEASED;
		} else {
			if (isRetry) {
				status = AUTHOR_WORK_NEEDS_REVISION;
			} else {
				status = AUTHOR_WORK_EVALUATED;
			}
		}

		return status;
	}
}
