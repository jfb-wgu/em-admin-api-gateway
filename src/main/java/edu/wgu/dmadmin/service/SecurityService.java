package edu.wgu.dmadmin.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.security.Permission;
import edu.wgu.dmadmin.domain.security.Role;
import edu.wgu.dmadmin.exception.RoleNotFoundException;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;

@Service
public class SecurityService {

	CassandraRepo cassandraRepo;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}

	public List<Permission> getPermissions() {
		return cassandraRepo.getPermissions().stream().map(p -> new Permission(p)).collect(Collectors.toList());
	}
	
	public List<Role> getRoles() {
		Map<UUID, PermissionModel> permissions = cassandraRepo.getPermissions().stream().collect(Collectors.toMap(p -> p.getPermissionId(), p -> p));
		List<Role> roles = cassandraRepo.getRoles().stream().map(r -> new Role(r)).collect(Collectors.toList());
		roles.forEach(role -> {
			role.getPermissions().forEach(permission -> {
				role.getPermissionNames().add(permissions.get(permission).getPermission());
			});
		});
		
		return roles;
	}
	
	public Role getRole(UUID roleId) {
		Map<UUID, PermissionModel> permissions = cassandraRepo.getPermissions().stream().collect(Collectors.toMap(p -> p.getPermissionId(), p -> p));
		Role role = new Role(cassandraRepo.getRole(roleId).orElseThrow(() -> new RoleNotFoundException(roleId)));
		role.getPermissions().forEach(permission -> {
			role.getPermissionNames().add(permissions.get(permission).getPermission());
		});
		
		return role;
	}
	
	/**
	 * When deleting a role, check for any users who have it assigned and remove it.
	 * 
	 * @param roleId
	 */
	public void deleteRole(UUID roleId) {
		List<UserByIdModel> usersWithRole = cassandraRepo.getUsers().stream().filter(u -> u.getRoles().contains(roleId)).collect(Collectors.toList());
		for (UserByIdModel user : usersWithRole) {
			user.getRoles().remove(roleId);
			cassandraRepo.saveUser(user);
		}
		
		cassandraRepo.deleteRole(roleId);
	}
	
	/**
	 * Add or update a system Role.  
	 * 
	 * If the list of permissions is changed, any users who have that role should be updated
	 * to pick up the changed permissions.
	 * 
	 * @param role array
	 */
	public List<Role> saveRoles(Role[] roles) {		
		for (Role role : roles) {
			if (role.getRoleId() == null) {
				role.setRoleId(UUID.randomUUID());
			}
			
			if (role.getDateCreated() == null) {
				role.setDateCreated(DateUtil.getZonedNow());
			}
		
			RoleModel oldRole = cassandraRepo.getRole(role.getRoleId()).orElse(null);
			cassandraRepo.saveRole(new RoleModel(role));
			
			if (oldRole != null && !SetUtils.isEqualSet(role.getPermissions(), oldRole.getPermissions())) {
				cassandraRepo.saveUsers(cassandraRepo.getUsersForRole(role.getRoleId()));
			}
		}
		
		return Arrays.asList(roles);
	}
	
	/**
	 * Add or update system Permissions.  As permission name is part of the primary key on the
	 * permission table, the old record will have to be removed manually if the name changes.
	 * 
	 * If the defined landing page or permission name has changed, update any affected users.
	 * 
	 * @param permission array
	 */
	public void savePermissions(Permission[] permissions) {		
		for (Permission permission : permissions) {
			if (permission.getPermissionId() == null) {
				permission.setPermissionId(UUID.randomUUID());
			}
			
			if (permission.getDateCreated() == null) {
				permission.setDateCreated(DateUtil.getZonedNow());
			}

			PermissionModel oldPermission = cassandraRepo.getPermission(permission.getPermissionId()).orElse(null);
			cassandraRepo.savePermission(new PermissionModel(permission));
			
			if (oldPermission != null) {
				if (!oldPermission.getPermission().equals(permission.getPermission())) {
					cassandraRepo.deletePermission(permission.getPermissionId(), oldPermission.getPermission());
					cassandraRepo.saveUsers(cassandraRepo.getUsersForPermission(oldPermission.getPermission()));
				} else if (!oldPermission.getLanding().equals(permission.getLanding())) {
					cassandraRepo.saveUsers(cassandraRepo.getUsersForPermission(permission.getPermission()));
				}
			}
		}
	}
}
