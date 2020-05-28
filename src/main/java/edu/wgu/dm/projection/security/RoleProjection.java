package edu.wgu.dm.projection.security;

import edu.wgu.dm.dto.security.Role;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface RoleProjection {

    public Long getRoleId();

    public String getRole();

    public String getRoleDescription();

    public Date getDateCreated();

    public Date getDateUpdated();

    public default Role toRole() {
        Role nRole = new Role();

        nRole.setDateCreated(getDateCreated());
        nRole.setDateUpdated(getDateUpdated());
        nRole.setRole(getRole());
        nRole.setRoleDescription(getRoleDescription());
        nRole.setRoleId(getRoleId());

        return nRole;
    }

    public static Optional<Role> toRole(RoleProjection model) {
        if (model == null) {
            return Optional.empty();
        }
        return Optional.of(model.toRole());
    }

    public static List<Role> toRoles(List<RoleProjection> models) {
        if (models == null) {
            return Collections.emptyList();
        }
        return models.stream()
                     .map(r -> r.toRole())
                     .collect(Collectors.toList());
    }
}
