package edu.wgu.dm.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nimbusds.jwt.SignedJWT;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.minidev.json.JSONObject;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Person implements Serializable {

    private static final long serialVersionUID = 7181392197281511771L;

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
    List<String> roles = new ArrayList<>();

    @JsonInclude(value = Include.NON_EMPTY)
    List<Long> teams;

    @JsonInclude(value = Include.NON_EMPTY)
    Set<Long> emaRoles;

    @JsonInclude(value = Include.NON_EMPTY)
    Set<String> permissions;

    @JsonInclude(value = Include.NON_EMPTY)
    List<Long> tasks;

    @JsonInclude(value = Include.NON_EMPTY)
    Set<String> landings;

    @JsonInclude(value = Include.NON_EMPTY)
    Date lastLogin;

    public Person(@NonNull String authToken) throws ParseException {
        String jwtToken = authToken.substring(6, authToken.length());

        JSONObject json = SignedJWT.parse(jwtToken)
                                   .getPayload()
                                   .toJSONObject();

        this.setStudentId(json.getAsString("wguBannerID"));

        if (json.getAsString("wguPIDM") != null) {
            this.setPidm(Long.valueOf(json.getAsString("wguPIDM")));
        }

        this.setFirstName(json.getAsString("givenName"));
        this.setLastName(json.getAsString("sn"));
        this.setUsername(json.getAsString("username"));

        String roleOne = json.getAsString("wguLevelOneRole");
        this.setIsEmployee(Boolean.valueOf("Employee".equalsIgnoreCase(roleOne)));

        if (this.isEmployee.booleanValue()) {
            this.setPreferredEmail(json.get("username") + "@wgu.edu");
        } else {
            this.setPreferredEmail(json.get("username") + "@my.wgu.edu");
        }
    }

    @JsonInclude(value = Include.NON_EMPTY)
    public String getUserId() {
        if (this.isEmployee.booleanValue()) {
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
