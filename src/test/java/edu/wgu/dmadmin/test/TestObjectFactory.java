package edu.wgu.dmadmin.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.entity.AssessmentType;
import edu.wgu.dm.entity.publish.AssessmentModel;
import edu.wgu.dm.entity.publish.TaskModel;
import edu.wgu.dm.entity.security.PermissionModel;
import edu.wgu.dm.entity.security.RoleModel;
import edu.wgu.dm.entity.security.UserModel;
import edu.wgu.dm.util.DateUtil;

public class TestObjectFactory {
    static Random random = new Random();

	static String evaluatorLastName = "Wayne";
	static String evaluatorFirstName = "Bruce";
	static String evaluatorId = "evaluator";
	static String employeeId = "employeeId";
	static String studentId = "student";
	static Long submissionId =  random.nextLong();
	static Long assessmentId = random.nextLong();
	static Long evaluationId = random.nextLong();
	static Long taskId = random.nextLong();

    public static UserModel getUserModel() {
        UserModel user = getUserModel(evaluatorFirstName, evaluatorLastName, evaluatorId,
                new ArrayList<RoleModel>(), new ArrayList<PermissionModel>(),
                new ArrayList<TaskModel>(), new ArrayList<String>(), employeeId);
        RoleModel role = getRoleModel("role");
        PermissionModel permission1 = getPermission("test1", "dashboard");
        PermissionModel permission2 = getPermission("test2", "evaluator");
        role.getPermissions().add(permission1);
        role.getPermissions().add(permission2);
        user.getRoles().add(role);
        return user;
    }

	public static UserModel getUserModel(String inUserId, String inEmployeeId) {
		return getUserModel(evaluatorFirstName, evaluatorLastName, inUserId, new ArrayList<RoleModel>(), new ArrayList<PermissionModel>(),
				new ArrayList<TaskModel>(), new ArrayList<String>(), inEmployeeId);
	}

	public static UserModel getUserModel(String firstName, String lastName, String userId, List<RoleModel> roles,
            List<PermissionModel> permissions, List<TaskModel> tasks, List<String> landings,
            String inEmployeeId) {
        UserModel user = new UserModel();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserId(userId);
        user.setRoles(roles);
        user.setTasks(tasks);
        user.setEmployeeId(inEmployeeId);
        return user;
    }

	public static User getUser(String firstName, String lastName, String userId, List<Long> roles,
            List<String> permissions, List<Long> tasks, List<String> landings,
            String inEmployeeId) {
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
		return getRoleModel(role, random.nextLong());
	}

	public static RoleModel getRoleModel(String role, Long roleId) {
		return getRoleModel(role, roleId, new ArrayList<PermissionModel>(), "testing role");
	}

    public static RoleModel getRoleModel(String role, Long roleId,
            List<PermissionModel> permissions, String description) {
        RoleModel model = new RoleModel();
        model.setDateCreated(DateUtil.getZonedNow());
        model.setRole(role);
        model.setPermissions(permissions);
        model.setRoleDescription(description);
        model.setRoleId(roleId);
        return model;
    }

	public static PermissionModel getPermission(String permission, String landing) {
		return getPermissionModel(permission, random.nextLong(), "test permission", landing, "test type");
	}

	public static PermissionModel getPermissionModel(String permission, Long permissionId, String description,
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
	
    public static TaskModel getTaskModel() {
		TaskModel taskModel = new TaskModel();
		AssessmentModel assessment = new AssessmentModel();
		assessment.setAssessmentName("Assessment 1");
		assessment.setAssessmentCode("A1A1");
		assessment.setPamsAssessmentId(new Random().nextLong());
		assessment.setAssessmentType(AssessmentType.P);
        taskModel.setAssessment(assessment);
        taskModel.setTaskName("Task Name");
        taskModel.setTaskId(random.nextLong());
        taskModel.setTaskOrder(1);
        return taskModel;
    }
}
