package edu.wgu.dm.dto.security;

import com.fasterxml.jackson.annotation.JsonGetter;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Role implements Serializable {

    private static final long serialVersionUID = 1664320180308328451L;

    private Long roleId;
    private String role;
    private String roleDescription;
    private Date dateCreated;
    private Date dateUpdated;

    @ApiModelProperty(dataType = "java.util.List", example = "[0]")
    private List<Permission> permissions = new ArrayList<>();

    public Role(Long roleId) {
        this.setRoleId(roleId);
    }

    public Role() {
    }

    @JsonGetter("permissions")
    public Set<Long> getPermissionIds() {
        return this.getPermissions()
                   .stream()
                   .map(Permission::getPermissionId)
                   .collect(Collectors.toSet());
    }

    @JsonGetter("permissionNames")
    public Set<String> getPermissionNames() {
        return this.getPermissions()
                   .stream()
                   .map(Permission::getPermission)
                   .collect(Collectors.toSet());
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
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

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
