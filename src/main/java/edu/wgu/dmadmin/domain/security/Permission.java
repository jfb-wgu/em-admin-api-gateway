package edu.wgu.dmadmin.domain.security;

import java.util.Date;
import java.util.UUID;

import edu.wgu.dmadmin.model.security.PermissionModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Permission {
	UUID permissionId;
	String permission;
	String permissionType;
	String permissionDescription;
	String landing;
	Date dateCreated;
	Date dateUpdated;
	
	public Permission (PermissionModel model) {
		this.permissionId = model.getPermissionId();
		this.permission = model.getPermission();
		this.permissionType = model.getPermissionType();
		this.permissionDescription = model.getPermissionDescription();
		this.landing = model.getLanding();
		this.dateCreated = model.getDateCreated();
		this.dateUpdated = model.getDateUpdated();
	}
}
