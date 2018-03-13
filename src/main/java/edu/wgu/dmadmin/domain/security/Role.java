package edu.wgu.dmadmin.domain.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import edu.wgu.dmadmin.model.security.RoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Role {
	UUID roleId;
	String role;
	String roleDescription;
	Set<UUID> permissions;
	
	@ApiModelProperty(hidden=true)
	Date dateCreated;
	
	@ApiModelProperty(hidden=true)
	Date dateUpdated;
	
	@ApiModelProperty(hidden=true)
	Set<String> permissionNames;
	
	public Set<String> getPermissionNames() {
		if (this.permissionNames == null) this.permissionNames = new HashSet<String>();
		return this.permissionNames;
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
