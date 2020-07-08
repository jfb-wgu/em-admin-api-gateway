package edu.wgu.dm.entity.view;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPermissionsRepository extends JpaRepository<UserPermissionsEntity, UserPermissionsKey> {

    /**
     * This method returns the count of matched permissions for the user.
     */
    int countByKeyUserIdAndKeyPermissionIn(String userId, List<String> permissions);

    /**
     * This method gets the permissions as Strings
     */
    @Query(value = "SELECT u.key.permission FROM UserPermissionsEntity u WHERE u.key.userId = :userId")
    Set<String> getPermissionsForUser(@Param(value = "userId") String userId);
}
