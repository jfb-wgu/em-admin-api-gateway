package edu.wgu.dmadmin.test;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.publish.TaskByCourseModel;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.DateUtil;

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
	
    public static TaskByCourseModel getTaskModel() {
        TaskByCourseModel taskModel = new TaskByCourseModel();
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
