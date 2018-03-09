package edu.wgu.dmadmin.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.exception.RoleNotFoundException;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dreammachine.domain.security.Role;
import edu.wgu.dreammachine.model.security.PermissionModel;
import edu.wgu.dreammachine.model.security.RoleModel;
import edu.wgu.dreammachine.model.security.UserByIdModel;
import edu.wgu.dreammachine.util.DateUtil;

@Service
public class RoleService {

	CassandraRepo cassandraRepo;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}

	public List<Role> getRoles() {
		Map<UUID, PermissionModel> permissions = this.cassandraRepo.getPermissionMap();
		List<Role> roles = this.cassandraRepo.getRoles().stream().map(r -> new Role(r)).collect(Collectors.toList());
		roles.forEach(role -> {
			role.getPermissions().forEach(permission -> {
				role.getPermissionNames().add(permissions.get(permission).getPermission());
			});
		});
		
		return roles;
	}
	
	public Role getRole(UUID roleId) {
		RoleModel model = this.cassandraRepo.getRole(roleId).orElseThrow(() -> new RoleNotFoundException(roleId));
		Map<UUID, PermissionModel> permissions = this.cassandraRepo.getPermissionMap(Arrays.asList(model));
		Role role = new Role(model);
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
		List<UserByIdModel> usersWithRole = this.cassandraRepo.getUsersForRole(roleId);
		for (UserByIdModel user : usersWithRole) {
			user.getRoles().remove(roleId);
		}
		
		this.cassandraRepo.saveUsers(usersWithRole);
		this.cassandraRepo.deleteRole(roleId);
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
		
			RoleModel oldRole = this.cassandraRepo.getRole(role.getRoleId()).orElse(null);
			this.cassandraRepo.saveRole(new RoleModel(role));
			
			if (oldRole != null && !SetUtils.isEqualSet(role.getPermissions(), oldRole.getPermissions())) {
				this.cassandraRepo.saveUsers(this.cassandraRepo.getUsersForRole(role.getRoleId()));
			}
		}
		
		return Arrays.asList(roles);
	}
}
