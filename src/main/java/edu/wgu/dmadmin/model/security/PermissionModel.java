package edu.wgu.dmadmin.model.security;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.security.Permission;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "permission")
public class PermissionModel {
	
	@PartitionKey(0)
	@Column(name="permission_id")
	UUID permissionId;
	
	String permission;
	
	@Column(name="permission_type")
	String permissionType;

	@Column(name="permission_description")
	String permissionDescription;
	
	String landing;

	@Column(name="date_created")
	Date dateCreated;
	
	@Column(name="date_updated")
	Date dateUpdated;
	
	public PermissionModel(Permission permission) {
		this.permissionId = permission.getPermissionId();
		this.permission = permission.getPermission();
		this.permissionType = permission.getPermissionType();
		this.permissionDescription = permission.getPermissionDescription();
		this.landing = permission.getLanding();
		this.dateCreated = permission.getDateCreated();
		this.dateUpdated = permission.getDateUpdated();
	}
}
