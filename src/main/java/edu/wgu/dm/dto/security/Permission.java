package edu.wgu.dm.dto.security;

import java.io.Serializable;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Permission implements Serializable {

    private static final long serialVersionUID = -7655121357398830905L;

    @EqualsAndHashCode.Include
    Long permissionId;

    @EqualsAndHashCode.Include
    String permission;

    String permissionType;
    String permissionDescription;
    String landing;
    Date dateCreated;
    Date dateUpdated;

    public Permission(Long permissionId) {
        this.setPermissionId(permissionId);
    }
}
