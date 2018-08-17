package edu.wgu.dmadmin.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.entity.AssessmentType;
import edu.wgu.dm.entity.publish.AssessmentEntity;
import edu.wgu.dm.entity.publish.TaskEntity;
import edu.wgu.dm.entity.security.PermissionEntity;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.entity.security.UserEntity;
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

    public static UserEntity getUserModel() {
        UserEntity user = getUserModel(evaluatorFirstName, evaluatorLastName, evaluatorId,
                new ArrayList<RoleEntity>(), new ArrayList<PermissionEntity>(),
                new ArrayList<TaskEntity>(), new ArrayList<String>(), employeeId);
        RoleEntity role = getRoleModel("role");
        PermissionEntity permission1 = getPermission("test1", "dashboard");
        PermissionEntity permission2 = getPermission("test2", "evaluator");
        role.getPermissions().add(permission1);
        role.getPermissions().add(permission2);
        user.getRoles().add(role);
        return user;
    }

	public static UserEntity getUserModel(String inUserId, String inEmployeeId) {
		return getUserModel(evaluatorFirstName, evaluatorLastName, inUserId, new ArrayList<RoleEntity>(), new ArrayList<PermissionEntity>(),
				new ArrayList<TaskEntity>(), new ArrayList<String>(), inEmployeeId);
	}

	public static UserEntity getUserModel(String firstName, String lastName, String userId, List<RoleEntity> roles,
            List<PermissionEntity> permissions, List<TaskEntity> tasks, List<String> landings,
            String inEmployeeId) {
        UserEntity user = new UserEntity();
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

	public static RoleEntity getRoleModel(String role) {
		return getRoleModel(role, random.nextLong());
	}

	public static RoleEntity getRoleModel(String role, Long roleId) {
		return getRoleModel(role, roleId, new ArrayList<PermissionEntity>(), "testing role");
	}

    public static RoleEntity getRoleModel(String role, Long roleId,
            List<PermissionEntity> permissions, String description) {
        RoleEntity model = new RoleEntity();
        model.setDateCreated(DateUtil.getZonedNow());
        model.setRole(role);
        model.setPermissions(permissions);
        model.setRoleDescription(description);
        model.setRoleId(roleId);
        return model;
    }

	public static PermissionEntity getPermission(String permission, String landing) {
		return getPermissionModel(permission, random.nextLong(), "test permission", landing, "test type");
	}

	public static PermissionEntity getPermissionModel(String permission, Long permissionId, String description,
            String landing, String type) {
        PermissionEntity model = new PermissionEntity();
        model.setDateCreated(DateUtil.getZonedNow());
        model.setPermission(permission);
        model.setLanding(landing);
        model.setPermissionDescription(description);
        model.setPermissionId(permissionId);
        model.setPermissionType(type);
        return model;
    }
	
    public static TaskEntity getTaskModel() {
		TaskEntity taskModel = new TaskEntity();
		AssessmentEntity assessment = new AssessmentEntity();
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
