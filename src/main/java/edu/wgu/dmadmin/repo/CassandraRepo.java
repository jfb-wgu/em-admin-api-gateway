package edu.wgu.dmadmin.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.SecurityAccessor;
import edu.wgu.dmadmin.model.user.UserByFirstNameModel;
import edu.wgu.dmadmin.model.user.UserByIdModel;
import edu.wgu.dmadmin.model.user.UserByLastNameModel;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dreammachine.model.audit.ActivityLogByUserModel;
import edu.wgu.dreammachine.model.publish.TaskAccessor;
import edu.wgu.dreammachine.model.publish.TaskByCourseModel;
import edu.wgu.dreammachine.model.submission.SubmissionAccessor;
import edu.wgu.dreammachine.model.submission.SubmissionByEvaluatorAndTaskModel;
import edu.wgu.dreammachine.model.submission.SubmissionByIdModel;
import edu.wgu.dreammachine.model.submission.SubmissionByStatusGroupAndTaskModel;
import edu.wgu.dreammachine.model.submission.SubmissionByStudentAndTaskModel;

@Repository("cassandra")
public class CassandraRepo {

	@Autowired
	Session session;

	MappingManager mappingManager;
	SecurityAccessor securityAccessor;
	Mapper<UserByIdModel> userMapper;
	Mapper<RoleModel> roleMapper;
	Mapper<PermissionModel> permissionMapper;
	Mapper<ActivityLogByUserModel> activityMapper;
	TaskAccessor taskAccessor;
	SubmissionAccessor submissionAccessor;

	@Autowired
	public CassandraRepo(Session session) {
		this.mappingManager = new MappingManager(session);
		this.securityAccessor = this.mappingManager.createAccessor(SecurityAccessor.class);
		this.userMapper = this.mappingManager.mapper(UserByIdModel.class);
		this.permissionMapper = this.mappingManager.mapper(PermissionModel.class);
		this.roleMapper = this.mappingManager.mapper(RoleModel.class);
		this.activityMapper = this.mappingManager.mapper(ActivityLogByUserModel.class);
		this.taskAccessor = this.mappingManager.createAccessor(TaskAccessor.class);
		this.submissionAccessor = this.mappingManager.createAccessor(SubmissionAccessor.class);
	}

	public Optional<UserByIdModel> getUser(String userId) {
		return Optional.ofNullable(this.securityAccessor.getByUserId(userId));
	}

	public UserByIdModel getPermissionsForUser(String userId) {
		this.securityAccessor.updateLastLogin(DateUtil.getZonedNow(), userId);
		return this.securityAccessor.getPermissionsForUser(userId);
	}

	public Optional<UserByIdModel> getUserQualifications(String userId) {
		return Optional.ofNullable(this.securityAccessor.getUserQualifications(userId));
	}

	public List<UserByIdModel> getUsers() {
		return this.securityAccessor.getAll().all();
	}

	public List<UserByIdModel> getUsersById(List<String> userIds) {
		return this.securityAccessor.getUsersById(userIds).all();
	}

	public List<UserByLastNameModel> getUsersByLastName(String name) {
		return this.securityAccessor.getUsersByLastName(name).all();
	}

	public List<UserByFirstNameModel> getUsersByFirstName(String name) {
		return this.securityAccessor.getUsersByFirstName(name).all();
	}

	public List<UserByIdModel> getUsersForRole(UUID roleId) {
		return this.securityAccessor.getUsersForRole(roleId).all();
	}

	public List<UserByIdModel> getUsersForPermission(String permission) {
		return this.securityAccessor.getUsersForPermission(permission).all();
	}

	/***
	 * When saving the user, update the permissions and landing pages as well.
	 *
	 * @param userModel
	 * @return the user with updated permission values.
	 */
	public UserByIdModel saveUser(UserByIdModel userModel) {
		if (!userModel.getRoles().isEmpty()) {
			List<RoleModel> roles = this.getRoles(userModel.getRoles().stream().collect(Collectors.toList()));
			List<PermissionModel> permissions = this.getPermissions(roles
					.stream()
					.map(role -> role.getPermissions())
					.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
				);

			userModel.setPermissions(permissions.stream().map(perm -> perm.getPermission()).collect(Collectors.toSet()));
			userModel.setLandings(permissions.stream().map(perm -> perm.getLanding()).collect(Collectors.toSet()));
		} else {
			userModel.setPermissions(Collections.emptySet());
			userModel.setLandings(Collections.emptySet());
		}

		this.userMapper.save(userModel);
		return userModel;
	}

	public void saveUsers(List<UserByIdModel> userModels) {
		userModels.forEach(user -> {
			this.saveUser(user);
		});
	}

	public void deleteUser(String userId) {
		this.userMapper.delete(userId);
	}

	public void saveActivityLogEntry(ActivityLogByUserModel model) {
		this.activityMapper.save(model);
	}

	public void savePermission(PermissionModel model) {
		model.setDateUpdated(DateUtil.getZonedNow());
		this.permissionMapper.save(model);
	}

	public List<PermissionModel> getPermissions() {
		return this.securityAccessor.getPermissions().all();
	}

	public List<PermissionModel> getPermissions(List<UUID> permissionIds) {
		return this.securityAccessor.getPermissions(permissionIds).all();
	}

	public Optional<PermissionModel> getPermission(UUID permissionId) {
		return Optional.ofNullable(this.securityAccessor.getPermission(permissionId));
	}

	public void saveRole(RoleModel model) {
		model.setDateUpdated(DateUtil.getZonedNow());
		this.roleMapper.save(model);
	}

	public void deleteRole(UUID roleId) {
		this.securityAccessor.deleteRole(roleId);
	}

	public void deletePermission(UUID permissionId, String permission) {
		this.securityAccessor.deletePermission(permissionId, permission);
	}

	public Optional<RoleModel> getRole(UUID roleId) {
		return Optional.ofNullable(this.securityAccessor.getRole(roleId));
	}

	public List<RoleModel> getRoles() {
		return this.securityAccessor.getRoles().all();
	}

	public List<RoleModel> getRoles(List<UUID> roleIds) {
		return this.securityAccessor.getRoles(roleIds).all();
	}
	
	public Optional<SubmissionByIdModel> getSubmissionById(UUID submissionId) {
		return Optional.ofNullable(this.submissionAccessor.getSubmissionById(submissionId));
	}
	
	public List<SubmissionByStudentAndTaskModel> getSubmissionByStudentByTasks(String studentId, List<UUID> taskIds) {
		return this.submissionAccessor.getSubmissionsByStudentAndTasks(studentId, taskIds).all();
	}
	
	public List<SubmissionByStudentAndTaskModel> getSubmissionsByStudentId(String studentId) {
		return this.submissionAccessor.getSubmissionsByStudent(studentId).all();
	}
	
	public List<SubmissionByStatusGroupAndTaskModel> getSubmissionsByStatusGroupAndTasks(String statusGroup, List<UUID> taskIds) {
		return this.submissionAccessor.getSubmissionsByStatusGroupAndTasks(statusGroup, taskIds).all();
	}
	
	public List<SubmissionByStatusGroupAndTaskModel> getSubmissionsByStatusGroup(String statusGroup) {
		return this.submissionAccessor.getSubmissionsByStatusGroup(statusGroup).all();
	}
	
	public List<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluatorsAndTasks(List<String> evaluatorIds, List<UUID> taskIds) {
		return this.submissionAccessor.getSubmissionsByEvaluatorAndTasks(evaluatorIds, taskIds).all();
	}
	
	public List<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluators(List<String> evaluatorIds) {
		return this.submissionAccessor.getSubmissionsByEvaluators(evaluatorIds).all();
	}
	
	public List<TaskByCourseModel> getTaskBasics() {
		return this.taskAccessor.getAllBasics().all();
	}
}
