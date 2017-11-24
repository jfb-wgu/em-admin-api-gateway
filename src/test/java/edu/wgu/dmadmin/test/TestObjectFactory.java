package edu.wgu.dmadmin.test;

import static edu.wgu.dreammachine.util.StatusUtil.getStatusGroup;
import static edu.wgu.dreammachine.util.StatusUtil.hasBeenClaimed;
import static edu.wgu.dreammachine.util.StatusUtil.hasBeenEvaluated;
import static edu.wgu.dreammachine.util.StatusUtil.hasBeenSubmitted;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import edu.wgu.dmadmin.domain.search.SearchCriteria;
import edu.wgu.dreammachine.domain.security.Permissions;
import edu.wgu.dreammachine.domain.security.User;
import edu.wgu.dreammachine.model.publish.RubricModel;
import edu.wgu.dreammachine.model.publish.TaskModel;
import edu.wgu.dreammachine.model.security.PermissionModel;
import edu.wgu.dreammachine.model.security.RoleModel;
import edu.wgu.dreammachine.model.security.UserByIdModel;
import edu.wgu.dreammachine.model.submission.SubmissionModel;
import edu.wgu.dreammachine.util.DateUtil;
import edu.wgu.dreammachine.util.StatusUtil;

public class TestObjectFactory {

	static String evaluatorLastName = "Wayne";
	static String evaluatorFirstName = "Bruce";
	static String studentId = "student";
	static UUID submissionId = UUID.randomUUID();
	static String evaluatorId = "evaluator";
	static String employeeId = "employeeId";
	static SubmissionModel submissionModel;
	static UUID assessmentId = UUID.randomUUID();
	static Random random = new Random();
	static UUID evaluationId = UUID.randomUUID();
	static UUID taskId = UUID.randomUUID();

	public static SearchCriteria getSearchCriteria() {
		List<UUID> taskList = new ArrayList<>();
		taskList.add(UUID.randomUUID());
		taskList.add(UUID.randomUUID());
		taskList.add(UUID.randomUUID());

		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setStatus(StatusUtil.EVALUATION_BEGUN);
		searchCriteria.setTasks(taskList);
		searchCriteria.setDateRange(DateUtil.getZonedNow().toString());
		searchCriteria.setEvaluatorLastName(evaluatorLastName);
		searchCriteria.setEvaluatorFirstName(evaluatorFirstName);
		searchCriteria.setStudentId(studentId);
		searchCriteria.setSubmissionId(submissionId.toString());

		return searchCriteria;
	}

	public static UserByIdModel getUserModel() {
		UserByIdModel user = getUserModel(evaluatorFirstName, evaluatorLastName, evaluatorId, new HashSet<UUID>(),
				new HashSet<String>(), new HashSet<UUID>(), new HashSet<String>(), employeeId);

		user.getRoles().add(UUID.randomUUID());
		user.getLandings().add("dashboard");
		user.getLandings().add("evaluator");
		user.getPermissions().add(Permissions.TASK_QUEUE);
		user.getPermissions().add(Permissions.EVALUATION_CLAIM);

		return user;
	}

	public static UserByIdModel getUserModel(String inUserId, String inEmployeeId) {
		return getUserModel(evaluatorFirstName, evaluatorLastName, inUserId, new HashSet<UUID>(), new HashSet<String>(),
				new HashSet<UUID>(), new HashSet<String>(), inEmployeeId);
	}

	public static UserByIdModel getUserModel(String firstName, String lastName, String userId, Set<UUID> roles,
			Set<String> permissions, Set<UUID> tasks, Set<String> landings, String inEmployeeId) {
		UserByIdModel user = new UserByIdModel();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserId(userId);
		user.setLandings(landings);
		user.setPermissions(permissions);
		user.setRoles(roles);
		user.setTasks(tasks);
		user.setEmployeeId(inEmployeeId);

		return user;
	}

	public static User getUser(String firstName, String lastName, String userId, Set<UUID> roles,
			Set<String> permissions, Set<UUID> tasks, Set<String> landings, String inEmployeeId) {
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserId(userId);
		user.setLandings(landings);
		user.setPermissions(permissions);
		user.setRoles(roles);
		user.setTasks(tasks);
		user.setEmployeeId(inEmployeeId);

		return user;
	}

	public static RoleModel getRoleModel(String role) {
		return getRoleModel(role, UUID.randomUUID());
	}

	public static RoleModel getRoleModel(String role, UUID roleId) {
		return getRoleModel(role, roleId, new HashSet<UUID>(), "testing role");
	}

	public static RoleModel getRoleModel(String role, UUID roleId, Set<UUID> permissions, String description) {
		RoleModel model = new RoleModel();
		model.setDateCreated(DateUtil.getZonedNow());
		model.setRole(role);
		model.setPermissions(permissions);
		model.setRoleDescription(description);
		model.setRoleId(roleId);

		return model;
	}

	public static PermissionModel getPermission(String permission, String landing) {
		return getPermissionModel(permission, UUID.randomUUID(), "test permission", landing, "test type");
	}

	public static PermissionModel getPermissionModel(String permission, UUID permissionId, String description,
			String landing, String type) {
		PermissionModel model = new PermissionModel();
		model.setDateCreated(DateUtil.getZonedNow());
		model.setPermission(permission);
		model.setLanding(landing);
		model.setPermissionDescription(description);
		model.setPermissionId(permissionId);
		model.setPermissionType(type);

		return model;
	}
	
    public static SubmissionModel getSubmissionModel(String status) {
        return getSubmissionModel(submissionId, status, taskId, studentId, evaluatorFirstName, evaluatorLastName, evaluatorId);
    }

	public static SubmissionModel getSubmissionModel(UUID inSubmissionId, String status, UUID inTaskId,
			String inStudentId, String firstName, String lastName, String inEvaluatorId) {

		submissionModel = new SubmissionModel();
		submissionModel.setSubmissionId(inSubmissionId);
		submissionModel.setStudentId(inStudentId);
		submissionModel.setAttempt(1);
		submissionModel.setTaskId(inTaskId);
		submissionModel.setTaskName("Task1");
		submissionModel.setAspectCount(4);
		submissionModel.setCourseCode("C745");
		submissionModel.setCourseName("How to be Batman");
		submissionModel.setAssessmentId(assessmentId);
		submissionModel.setAssessmentCode("BAT1");
		submissionModel.setAssessmentName("Batman Final Test");
		submissionModel.setComments("These are some comments");
		submissionModel.setInternalComments(null);
		submissionModel.setStatus(status);
		submissionModel.setStatusGroup(getStatusGroup(status));
		submissionModel.setReferrals(null);
		submissionModel.setPreviousSubmissionId(null);
		submissionModel.setPreviousEvaluationId(null);
		submissionModel.setReviewEvaluationId(null);
		submissionModel.setPidm(new Long(234567));

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, Math.negateExact(random.nextInt(1440)));
		submissionModel.setDateCreated(calendar.getTime());

		if (hasBeenSubmitted(status)) {
			calendar.add(Calendar.HOUR, random.nextInt(48));
			submissionModel.setDateSubmitted(calendar.getTime());

			calendar.add(Calendar.HOUR, 72);
			submissionModel.setDateEstimated(calendar.getTime());

			if (hasBeenClaimed(status)) {
				calendar.add(Calendar.HOUR, Math.negateExact(random.nextInt(60)));
				submissionModel.setDateStarted(calendar.getTime());

				submissionModel.setEvaluatorId(inEvaluatorId);
				submissionModel.setEvaluatorFirstName(firstName);
				submissionModel.setEvaluatorLastName(lastName);
				submissionModel.setEvaluationId(evaluationId);

				if (hasBeenEvaluated(status)) {
					calendar.add(Calendar.HOUR, random.nextInt(5));
					submissionModel.setDateCompleted(calendar.getTime());
				}
			}
		}

		submissionModel.setDateUpdated(calendar.getTime());

		return submissionModel;
	}

	public static List<SubmissionModel> getSubmissions(List<String> studentIds, List<String> firstNames,
			List<String> lastNames, List<String> statuses, List<UUID> tasks, List<String> evaluatorIds, int generate) {
		List<SubmissionModel> submissions = new ArrayList<SubmissionModel>();

		for (int i = 0; i < generate; i++) {
			UUID randSubmissionId = UUID.randomUUID();
			String randStudentId = studentIds.get(random.nextInt(studentIds.size()));
			String status = statuses.get(random.nextInt(statuses.size()));
			UUID randTaskId = tasks.get(random.nextInt(tasks.size()));
			String firstName = firstNames.get(random.nextInt(firstNames.size()));
			String lastName = lastNames.get(random.nextInt(lastNames.size()));
			String evalId = evaluatorIds.get(random.nextInt(evaluatorIds.size()));

			SubmissionModel sub = getSubmissionModel(randSubmissionId, status, randTaskId, randStudentId, firstName,
					lastName, evalId);
			submissions.add(sub);
		}

		return submissions;
	}
	
    public static TaskModel getTaskModel() {
        TaskModel taskModel = new TaskModel();
        taskModel.setCourseName("Course 1");
        taskModel.setCourseCode("C1C1");
        taskModel.setCourseId(new Long(123345));
        taskModel.setAssessmentName("Assessment 1");
        taskModel.setAssessmentCode("A1A1");
        taskModel.setAssessmentDate("2015-07-09");
        taskModel.setAssessmentType("Performance");
        taskModel.setAssessmentOrder(1);
        taskModel.setAssessmentId(UUID.randomUUID());
        taskModel.setTaskName("Task Name");
        taskModel.setTaskId(UUID.randomUUID());
        taskModel.setTaskOrder(1);
        taskModel.setAverageTime(34523);
        taskModel.setDescription("Description");
        taskModel.setCompetencies(Collections.emptyList());
        taskModel.setSupportingDocuments(Collections.emptyMap());
        taskModel.setIntroduction("Introduction");
        taskModel.setScenario("Senario");
        taskModel.setNotes("notes");
        taskModel.setRequirements("requirements");
        taskModel.setCRDNotes("CRD Notes");
        taskModel.setRubric(new RubricModel());
        taskModel.setAspectCount(4);
        taskModel.setWebLinks(Collections.emptyList());
        taskModel.setOriginalityMinimum(3);
        taskModel.setOriginalityWarning(5);
        taskModel.setDateCreated(new Date());
        taskModel.setDateUpdated(new Date());
        taskModel.setDatePublished(new Date());
        taskModel.setDateRetired(null);
        taskModel.setPublicationStatus("Published");
        return taskModel;
    }
}
