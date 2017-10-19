package edu.wgu.dmadmin.domain.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import edu.wgu.dmadmin.model.security.RoleModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Role {
	UUID roleId;
	String role;
	String roleDescription;
	Set<UUID> permissions;
	Date dateCreated;
	Date dateUpdated;
	Set<String> permissionNames;
	
	public Set<String> getPermissionNames() {
		if (permissionNames == null) permissionNames = new HashSet<String>();
		return permissionNames;
	}
	
	public Role(RoleModel model) {
		this.roleId = model.getRoleId();
		this.role = model.getRole();
		this.roleDescription = model.getRoleDescription();
		this.permissions = model.getPermissions();
		this.dateCreated = model.getDateCreated();
		this.dateUpdated = model.getDateUpdated();
	}
}
