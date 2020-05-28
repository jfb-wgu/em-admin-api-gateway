package edu.wgu.dm.dto.security;

import java.util.Date;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSummary implements FirstAndLastName {

    String userId;
    String firstName;
    String lastName;
    Date lastLogin;
    String employeeId;
}
