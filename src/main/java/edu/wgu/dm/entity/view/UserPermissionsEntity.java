package edu.wgu.dm.entity.view;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "user_permissions")
@Immutable
public class UserPermissionsEntity implements Serializable {

    private static final long serialVersionUID = 5100770206311806188L;

    @EmbeddedId
    private UserPermissionsKey key;

    public UserPermissionsKey getKey() {
        return key;
    }

    public void setKey(UserPermissionsKey key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserPermissionsEntity that = (UserPermissionsEntity) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
