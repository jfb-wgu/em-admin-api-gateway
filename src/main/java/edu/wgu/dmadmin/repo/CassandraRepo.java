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
import edu.wgu.dmadmin.domain.security.RequestBean;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dreammachine.domain.security.Permissions;
import edu.wgu.dreammachine.domain.security.User;
import edu.wgu.dreammachine.model.audit.ActivityLogByUserModel;
import edu.wgu.dreammachine.model.audit.StatusLogByAssessmentModel;
import edu.wgu.dreammachine.model.audit.StatusLogByStudentModel;
import edu.wgu.dreammachine.model.evaluation.EvaluationAccessor;
import edu.wgu.dreammachine.model.evaluation.EvaluationByIdModel;
import edu.wgu.dreammachine.model.publish.TaskAccessor;
import edu.wgu.dreammachine.model.publish.TaskByAssessmentModel;
import edu.wgu.dreammachine.model.publish.TaskByCourseModel;
import edu.wgu.dreammachine.model.publish.TaskModel;
import edu.wgu.dreammachine.model.security.PermissionModel;
import edu.wgu.dreammachine.model.security.RoleModel;
import edu.wgu.dreammachine.model.security.SecurityAccessor;
import edu.wgu.dreammachine.model.security.UserByFirstNameModel;
import edu.wgu.dreammachine.model.security.UserByIdModel;
import edu.wgu.dreammachine.model.security.UserByLastNameModel;
import edu.wgu.dreammachine.model.submission.SubmissionAccessor;
import edu.wgu.dreammachine.model.submission.SubmissionByIdModel;
import edu.wgu.dreammachine.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dreammachine.util.DateUtil;

@Repository("cassandra")
public class CassandraRepo {

	@Autowired
	Session session;

	@Autowired
	private RequestBean rBean;

	MappingManager mappingManager;
	SecurityAccessor securityAccessor;
	EvaluationAccessor evaluationAccessor;
	TaskAccessor taskAccessor;
	SubmissionAccessor submissionAccessor;
	CassandraAccessor cassandraAccessor;
	Mapper<UserByIdModel> userMapper;
	Mapper<RoleModel> roleMapper;
	Mapper<PermissionModel> permissionMapper;
	Mapper<ActivityLogByUserModel> activityMapper;
	Mapper<SubmissionByIdModel> submissionMapper;
	Mapper<EvaluationByIdModel> evaluationMapper;

	@Autowired
	public CassandraRepo(Session session) {
		this.mappingManager = new MappingManager(session);
		this.securityAccessor = this.mappingManager.createAccessor(SecurityAccessor.class);
		this.evaluationAccessor = this.mappingManager.createAccessor(EvaluationAccessor.class);
		this.taskAccessor = this.mappingManager.createAccessor(TaskAccessor.class);
		this.submissionAccessor = this.mappingManager.createAccessor(SubmissionAccessor.class);
		this.cassandraAccessor = this.mappingManager.createAccessor(CassandraAccessor.class);
		this.userMapper = this.mappingManager.mapper(UserByIdModel.class);
		this.permissionMapper = this.mappingManager.mapper(PermissionModel.class);
		this.roleMapper = this.mappingManager.mapper(RoleModel.class);
		this.activityMapper = this.mappingManager.mapper(ActivityLogByUserModel.class);
		this.submissionMapper = this.mappingManager.mapper(SubmissionByIdModel.class);
		this.evaluationMapper = this.mappingManager.mapper(EvaluationByIdModel.class);

		this.userMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
		this.roleMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
		this.permissionMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
		this.activityMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
		this.submissionMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
		this.evaluationMapper.setDefaultSaveOptions(Mapper.Option.saveNullFields(false));
	}

	public Optional<UserByIdModel> getUserModel(String userId) {
		if (this.rBean.getUser() != null && this.rBean.getUser().getUserId().equals(userId)) {
			return Optional.of(this.rBean.getUser());
		}

		return Optional.ofNullable(this.securityAccessor.getByUserId(userId));
	}

	public void updateLastLogin(String userId) {
		this.securityAccessor.updateLastLogin(DateUtil.getZonedNow(), userId);
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

	public UserByIdModel saveUser(UserByIdModel userModel) {
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

	public List<TaskByCourseModel> getTaskBasics() {
		return this.taskAccessor.getAllBasics().all();
	}

	public List<StatusLogByAssessmentModel> getAssessmentStatus(List<UUID> assessmentIds) {
		return this.cassandraAccessor.getAssessmentStatus(assessmentIds).all();
	}

	public List<StatusLogByAssessmentModel> getAssessmentStatus(Date activityDate) {
		return this.cassandraAccessor.getAssessmentStatusByDate(activityDate).all();
	}

	public List<TaskByAssessmentModel> getBasicTasksByAssessment(UUID assessmentId) {
		return this.cassandraAccessor.getBasicTasksByAssessment(assessmentId).all();
	}

	public Optional<SubmissionByStudentAndTaskModel> getLastSubmissionForTask(String studentId, UUID taskId) {
		return Optional.ofNullable(this.cassandraAccessor.getLastSubmissionByStudentAndTask(studentId, taskId));
	}

	public Optional<StatusLogByStudentModel> getLastStatus(String studentId, UUID taskId) {
		return Optional.ofNullable(this.cassandraAccessor.getLastStatusEntry(studentId, taskId));
	}
	
	public List<User> saveUsers(String userId, List<User> users, boolean checkSystem, boolean clean) {
		List<UserByIdModel> models = users.stream().map(u -> new UserByIdModel(u)).collect(Collectors.toList());
		return this.saveUsers(models, userId, checkSystem, clean);
	}
	
	public List<User> saveUsers(List<UserByIdModel> users) {
		return this.saveUsers(users, null, false, true);
	}

	public List<User> saveUsers(List<UserByIdModel> users, String userId, boolean checkSystem, boolean clean) {
		List<User> created = new ArrayList<>();

		Map<UUID, RoleModel> roles = this.getRoleMap(users);
		Map<UUID, PermissionModel> permissions = this.getPermissionMap(roles.values());

		boolean system = permissions.values().stream().filter(p -> p.getPermission().equals(Permissions.SYSTEM))
				.count() > 0;

		if (system && checkSystem)
			checkSystemUser(userId);

		users.forEach(user -> {
			if (clean) {
				user.setPermissions(new HashSet<>());
				user.setLandings(new HashSet<>());
			}

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

	private void checkSystemUser(String userId) {
		UserByIdModel user = this.getUserModel(userId).orElseThrow(() -> new UserNotFoundException(userId));
		if (!user.getPermissions().contains(Permissions.SYSTEM))
			throw new AuthorizationException("Only SYSTEM users can assign SYSTEM permissions");
	}

	public Map<UUID, RoleModel> getRoleMap() {
		Map<UUID, RoleModel> roles = this.getRoles().stream().collect(Collectors.toMap(r -> r.getRoleId(), r -> r));
		return roles;
	}

	public Map<UUID, RoleModel> getRoleMap(Collection<UserByIdModel> users) {
		List<UUID> roleIds = users.stream().map(user -> user.getRoles()).collect(ArrayList::new, ArrayList::addAll,
				ArrayList::addAll);
		return this.getRoleMap(roleIds);
	}

	public Map<UUID, RoleModel> getRoleMap(List<UUID> ids) {
		Map<UUID, RoleModel> roles = this.getRoles(ids).stream().collect(Collectors.toMap(r -> r.getRoleId(), r -> r));
		return roles;
	}

	public Map<UUID, PermissionModel> getPermissionMap(Collection<RoleModel> roles) {
		List<UUID> permissionIds = roles.stream().map(role -> role.getPermissions()).collect(ArrayList::new,
				ArrayList::addAll, ArrayList::addAll);
		Map<UUID, PermissionModel> permissions = this.getPermissions(permissionIds).stream()
				.collect(Collectors.toMap(p -> p.getPermissionId(), p -> p));
		return permissions;
	}

	public Map<UUID, TaskModel> getTaskMap() {
		Map<UUID, TaskModel> tasks = this.getTaskBasics().stream()
				.collect(Collectors.toMap(t -> t.getTaskId(), t -> t));
		return tasks;
	}
}
