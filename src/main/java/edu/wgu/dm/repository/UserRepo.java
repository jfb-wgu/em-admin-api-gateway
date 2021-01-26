package edu.wgu.dm.repository;

import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.entity.projection.security.UserProjection;
import edu.wgu.dm.entity.security.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepo {

    private final UserRepository userRepository;
    private final SecurityRepo secRepo;

    public UserRepo(UserRepository userRepository, SecurityRepo secRepo) {
        this.userRepository = userRepository;
        this.secRepo = secRepo;
    }

    @Transactional
    public Optional<User> saveUser(User user) {
        return UserEntity.toUser(this.userRepository.saveAndFlush(new UserEntity(user)));
    }

    public Optional<User> getUserById(String userId) {
        return this.secRepo.getUserById(userId);
    }

    public int checkIfUserHasPermission(String userId, String permission) {
        return userRepository.countByUserIdAndRolesPermissionsPermission(userId, permission);
    }

    public List<UserSummary> getAllUsers() {
        return UserProjection.toUsers(this.userRepository.findAllProjectedBy());
    }

    public List<UserSummary> getUsersByTask(Long taskId) {
        return UserProjection.toUsers(this.userRepository.findByTasksTaskId(taskId));
    }

    @Transactional
    public void deleteUser(String userId) {
        this.userRepository.deleteById(userId);
    }
}
