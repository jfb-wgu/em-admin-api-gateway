package edu.wgu.dm.dto.security;

import com.fasterxml.jackson.annotation.JsonGetter;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role implements Serializable {

    private static final long serialVersionUID = 1664320180308328451L;

    Long roleId;
    String role;
    String roleDescription;
    Date dateCreated;
    Date dateUpdated;

    @ApiModelProperty(dataType = "java.util.List", example = "[0]")
    List<Permission> permissions = new ArrayList<>();

    public Role(Long roleId) {
        this.setRoleId(roleId);
    }

    @JsonGetter("permissions")
    public Set<Long> getPermissionIds() {
        return this.getPermissions()
                   .stream()
                   .map(p -> p.getPermissionId())
                   .collect(Collectors.toSet());
    }

    @JsonGetter("permissionNames")
    public Set<String> getPermissionNames() {
        return this.getPermissions()
                   .stream()
                   .map(p -> p.getPermission())
                   .collect(Collectors.toSet());
    }
}
