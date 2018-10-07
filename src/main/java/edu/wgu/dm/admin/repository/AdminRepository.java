package edu.wgu.dm.admin.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.dm.common.enums.EvaluationStatus;
import edu.wgu.dm.dto.admin.report.EmaEvaluationAspectRecord;
import edu.wgu.dm.dto.admin.report.EmaTaskRubricRecord;
import edu.wgu.dm.dto.publish.Competency;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
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
import lombok.experimental.FieldDefaults;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminRepository {

    @Autowired
    CompetencyRepository competencyRepo;

    @Autowired
    EvaluationRepository evaluationRepo;

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PermissionRepository permissionRepo;

    public List<Competency> getTaskCompetencies(Date datePublished) {
        return this.competencyRepo.getCompetencies(datePublished)
                                  .stream()
                                  .map(entity -> DtoCreationHelper.toCompetency(entity))
                                  .collect(Collectors.toList());
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date startOfDay, Date endOfDay) {
        List<EmaEvaluationAspectRecord> aspectRecords = new ArrayList<>();
        List<EvaluationEntity> list =
                this.evaluationRepo.findByDateCompletedGreaterThanEqualAndDateCompletedLessThanEqualAndStatus(
                        startOfDay, endOfDay, EvaluationStatus.COMPLETED);
        list.forEach(eval -> {
            eval.getEvaluationAspects()
                .forEach(aspect -> {
                    aspectRecords.add(DtoCreationHelper.toEmaEvaluationAspectRecord(aspect, eval));
                });
        });
        return aspectRecords;
    }

    public List<EmaTaskRubricRecord> getTaskRecords(Date datePublished) {
        List<EmaTaskRubricRecord> taskRecords = new ArrayList<>();
        List<TaskEntity> list = this.taskRepo.findByAssessmentDatePublishedGreaterThanEqual(datePublished);

        list.stream()
            .forEach(task -> {
                task.getAspects()
                    .forEach(aspect -> {
                        aspect.getAnchors()
                              .forEach(anchor -> {
                                  taskRecords.add(DtoCreationHelper.toEmaTaskRubricRecord(anchor, aspect, task));
                              });
                    });
            });
        return taskRecords;
    }

    /*
     * (non-Javadoc) Role
     */
    @Transactional
    public Optional<Role> saveRole(Role role) {
        return RoleEntity.toRole(this.roleRepo.saveAndFlush(new RoleEntity(role)));
    }

    @Transactional
    public List<Role> saveRoles(List<Role> roles) {
        List<RoleEntity> entities = this.roleRepo.save(roles.stream()
                                                            .map(r -> new RoleEntity(r))
                                                            .collect(Collectors.toList()));
        return RoleEntity.toRoles(entities);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        this.roleRepo.delete(roleId);
    }

    public Optional<Role> getRoleById(Long roleId) {
        return RoleEntity.toRole(this.roleRepo.findOne(roleId));
    }

    public List<Role> getAllRoles() {
        return RoleEntity.toRoles(this.roleRepo.findAll());
    }

    /*
     * (non-Javadoc) User
     */

    @Transactional
    public Optional<User> saveUser(User user) {
        return UserEntity.toUser(this.userRepo.saveAndFlush(new UserEntity(user)));
    }

    @Transactional
    public List<User> saveUsers(List<User> users) {
        List<UserEntity> entities = this.userRepo.save(users.stream()
                                                            .map(u -> new UserEntity(u))
                                                            .collect(Collectors.toList()));
        return UserEntity.toUsers(entities);
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

    @Transactional
    public void updateLastLogin(String userId) {
        this.userRepo.updateLastLogin(userId);
    }

    @Transactional
    public void deleteUser(String userId) {
        this.userRepo.delete(userId);
    }

    /*
     * (non-Javadoc) Permission
     */
    @Transactional
    public Long savePermission(Permission permission) {
        return this.permissionRepo.saveAndFlush(new PermissionEntity(permission))
                                  .getPermissionId();
    }

    @Transactional
    public void savePermissions(List<Permission> permissions) {
        this.permissionRepo.save(permissions.stream()
                                            .map(p -> new PermissionEntity(p))
                                            .collect(Collectors.toList()));
    }

    @Transactional
    public void deletePermission(Long permissionId) {
        this.permissionRepo.delete(permissionId);
    }

    public Optional<Permission> getPermissionById(Long id) {
        return PermissionEntity.toPermission(this.permissionRepo.findOne(id));
    }

    public List<Permission> getAllPermissions() {
        return PermissionEntity.toPermissions(this.permissionRepo.findAll());
    }
}
