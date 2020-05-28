package edu.wgu.dm.view;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

@Data
@NoArgsConstructor
@Table(name = "user_permissions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Immutable
public class UserPermissionsEntity implements Serializable {

    private static final long serialVersionUID = 5100770206311806188L;

    @EmbeddedId
    UserPermissionsKey key;
}
