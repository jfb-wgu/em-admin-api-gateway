package edu.wgu.dm.admin.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.dm.dto.publish.Competency;
import edu.wgu.dm.dto.report.EmaEvaluationAspectRecord;
import edu.wgu.dm.dto.report.EmaTaskRubricRecord;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.entity.publish.CompetencyEntity;
import edu.wgu.dm.entity.report.EvaluationAspectReportEntity;
import edu.wgu.dm.entity.report.TaskRubricReportEntity;
import edu.wgu.dm.entity.security.PermissionEntity;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.repo.ema.CompetencyRepository;
import edu.wgu.dm.repo.ema.EvaluationAspectReportRepository;
import edu.wgu.dm.repo.ema.PermissionRepository;
import edu.wgu.dm.repo.ema.RoleRepository;
import edu.wgu.dm.repo.ema.TaskRubricReportRepository;
import edu.wgu.dm.repo.ema.UserRepository;
import egu.wgu.dm.projection.security.RoleIdProjection;
import egu.wgu.dm.projection.security.UserProjection;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminRepository {

    @Autowired
    CompetencyRepository competencyRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PermissionRepository permissionRepo;

    @Autowired
    TaskRubricReportRepository rubricReportRepo;

    @Autowired
    EvaluationAspectReportRepository aspectReportRepo;

    public List<Competency> getTaskCompetencies(Date datePublished) {
        return CompetencyEntity.toCompetencies(this.competencyRepo.getCompetencies(datePublished));
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date startOfDay, Date endOfDay) {
        return EvaluationAspectReportEntity.toRecords(
                this.aspectReportRepo.findByDateCompletedGreaterThanEqualAndDateCompletedLessThanEqual(startOfDay,
                        endOfDay));
    }

    public List<EmaTaskRubricRecord> getTaskRecords(Date datePublished) {
        return TaskRubricReportEntity.toTasks(this.rubricReportRepo.findByDatePublishedGreaterThanEqual(datePublished));
    }

    /*
     * (non-Javadoc) Role
     */
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
    
    public List<Long> getRolesByPermission(String permission) {
        List<RoleIdProjection> ids = this.roleRepo.findByPermissionsPermission(permission);
        return ids.stream().map(id -> id.getRoleId()).collect(Collectors.toList());
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

    public Optional<UserSummary> getUserWithPermission(String userId, String permission) {
        return UserProjection.toUser(this.userRepo.findByUserIdAndRolesPermissionsPermission(userId, permission));
    }

    public List<UserSummary> getAllUsers() {
        return UserProjection.toUsers(this.userRepo.findAllProjectedBy());
    }

    public List<UserSummary> getUsersByName(String firstName, String lastName) {
        return UserProjection.toUsers(this.userRepo.getUsersByName(firstName, lastName));
    }

    public List<UserSummary> getUsersByTask(Long taskId) {
        return UserProjection.toUsers(this.userRepo.findByTasksTaskId(taskId));
    }

    @Transactional
    public void deleteUser(String userId) {
        this.userRepo.delete(userId);
    }

    /*
     * (non-Javadoc) Permission
     */
    
    @Transactional
    public void savePermissions(List<Permission> permissions) {
        this.permissionRepo.save(permissions.stream()
                                            .map(p -> new PermissionEntity(p))
                                            .collect(Collectors.toList()));
    }

    public Optional<Permission> getPermissionById(Long id) {
        return PermissionEntity.toPermission(this.permissionRepo.findOne(id));
    }
    
    public Optional<Permission> getPermissionByName(String permission) {
        return PermissionEntity.toPermission(this.permissionRepo.findByPermission(permission));
    }

    public List<Permission> getAllPermissions() {
        return PermissionEntity.toPermissions(this.permissionRepo.findAll());
    }
}
