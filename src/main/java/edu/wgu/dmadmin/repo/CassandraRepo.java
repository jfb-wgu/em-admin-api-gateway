package edu.wgu.dmadmin.repo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import edu.wgu.common.exception.AuthorizationException;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.RequestBean;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.model.audit.ActivityLogByUserModel;
import edu.wgu.dmadmin.model.audit.StatusLogAccessor;
import edu.wgu.dmadmin.model.audit.StatusLogModel;
import edu.wgu.dmadmin.model.publish.EMATaskModel;
import edu.wgu.dmadmin.model.publish.TaskAccessor;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.SecurityAccessor;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.SubmissionAccessor;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.DateUtil;

@Repository("cassandra")
public class CassandraRepo {

	@Autowired
	Session session;

	@Autowired
	private RequestBean rBean;

	MappingManager mappingManager;
	SecurityAccessor securityAccessor;
	TaskAccessor taskAccessor;
	SubmissionAccessor submissionAccessor;
	StatusLogAccessor statusAccessor;
	Mapper<UserModel> userMapper;
	Mapper<RoleModel> roleMapper;
	Mapper<PermissionModel> permissionMapper;
	Mapper<ActivityLogByUserModel> activityMapper;

	@Autowired
	public CassandraRepo(Session session) {
		this.mappingManager = new MappingManager(session);
		this.securityAccessor = this.mappingManager.createAccessor(SecurityAccessor.class);
		this.taskAccessor = this.mappingManager.createAccessor(TaskAccessor.class);
		this.submissionAccessor = this.mappingManager.createAccessor(SubmissionAccessor.class);
		this.statusAccessor = this.mappingManager.createAccessor(StatusLogAccessor.class);
		this.userMapper = this.mappingManager.mapper(UserModel.class);
		this.permissionMapper = this.mappingManager.mapper(PermissionModel.class);
		this.roleMapper = this.mappingManager.mapper(RoleModel.class);
		this.activityMapper = this.mappingManager.mapper(ActivityLogByUserModel.class);

		this.userMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
		this.roleMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
		this.permissionMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
		this.activityMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
	}

	public Optional<UserModel> getUserModel(String userId) {
		if (this.rBean.getUser() != null && this.rBean.getUser().getUserId().equals(userId)) {
			return Optional.of(this.rBean.getUser());
		}

		return Optional.ofNullable(this.securityAccessor.getByUserId(userId));
	}

	public void updateLastLogin(String userId) {
		this.securityAccessor.updateLastLogin(DateUtil.getZonedNow(), userId);
	}

	public List<UserModel> getUsers() {
		return this.securityAccessor.getAll().all();
	}

	public List<UserModel> getUsersById(List<String> userIds) {
		return this.securityAccessor.getUsersById(userIds).all();
	}

	public List<UserModel> getUsersByLastName(String name) {
		return this.securityAccessor.getUsersByLastName(name).all();
	}

	public List<UserModel> getUsersByFirstName(String name) {
		return this.securityAccessor.getUsersByFirstName(name).all();
	}

	public List<UserModel> getUsersForRole(UUID roleId) {
		return this.securityAccessor.getUsersForRole(roleId).all();
	}

	public List<UserModel> getUsersForPermission(String permission) {
		return this.securityAccessor.getUsersForPermission(permission).all();
	}

	public UserModel saveUser(UserModel userModel) {
		this.userMapper.save(userModel);
		return userModel;
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

	public Optional<RoleModel> getRole(UUID roleId) {
		return Optional.ofNullable(this.securityAccessor.getRole(roleId));
	}

	public List<RoleModel> getRoles() {
		return this.securityAccessor.getRoles().all();
	}

	public List<RoleModel> getRoles(List<UUID> roleIds) {
		return this.securityAccessor.getRoles(roleIds).all();
	}

	public List<EMATaskModel> getTaskBasics() {
		return this.taskAccessor.getAllBasics().all();
	}

	public List<StatusLogModel> getAssessmentStatus(List<Long> assessmentIds) {
		List<StatusLogModel> models = new ArrayList<>();
		
		assessmentIds.forEach(id -> {
			models.addAll(this.statusAccessor.getAssessmentStatus(id).all());
		});
		
		return models;
	}

	public List<StatusLogModel> getAssessmentStatus(Date activityDate) {
		return this.statusAccessor.getAssessmentStatusByDate(activityDate).all();
	}

	public List<EMATaskModel> getBasicTasksByAssessment(Long assessmentId) {
		return this.taskAccessor.getBasicTasksByAssessment(assessmentId).all();
	}

	public Optional<SubmissionModel> getLastSubmissionForTask(String studentId, UUID taskId) {
		return Optional.ofNullable(this.submissionAccessor.getLastSubmissionByStudentAndTask(studentId, taskId));
	}

	public Optional<StatusLogModel> getLastStatus(String studentId, UUID taskId) {
		return Optional.ofNullable(this.statusAccessor.getLastStatusEntry(studentId, taskId));
	}

	public List<User> saveUsers(String userId, List<User> users, boolean checkSystem) {
		List<UserModel> models = users.stream().map(u -> new UserModel(u)).collect(Collectors.toList());
		return this.saveUsers(models, userId, checkSystem);
	}

	public List<User> saveUsers(List<UserModel> users) {
		return this.saveUsers(users, null, false);
	}

	public List<User> saveUsers(List<UserModel> users, String userId, boolean checkSystem) {
		List<User> created = new ArrayList<>();

		Map<UUID, RoleModel> roles = this.getRoleMap(users);
		Map<UUID, PermissionModel> permissions = this.getPermissionMap(roles.values());

		if (checkSystem)
			checkSystemUser(permissions.values(), userId);

		users.forEach(user -> {
			user.setPermissions(new HashSet<>());
			user.setLandings(new HashSet<>());

			user.getRoles().forEach(role -> {
				RoleModel model = roles.get(role);
				model.getPermissions().forEach(perm -> {
					PermissionModel permission = permissions.get(perm);
					user.getPermissions().add(permission.getPermission());
					user.getLandings().add(permission.getLanding());
				});
			});

			created.add(new User(this.saveUser(user)));
		});

		return created;
	}

	private void checkSystemUser(Collection<PermissionModel> permissions, String userId) {
		if (permissions.stream().filter(p -> p.getPermission().equals(Permissions.SYSTEM)).count() > 0) {
			UserModel user = this.getUserModel(userId).orElseThrow(() -> new UserNotFoundException(userId));
			if (!user.getPermissions().contains(Permissions.SYSTEM))
				throw new AuthorizationException("Only SYSTEM users can assign SYSTEM permissions");
		}
	}

	public Map<UUID, RoleModel> getRoleMap() {
		Map<UUID, RoleModel> roles = this.getRoles().stream().collect(Collectors.toMap(r -> r.getRoleId(), r -> r));
		return roles;
	}

	public Map<UUID, RoleModel> getRoleMap(Collection<UserModel> users) {
		List<UUID> roleIds = users.stream().map(user -> user.getRoles()).collect(ArrayList::new, ArrayList::addAll,
				ArrayList::addAll);
		return this.getRoleMap(roleIds);
	}

	public Map<UUID, RoleModel> getRoleMap(List<UUID> ids) {
		Map<UUID, RoleModel> roles = this.getRoles(ids).stream().collect(Collectors.toMap(r -> r.getRoleId(), r -> r));
		return roles;
	}

	public Map<UUID, PermissionModel> getPermissionMap() {
		Map<UUID, PermissionModel> permissions = this.getPermissions().stream()
				.collect(Collectors.toMap(p -> p.getPermissionId(), p -> p));
		return permissions;
	}

	public Map<UUID, PermissionModel> getPermissionMap(Collection<RoleModel> roles) {
		List<UUID> permissionIds = roles.stream().map(role -> role.getPermissions()).collect(ArrayList::new,
				ArrayList::addAll, ArrayList::addAll);
		Map<UUID, PermissionModel> permissions = this.getPermissions(permissionIds).stream()
				.collect(Collectors.toMap(p -> p.getPermissionId(), p -> p));
		return permissions;
	}

	public Map<UUID, EMATaskModel> getTaskMap() {
		Map<UUID, EMATaskModel> tasks = this.getTaskBasics().stream()
				.collect(Collectors.toMap(t -> t.getTaskId(), t -> t));
		return tasks;
	}
}
