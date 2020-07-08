package edu.wgu.dm.entity.projection.security;

import edu.wgu.dm.dto.security.Role;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface RoleProjection {

    Long getRoleId();

    String getRole();

    String getRoleDescription();

    Date getDateCreated();

    Date getDateUpdated();

    default Role toRole() {
        Role nRole = new Role();

        nRole.setDateCreated(getDateCreated());
        nRole.setDateUpdated(getDateUpdated());
        nRole.setRole(getRole());
        nRole.setRoleDescription(getRoleDescription());
        nRole.setRoleId(getRoleId());

        return nRole;
    }

    static Optional<Role> toRole(RoleProjection model) {
        if (model == null) {
            return Optional.empty();
        }
        return Optional.of(model.toRole());
    }

    static List<Role> toRoles(List<RoleProjection> models) {
        if (models == null) {
            return Collections.emptyList();
        }
        return models.stream()
                     .map(r -> r.toRole())
                     .collect(Collectors.toList());
    }
}
