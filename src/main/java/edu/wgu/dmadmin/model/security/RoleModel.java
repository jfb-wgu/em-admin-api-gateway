package edu.wgu.dmadmin.model.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.security.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "role")
public class RoleModel {
	
	@PartitionKey(0)
	@Column(name="role_id")
	UUID roleId;
	
	String role;

	@Column(name="role_description")
	String roleDescription;
	
	Set<UUID> permissions;
	
	@Column(name="date_created")
	Date dateCreated;
	
	@Column(name="date_updated")
	Date dateUpdated;
	
	public Set<UUID> getPermissions() {
		if (this.permissions == null) this.permissions = new HashSet<UUID>();
		return this.permissions;
	}
	
	public RoleModel(Role role) {
		this.roleId = role.getRoleId();
		this.role = role.getRole();
		this.roleDescription = role.getRoleDescription();
		this.permissions = role.getPermissions();
		this.dateCreated = role.getDateCreated();
		this.dateUpdated = role.getDateUpdated();
	}
}
