package edu.wgu.dm.dto.security;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Permission implements Serializable {

    private static final long serialVersionUID = -7655121357398830905L;

    private Long permissionId;
    private String permission;
    private String permissionType;
    private String permissionDescription;
    private String landing;
    private Date dateCreated;
    private Date dateUpdated;

    public Permission(Long permissionId) {
        this.setPermissionId(permissionId);
    }

    public Permission() {
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getPermissionDescription() {
        return permissionDescription;
    }

    public void setPermissionDescription(String permissionDescription) {
        this.permissionDescription = permissionDescription;
    }

    public String getLanding() {
        return landing;
    }

    public void setLanding(String landing) {
        this.landing = landing;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Permission that = (Permission) o;
        return Objects.equals(permissionId, that.permissionId) && Objects.equals(permission,
                                                                                 that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId, permission);
    }
}
