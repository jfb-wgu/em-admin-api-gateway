package edu.wgu.dm.projection.security;

import edu.wgu.dm.dto.security.RoleInfo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface RoleIdNameProjection {

    Long getRoleId();

    String getRole();

    String getRoleDescription();

    default RoleInfo toRole() {
        RoleInfo nRole = new RoleInfo();
        nRole.setRole(getRole());
        nRole.setRoleDescription(getRoleDescription());
        nRole.setRoleId(getRoleId());
        return nRole;
    }

    static Optional<RoleInfo> toRole(RoleIdNameProjection model) {
        if (model == null) {
            return Optional.empty();
        }
        return Optional.of(model.toRole());
    }

    static List<RoleInfo> toRoles(List<RoleIdNameProjection> models) {
        if (models == null) {
            return Collections.emptyList();
        }
        return models.stream()
                     .map(r -> r.toRole())
                     .collect(Collectors.toList());
    }
}
