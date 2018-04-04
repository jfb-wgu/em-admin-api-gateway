package edu.wgu.dmadmin.test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.model.publish.EMATaskModel;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.util.DateUtil;

public class TestObjectFactory {

	static String evaluatorLastName = "Wayne";
	static String evaluatorFirstName = "Bruce";
	static String studentId = "student";
	static UUID submissionId = UUID.randomUUID();
	static String evaluatorId = "evaluator";
	static String employeeId = "employeeId";
	static UUID assessmentId = UUID.randomUUID();
	static Random random = new Random();
	static UUID evaluationId = UUID.randomUUID();
	static UUID taskId = UUID.randomUUID();

	public static UserModel getUserModel() {
		UserModel user = getUserModel(evaluatorFirstName, evaluatorLastName, evaluatorId, new HashSet<UUID>(),
				new HashSet<String>(), new HashSet<UUID>(), new HashSet<String>(), employeeId);

		user.getRoles().add(UUID.randomUUID());
		user.getLandings().add("dashboard");
		user.getLandings().add("evaluator");
		user.getPermissions().add("test1");
		user.getPermissions().add("test2");

		return user;
	}

	public static UserModel getUserModel(String inUserId, String inEmployeeId) {
		return getUserModel(evaluatorFirstName, evaluatorLastName, inUserId, new HashSet<UUID>(), new HashSet<String>(),
				new HashSet<UUID>(), new HashSet<String>(), inEmployeeId);
	}

	public static UserModel getUserModel(String firstName, String lastName, String userId, Set<UUID> roles,
			Set<String> permissions, Set<UUID> tasks, Set<String> landings, String inEmployeeId) {
		UserModel user = new UserModel();
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
	
    public static EMATaskModel getTaskModel() {
		EMATaskModel taskModel = new EMATaskModel();
        taskModel.setCourseId(new Long(123345));
        taskModel.setAssessmentName("Assessment 1");
        taskModel.setAssessmentCode("A1A1");
        taskModel.setAssessmentId(UUID.randomUUID());
        taskModel.setTaskName("Task Name");
        taskModel.setTaskId(UUID.randomUUID());
        taskModel.setTaskOrder(1);
        return taskModel;
    }
}
