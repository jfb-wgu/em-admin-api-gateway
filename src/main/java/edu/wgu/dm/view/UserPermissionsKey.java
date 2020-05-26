package edu.wgu.dm.view;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserPermissionsKey implements Serializable {

    private static final long serialVersionUID = 6362262830243684325L;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(name = "permission", nullable = false)
    String permission;
}
