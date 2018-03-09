package edu.wgu.dmadmin.domain.security;

import java.util.HashSet;
import java.util.Set;

import edu.wgu.dmadmin.util.StatusUtil;

public class Permissions {

	public static final String SYSTEM = "SYSTEM";

	public static final String ARTICULATION_QUEUE = "articulation-queue";
	public static final String ARTICUALATION_HOLD = "articulation-hold";
	public static final String ARTICULATION_CLEAR = "articulation-clear";

	public static final String ORIGINALITY_QUEUE = "originality-queue";
	public static final String ORIGINALITY_HOLD = "originality-hold";
	public static final String ORIGINALITY_CLEAR = "originality-clear";

	public static final String LEAD_QUEUE = "lead-queue";
	public static final String LEAD_HOLD = "lead-hold";
	public static final String LEAD_CLEAR = "lead-clear";

	public static final String OPEN_QUEUE = "open-queue";
	public static final String OPEN_HOLD = "open-hold";
	public static final String OPEN_CLEAR = "open-clear";

	public static final String ATTEMPTS_QUEUE = "attempts-queue";
	public static final String ATTEMPTS_HOLD = "attempts-hold";
	public static final String ATTEMPTS_CLEAR = "attempts-clear";

	public static final String SPOOF_STUDENT = "spoof-student";

	public static final String DIRECTORY_SEARCH = "directory-search";
	public static final String USER_SEARCH = "user-search";
	public static final String USER_CREATE = "user-create";
	public static final String USER_DELETE = "user-delete";

	public static final String TEAM_CREATE = "team-create";
	public static final String TEAM_ASSIGN = "team-assign";

	public static final String ROLE_CREATE = "role-create";
	public static final String ROLE_ASSIGN = "role-assign";

	public static final String TASK_QUEUE = "task-queue";
	public static final String EVALUATION_CLAIM = "evaluation-claim";
	public static final String EVALUATION_RELEASE = "evaluation-release";
	public static final String EVALUATION_VIEW = "evaluation-view";

	public static final String PUBLISH_TASK = "publish-task";
	public static final String EDIT_TASK = "edit-task";
	public static final String RETIRE_TASK = "retire-task";
	public static final String CREATE_TASK = "create-task";

	public static final String EVALUATION_ASSIGN = "evaluation-assign";
	public static final String SUBMISSION_SEARCH = "submission-search";
	public static final String EVALUATION_MODIFY = "released-modify";
	public static final String MASTER_QUEUE = "master-queue";
	public static final String ALL_CLEAR = "all-clear";
	public static final String MASTER_RELEASE = "master-release";
	public static final String INTERNAL_COMMENT = "internal-comment";

	public static Set<String> getQueues() {
		Set<String> queues = new HashSet<String>();
		queues.add(OPEN_QUEUE);
		queues.add(ORIGINALITY_QUEUE);
		queues.add(ARTICULATION_QUEUE);
		queues.add(LEAD_QUEUE);
		return queues;
	}

	public static String getPermissionforStatus(String status) {
		String permission = "";

		switch (status) {
		case StatusUtil.LEAD_HOLD:
			permission = LEAD_CLEAR;
			break;
		case StatusUtil.ARTICULATION_HOLD:
			permission = ARTICULATION_CLEAR;
			break;
		case StatusUtil.ORIGINALITY_HOLD:
			permission = ORIGINALITY_CLEAR;
			break;
		case StatusUtil.OPEN_HOLD:
			permission = OPEN_CLEAR;
			break;
		case StatusUtil.AUTHOR_WORK_EVALUATED:
			permission = ATTEMPTS_CLEAR;
			break;
		}

		return permission;
	}
}
