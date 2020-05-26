package edu.wgu.dm.dto.security;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BulkUsers {

    List<String> usernames;
    List<Long> roles;
    List<Long> tasks;
}
