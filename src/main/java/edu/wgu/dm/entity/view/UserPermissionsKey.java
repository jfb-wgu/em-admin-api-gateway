package edu.wgu.dm.entity.view;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserPermissionsKey implements Serializable {

    private static final long serialVersionUID = 6362262830243684325L;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "permission", nullable = false)
    private String permission;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserPermissionsKey that = (UserPermissionsKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, permission);
    }
}
