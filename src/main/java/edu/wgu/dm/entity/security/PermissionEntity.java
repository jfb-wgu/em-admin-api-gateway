package edu.wgu.dm.entity.security;

import edu.wgu.dm.dto.security.Permission;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor
@Table(name = "permission", uniqueConstraints = {@UniqueConstraint(columnNames = {"permission"})})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class PermissionEntity implements Serializable {

    private static final long serialVersionUID = 8191457489695276601L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", updatable = false, insertable = false)
    Long permissionId;

    String permission;

    @Column(name = "permission_type")
    String permissionType;

    @Column(name = "permission_description")
    String permissionDescription;

    @Column(name = "landing")
    String landing;

    @CreatedDate
    @Column(name = "date_created", updatable = false)
    Date dateCreated;

    @LastModifiedDate
    @Column(name = "date_updated")
    Date dateUpdated;

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
}
