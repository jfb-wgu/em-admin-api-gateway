package edu.wgu.dm.entity.security;

import edu.wgu.dm.dto.security.Permission;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "permission", uniqueConstraints = {@UniqueConstraint(columnNames = {"permission"})})
@EntityListeners(AuditingEntityListener.class)
public class PermissionEntity implements Serializable {

    private static final long serialVersionUID = 8191457489695276601L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", updatable = false, insertable = false)
    private Long permissionId;

    private String permission;

    @Column(name = "permission_type")
    private String permissionType;

    @Column(name = "permission_description")
    private String permissionDescription;

    @Column(name = "landing")
    private String landing;

    @CreatedDate
    @Column(name = "date_created", updatable = false)
    private Date dateCreated;

    @LastModifiedDate
    @Column(name = "date_updated")
    private Date dateUpdated;

    public PermissionEntity(Long id) {
        this.setPermissionId(id);
    }

    public PermissionEntity(Permission perm) {
        this.setLanding(perm.getLanding());
        this.setPermission(perm.getPermission());
        this.setPermissionDescription(perm.getPermissionDescription());
        this.setPermissionId(perm.getPermissionId());
        this.setPermissionType(perm.getPermissionType());
    }

    public PermissionEntity() {
    }

    public Permission toPermission() {
        Permission perm = new Permission();
        perm.setDateCreated(this.getDateCreated());
        perm.setDateUpdated(this.getDateUpdated());
        perm.setLanding(this.getLanding());
        perm.setPermission(this.getPermission());
        perm.setPermissionDescription(this.getPermissionDescription());
        perm.setPermissionId(this.getPermissionId());
        perm.setPermissionType(this.getPermissionType());
        return perm;
    }

    public static Optional<Permission> toPermission(PermissionEntity model) {
        if (model == null) {
            return Optional.empty();
        }
        return Optional.of(model.toPermission());
    }

    public static Optional<Permission> toPermission(Optional<PermissionEntity> model) {
        if (model.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(model.get()
                                .toPermission());
    }

    public static List<Permission> toPermissions(List<PermissionEntity> models) {
        if (models == null) {
            return Collections.emptyList();
        }
        return models.stream()
                     .map(p -> p.toPermission())
                     .collect(Collectors.toList());
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
        PermissionEntity that = (PermissionEntity) o;
        return Objects.equals(permissionId, that.permissionId) && Objects.equals(permission,
                                                                                 that.permission)
                   && Objects.equals(permissionType, that.permissionType) && Objects.equals(
            permissionDescription, that.permissionDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId, permission, permissionType, permissionDescription);
    }

}
