package edu.wgu.dm.admin.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import edu.wgu.dm.dto.admin.report.EmaEvaluationAspectRecord;
import edu.wgu.dm.dto.admin.report.EmaTaskRubricRecord;
import edu.wgu.dm.dto.publish.Competency;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.entity.EvaluationStatus;
import edu.wgu.dm.entity.converter.DtoCreationHelper;
import edu.wgu.dm.entity.evaluation.EvaluationEntity;
import edu.wgu.dm.entity.publish.TaskEntity;
import edu.wgu.dm.entity.security.PermissionEntity;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.repo.ema.CompetencyRepository;
import edu.wgu.dm.repo.ema.EvaluationRepository;
import edu.wgu.dm.repo.ema.PermissionRepository;
import edu.wgu.dm.repo.ema.RoleRepository;
import edu.wgu.dm.repo.ema.TaskRepository;
import edu.wgu.dm.repo.ema.UserRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminRepository {

    @Autowired
    private CompetencyRepository competencyRepo;

    @Autowired
    private EvaluationRepository evaluationRepo;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PermissionRepository permissionRepo;

    @Autowired
    private EntityManager entityManager;

    public List<Competency> getTaskCompetencies(Date datePublished) {
        return competencyRepo.getCompetencies(datePublished).stream()
                .map(entity -> DtoCreationHelper.toCompetency(entity)).collect(Collectors.toList());
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date startOfDay, Date endOfDay) {
        List<EmaEvaluationAspectRecord> aspectRecords = new ArrayList<>();
        List<EvaluationEntity> list = evaluationRepo
                .findByDateCompletedGreaterThanEqualAndDateCompletedLessThanEqualAndStatus(
                        startOfDay, endOfDay, EvaluationStatus.COMPLETED);
        list.forEach(eval -> {
            eval.getEvaluationAspects().forEach(aspect -> {
                aspectRecords.add(DtoCreationHelper.toEmaEvaluationAspectRecord(aspect, eval));
            });
        });
        return aspectRecords;
    }

    public List<EmaTaskRubricRecord> getTaskRecords(Date datePublished) {
        List<EmaTaskRubricRecord> taskRecords = new ArrayList<>();
        List<TaskEntity> list =
                taskRepo.findByAssessmentDatePublishedGreaterThanEqual(datePublished);

        list.stream().forEach(task -> {
            task.getAspects().forEach(aspect -> {
                aspect.getAnchors().forEach(anchor -> {
                    taskRecords.add(DtoCreationHelper.toEmaTaskRubricRecord(anchor, aspect, task));
                });
            });
        });
        return taskRecords;
    }

    /*
     * (non-Javadoc) Role
     */
    public Long saveRole(Role role) {
        return this.roleRepo.save(new RoleEntity(role)).getRoleId();
    }

    public void deleteRole(long roleId) {
        this.roleRepo.delete(roleId);
    }

    public Optional<Role> getRoleById(long roleId) {
        return RoleEntity.toRole(this.roleRepo.findOne(roleId));
    }

    public List<Role> getAllRoles() {
        return RoleEntity.toRoles(this.roleRepo.findAll());
    }

    public Optional<Role> getRolesByRole(String role) {
        return RoleEntity.toRole(this.roleRepo.findByRole(role));
    }

    /*
     * (non-Javadoc) User
     */

    public Optional<User> saveUser(User user) {
        return UserEntity.toUser(this.userRepo.save(new UserEntity(user)));
    }

    public void saveUsers(List<User> users) {
        List<UserEntity> usersEntity = new ArrayList<>();
        users.forEach(user -> {
            usersEntity.add(new UserEntity(user));
        });
        this.userRepo.save(usersEntity);
    }

    public Optional<User> getUserById(String userId) {
        return UserEntity.toUser(this.userRepo.findOne(userId));
    }

    public List<User> getAllUsers() {
        return UserEntity.toUsers(this.userRepo.findAll());
    }

    public List<User> getUsersByName(String firstName, String lastName) {
        return UserEntity.toUsers(this.userRepo.getUsersByName(firstName, lastName));
    }

    public List<User> getUsersByTask(Long taskId) {
        return UserEntity.toUsers(this.userRepo.findByTasksTaskId(taskId));
    }

    public List<User> getUsersByRole(Long roleId) {
        return UserEntity.toUsers(this.userRepo.findByRolesRoleId(roleId));
    }

    public void updateLastLogin(String userId) {
        this.userRepo.updateLastLogin(userId);
    }

    public void deleteUser(String userId) {
        this.userRepo.delete(userId);
    }

    public Long savePermission(Permission permission) {
        return this.permissionRepo.save(new PermissionEntity(permission)).getPermissionId();
    }

    public void deletePermission(long permissionId) {
        this.permissionRepo.delete(permissionId);
    }

    public Optional<Permission> getPermissionById(long id) {
        return PermissionEntity.toPermission(this.permissionRepo.findOne(id));
    }

    public List<Permission> getAllPermissions() {
        return PermissionEntity.toPermissions(this.permissionRepo.findAll());
    }

    public Optional<Permission> getPermissionByPermission(String permission) {
        return PermissionEntity.toPermission(this.permissionRepo.findByPermission(permission));
    }

    public Map<Long, Permission> getPermissionMap() {
        List<Permission> permissionList = getAllPermissions();
        Map<Long, Permission> permissions = new HashMap<>();
        permissionList.forEach(permission -> {
            permissions.put(permission.getPermissionId(), permission);
        });
        return permissions;
    }

    public Role saveOrUpdateRole(@NonNull Role role) {
        // We are expecting PermissionIds of the existing permissions
        RoleEntity roleEntity = entityManager.merge(new RoleEntity(role));
        return roleEntity.toRole();
    }

    public List<RoleEntity> findAllRoles(@NonNull List<Long> roleIds) {
        return roleRepo.findAll(roleIds);
    }


}
