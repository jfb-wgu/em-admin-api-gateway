package edu.wgu.dmadmin.model.security;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface SecurityAccessor {
    @Query("SELECT * FROM dm.user_by_id")
    Result<UserModel> getAll();
    
    @Query("SELECT * FROM dm.user_by_id where user_id = ?")
    UserModel getByUserId(String userId);
    
    @Query("SELECT * FROM dm.user_by_id where user_id IN :userIds")
    Result<UserModel> getUsersById(List<String> userIds);
    
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
    
    @Query("SELECT * FROM dm.user_by_id where last_name = ?")
    Result<UserModel> getUsersByLastName(String name);

    @Query("SELECT * FROM dm.user_by_id where first_name = ?")
    Result<UserModel> getUsersByFirstName(String name);
    
    @Query("SELECT * FROM dm.user_by_id WHERE roles CONTAINS ?")
    Result<UserModel> getUsersForRole(UUID roleId);

    @Query("SELECT * FROM dm.user_by_id WHERE permissions CONTAINS ?")
    Result<UserModel> getUsersForPermission(String permission);
    
    @Query("DELETE FROM dm.permission WHERE permission_id = ? and permission = ?")
    void deletePermission(UUID permissionId, String permission);
}
