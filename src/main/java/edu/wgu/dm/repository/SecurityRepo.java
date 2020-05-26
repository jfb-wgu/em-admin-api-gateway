package edu.wgu.dm.repository;

import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.projection.security.UserProjection;
import edu.wgu.dm.repo.security.UserRepository;
import edu.wgu.dm.view.UserPermissionsRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SecurityRepo {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserPermissionsRepository permRepo;

    public Optional<User> getUserById(String userId) {
        Optional<UserEntity> user = this.userRepo.findById(userId);
        return UserEntity.toUser(user);
    }

    public Optional<UserSummary> getUserSummary(String userId) {
        return UserProjection.toUser(this.userRepo.findByUserId(userId));
    }

    public Set<String> getPermissionsForUser(String userId) {
        Set<String> permissions = this.permRepo.getPermissionsForUser(userId);
        return permissions;
    }

    @Transactional
    public void updateLastLogin(String userId) {
        this.userRepo.updateLastLogin(userId);
    }

    public int countMatchingPermissionsForUser(String userId, List<String> permissions) {
        return this.permRepo.countByKeyUserIdAndKeyPermissionIn(userId, permissions);
    }
}
