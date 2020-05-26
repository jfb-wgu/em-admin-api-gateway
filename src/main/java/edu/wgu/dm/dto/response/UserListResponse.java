package edu.wgu.dm.dto.response;

import edu.wgu.dm.dto.security.UserSummary;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 *
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserListResponse {

    List<UserSummary> evaluators;
}
