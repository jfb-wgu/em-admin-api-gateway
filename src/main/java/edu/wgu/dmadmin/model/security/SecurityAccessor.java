package edu.wgu.dmadmin.model.security;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

import edu.wgu.dmadmin.model.user.UserByFirstNameModel;
import edu.wgu.dmadmin.model.user.UserByIdModel;
import edu.wgu.dmadmin.model.user.UserByLastNameModel;

@Accessor
public interface SecurityAccessor {
    @Query("SELECT * FROM dm.user_by_id")
    Result<UserByIdModel> getAll();
    
    @Query("SELECT * FROM dm.user_by_id where user_id = ?")
    UserByIdModel getByUserId(String userId);
    
    @Query("SELECT * FROM dm.user_by_id where user_id IN :userIds")
    Result<UserByIdModel> getUsersById(List<String> userIds);
    
    @Query("SELECT permissions FROM dm.user_by_id WHERE user_id = ?")
    UserByIdModel getPermissionsForUser(String user_id);
    
    @Query("SELECT user_id, tasks, permissions, first_name, last_name FROM dm.user_by_id WHERE user_id = ?")
    UserByIdModel getUserQualifications(String userId);

    @Query("SELECT * FROM dm.permission")
    Result<PermissionModel> getPermissions();
    
    @Query("SELECT * FROM dm.permission where permission_id IN :permissionIds")
    Result<PermissionModel> getPermissions(List<UUID> permissionIds);
    
    @Query("SELECT * FROM dm.permission WHERE permission_id = ?")
    PermissionModel getPermission(UUID permissionId);
    
    @Query("SELECT * FROM dm.role")
    Result<RoleModel> getRoles();
    
    @Query("SELECT * FROM dm.role where role_id in :roleIds")
    Result<RoleModel> getRoles(List<UUID> roleIds);

    @Query("SELECT * FROM dm.role WHERE role_id = ?")
    RoleModel getRole(UUID roleId);
    
    @Query("DELETE FROM dm.role WHERE role_id = ?")
    void deleteRole(UUID roleId);

    @Query("UPDATE dm.user_by_id set last_login = ? WHERE user_id = ?")
    void updateLastLogin(Date login, String userId);
    
    @Query("SELECT * FROM dm.user_by_last_name where last_name = ?")
    Result<UserByLastNameModel> getUsersByLastName(String name);

    @Query("SELECT * FROM dm.user_by_first_name where first_name = ?")
    Result<UserByFirstNameModel> getUsersByFirstName(String name);
    
    @Query("SELECT * FROM dm.user_by_id WHERE roles CONTAINS ?")
    Result<UserByIdModel> getUsersForRole(UUID roleId);

    @Query("SELECT * FROM dm.user_by_id WHERE permissions CONTAINS ?")
    Result<UserByIdModel> getUsersForPermission(String permission);
    
    @Query("DELETE FROM dm.permission WHERE permission_id = ? and permission = ?")
    void deletePermission(UUID permissionId, String permission);
}
