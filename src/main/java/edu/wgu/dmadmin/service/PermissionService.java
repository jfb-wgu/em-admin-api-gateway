package edu.wgu.dmadmin.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dreammachine.domain.security.Permission;
import edu.wgu.dreammachine.model.security.PermissionModel;
import edu.wgu.dreammachine.util.DateUtil;

@Service
public class PermissionService {

	CassandraRepo cassandraRepo;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}

	public List<Permission> getPermissions() {
		return this.cassandraRepo.getPermissions().stream().map(p -> new Permission(p)).collect(Collectors.toList());
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

			PermissionModel oldPermission = this.cassandraRepo.getPermission(permission.getPermissionId()).orElse(null);
			this.cassandraRepo.savePermission(new PermissionModel(permission));
			
			if (oldPermission != null) {
				if (!oldPermission.getPermission().equals(permission.getPermission())) {
					this.cassandraRepo.deletePermission(permission.getPermissionId(), oldPermission.getPermission());
					this.cassandraRepo.saveUsers(this.cassandraRepo.getUsersForPermission(oldPermission.getPermission()));
				} else if (!oldPermission.getLanding().equals(permission.getLanding())) {
					this.cassandraRepo.saveUsers(this.cassandraRepo.getUsersForPermission(permission.getPermission()));
				}
			}
		}
	}
}
