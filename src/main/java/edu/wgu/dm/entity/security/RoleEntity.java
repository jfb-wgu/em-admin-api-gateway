package edu.wgu.dm.entity.security;

import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class RoleEntity implements Serializable {

    private static final long serialVersionUID = -7685134213348338771L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", updatable = false, insertable = false)
    Long roleId;

    String role;

    @Column(name = "role_description")
    String roleDescription;

    @CreatedDate
    @Column(name = "date_created", updatable = false)
    Date dateCreated;

    @LastModifiedDate
    @Column(name = "date_updated")
    Date dateUpdated;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
               inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "permission_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    List<PermissionEntity> permissions = new ArrayList<>();

    public RoleEntity(Long roleId) {
        this.roleId = roleId;
    }

    public RoleEntity(Role inRole) {
        setRole(inRole.getRole());
        setRoleDescription(inRole.getRoleDescription());
        setRoleId(inRole.getRoleId());
        for (Permission p : inRole.getPermissions()) {
            this.getPermissions()
                .add(new PermissionEntity(p.getPermissionId()));
        }
    }

    public Role toRole() {
        Role nRole = new Role();

        nRole.setDateCreated(getDateCreated());
        nRole.setDateUpdated(getDateUpdated());
        nRole.setRole(getRole());
        nRole.setRoleDescription(getRoleDescription());
        nRole.setRoleId(getRoleId());
        nRole.setPermissions(PermissionEntity.toPermissions(this.getPermissions()));

        return nRole;
    }

    public static Optional<Role> toRole(RoleEntity model) {
        if (model == null) {
            return Optional.empty();
        }
        return Optional.of(model.toRole());
    }

    public static Optional<Role> toRole(Optional<RoleEntity> model) {
        if (model.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(model.get()
                                .toRole());
    }

    public static List<Role> toRoles(List<RoleEntity> models) {
        if (models == null) {
            return Collections.emptyList();
        }
        return models.stream()
                     .map(r -> r.toRole())
                     .collect(Collectors.toList());
    }
}
