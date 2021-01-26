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

public class Person implements Serializable {

    private static final long serialVersionUID = 7181392197281511771L;
    public static final String USER_NAME = "username";

    private Long pidm;
    private String username;
    private String studentId;
    private String firstName;
    private String lastName;
    private Boolean isEmployee;
    private String preferredEmail;

    @JsonIgnore
    private String personType;

    @JsonIgnore
    private String primaryPhone;

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
    private Date lastLogin;

    private Map<Long, String> tags = new HashMap<>();

    public Person( String authToken) throws ParseException {
        if(authToken==null)
             throw new NullPointerException("non null authToken is required");

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

        if (this.getIsEmployee().booleanValue()) {
            this.setPreferredEmail(json.get(USER_NAME) + "@wgu.edu");
        } else {
            this.setPreferredEmail(json.get(USER_NAME) + "@my.wgu.edu");
        }
    }

    public Person() {
    }

    @JsonInclude(value = Include.NON_EMPTY)
    public String getUserId() {
        if (this.getIsEmployee().booleanValue()) {
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

    public Long getPidm() {
        return this.pidm;
    }

    public String getUsername() {
        return this.username;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Boolean getIsEmployee() {
        return this.isEmployee;
    }

    public String getPreferredEmail() {
        return this.preferredEmail;
    }

    public String getPersonType() {
        return this.personType;
    }

    public String getPrimaryPhone() {
        return this.primaryPhone;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public List<Long> getTeams() {
        return this.teams;
    }

    public Set<Long> getEmaRoles() {
        return this.emaRoles;
    }

    public Set<String> getPermissions() {
        return this.permissions;
    }

    public List<Long> getTasks() {
        return this.tasks;
    }

    public Set<String> getLandings() {
        return this.landings;
    }

    public Date getLastLogin() {
        return this.lastLogin;
    }

    public Map<Long, String> getTags() {
        return this.tags;
    }

    public void setPidm(Long pidm) {
        this.pidm = pidm;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setIsEmployee(Boolean isEmployee) {
        this.isEmployee = isEmployee;
    }

    public void setPreferredEmail(String preferredEmail) {
        this.preferredEmail = preferredEmail;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setTeams(List<Long> teams) {
        this.teams = teams;
    }

    public void setEmaRoles(Set<Long> emaRoles) {
        this.emaRoles = emaRoles;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public void setTasks(List<Long> tasks) {
        this.tasks = tasks;
    }

    public void setLandings(Set<String> landings) {
        this.landings = landings;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setTags(Map<Long, String> tags) {
        this.tags = tags;
    }
}
