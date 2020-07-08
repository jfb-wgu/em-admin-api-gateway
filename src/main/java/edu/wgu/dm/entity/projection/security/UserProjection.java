package edu.wgu.dm.entity.projection.security;

import edu.wgu.dm.dto.security.UserSummary;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface UserProjection {

    String getUserId();

    String getFirstName();

    String getLastName();

    Date getLastLogin();

    String getEmployeeId();

    default UserSummary toUser() {
        UserSummary user = new UserSummary();

        user.setUserId(this.getUserId());
        user.setEmployeeId(this.getEmployeeId());
        user.setFirstName(this.getFirstName());
        user.setLastName(this.getLastName());
        user.setLastLogin(this.getLastLogin());

        return user;
    }

    static List<UserSummary> toUsers(List<UserProjection> models) {
        if (models == null) {
            return Collections.emptyList();
        }
        return models.stream()
                     .map(u -> u.toUser())
                     .collect(Collectors.toList());
    }

    static Optional<UserSummary> toUser(UserProjection model) {
        if (model == null) {
            return Optional.empty();
        }
        return Optional.of(model.toUser());
    }
}
