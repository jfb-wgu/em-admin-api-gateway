package edu.wgu.dm.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.projection.security.UserProjection;
import edu.wgu.dm.repo.security.UserRepository;
import edu.wgu.dm.security.strategy.RequestBean;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepo {

    @Autowired
    UserRepository userRepo;

    @Autowired
    RequestBean requestBean;
    
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
        if (this.requestBean != null && this.requestBean.getUser() != null && this.requestBean.getUser()
                                                                                              .getUserId()
                                                                                              .equals(userId)) {
            return Optional.of(this.requestBean.getUser());
        }

        return UserEntity.toUser(this.userRepo.findOne(userId));
    }

    public Optional<UserSummary> getUserWithPermission(String userId, String permission) {
        return UserProjection.toUser(this.userRepo.findByUserIdAndRolesPermissionsPermission(userId, permission));
    }

    public List<UserSummary> getAllUsers() {
        return UserProjection.toUsers(this.userRepo.findAllProjectedBy());
    }

    public List<UserSummary> getUsersByTask(Long taskId) {
        return UserProjection.toUsers(this.userRepo.findByTasksTaskId(taskId));
    }

    @Transactional
    public void deleteUser(String userId) {
        this.userRepo.delete(userId);
    }
}
