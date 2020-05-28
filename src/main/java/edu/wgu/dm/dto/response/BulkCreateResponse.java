package edu.wgu.dm.dto.response;

import edu.wgu.dm.dto.security.User;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BulkCreateResponse {

    List<User> users;
    List<String> failed;
}
