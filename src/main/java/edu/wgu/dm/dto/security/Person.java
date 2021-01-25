package edu.wgu.dm.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nimbusds.jwt.SignedJWT;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Person implements Serializable {

    private static final long serialVersionUID = 7181392197281511771L;
    public static final String USER_NAME = "username";

    Long pidm;
    String username;
    String studentId;
    String firstName;
    String lastName;
    Boolean isEmployee;
    String preferredEmail;

    @JsonIgnore
    String personType;

    @JsonIgnore
    String primaryPhone;

    @JsonIgnore
    private List<String> roles = new ArrayList<>();

    @JsonInclude(value = Include.NON_EMPTY)
    private List<Long> teams;

    @JsonInclude(value = Include.NON_EMPTY)
    private Set<Long> emaRoles;

    @JsonInclude(value = Include.NON_EMPTY)
    private Set<String> permissions;

    @JsonInclude(value = Include.NON_EMPTY)
    private List<Long> tasks;

    @JsonInclude(value = Include.NON_EMPTY)
    private Set<String> landings;

    @JsonInclude(value = Include.NON_EMPTY)
    Date lastLogin;

    private Map<Long, String> tags = new HashMap<>();

    public Person(@NonNull String authToken) throws ParseException {
        String jwtToken = authToken.substring(6);

        Map<String, Object> json = SignedJWT.parse(jwtToken)
                                            .getPayload()
                                            .toJSONObject();

        this.setStudentId((String) json.get("wguBannerID"));

        if (json.get("wguPIDM") != null) {
            this.setPidm(Long.valueOf((String) json.get("wguPIDM")));
        }
        this.setFirstName((String) json.get("givenName"));
        this.setLastName((String) json.get("sn"));
        this.setUsername((String) json.get(USER_NAME));
        String roleOne = (String) json.get("wguLevelOneRole");
        this.setIsEmployee("Employee".equalsIgnoreCase(roleOne));

        if (this.getIsEmployee()) {
            this.setPreferredEmail(json.get(USER_NAME) + "@wgu.edu");
        } else {
            this.setPreferredEmail(json.get(USER_NAME) + "@my.wgu.edu");
        }
    }

    @JsonInclude(value = Include.NON_EMPTY)
    public String getUserId() {
        if (this.getIsEmployee()) {
            return this.studentId;
        }
        return "";
    }

    public void setUserInfo(Optional<User> user) {
        if (user.isPresent()) {
            User u = user.get();

            this.teams = u.getTeams();
            this.emaRoles = u.getRoleIds();
            this.permissions = u.getPermissions();
            this.tasks = u.getTasks();
            this.landings = u.getLandings();
            this.lastLogin = u.getLastLogin();
        }
    }
}
