package edu.wgu.dm.dto.security;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.wgu.dm.util.Permissions;
import edu.wgu.dm.util.StatusUtil;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

@JsonIgnoreProperties(allowGetters = true, value = {"landings", "queues", "permissions"})
public class User implements Comparable<User>, Serializable, FirstAndLastName {

    private static final long serialVersionUID = -450361831269495272L;
    private String userId;
    private String firstName;
    private String lastName;
    private String employeeId;
    private Date lastLogin;
    private List<Long> tasks = new ArrayList<>();
    private List<Long> teams;

    @ApiModelProperty(dataType = "java.util.Set", example = "[0]")
    private List<Role> roles = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<Long> getTasks() {
        return tasks;
    }

    public void setTasks(List<Long> tasks) {
        this.tasks = tasks;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Long> getTeams() {
        return teams;
    }

    public void setTeams(List<Long> teams) {
        this.teams = teams;
    }

    public User() {
    }

    @JsonGetter("roles")
    public Set<Long> getRoleIds() {
        return this.getRoles()
                   .stream()
                   .map(Role::getRoleId)
                   .collect(Collectors.toSet());
    }

    @JsonGetter("permissions")
    public Set<String> getPermissions() {
        return this.getRolePermissions()
                   .stream()
                   .map(Permission::getPermission)
                   .collect(Collectors.toSet());
    }

    @JsonGetter("landings")
    public Set<String> getLandings() {
        return this.getRolePermissions()
                   .stream()
                   .map(Permission::getLanding)
                   .collect(Collectors.toSet());
    }

    @JsonGetter("queues")
    public List<String> getQueues() {
        var queues = new ArrayList<>(CollectionUtils.intersection(Permissions.getQueues(), this.getPermissions()));
        // Only include PENDING submissions if the user has tasks configured
        if (CollectionUtils.isNotEmpty(this.getTasks()) && this.getPermissions()
                                                               .contains(Permissions.TASK_QUEUE)) {
            queues.add(Permissions.TASK_QUEUE);
        }
        return StatusUtil.getStatusesForQueues(queues);
    }

    private List<Permission> getRolePermissions() {
        return this.getRoles()
                   .stream()
                   .map(Role::getPermissions)
                   .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    public User(Person person) {
        this.setUserId(person.getUserId());
        this.setFirstName(person.getFirstName());
        this.setLastName(person.getLastName());
        this.setEmployeeId(person.getUsername());
    }

    @Override
    public int compareTo(User o) {
        return Comparator.comparing(User::getLastName)
                         .thenComparing(User::getFirstName)
                         .compare(this, o);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(firstName, user.firstName)
                   && Objects.equals(lastName, user.lastName) && Objects.equals(employeeId,
                                                                                user.employeeId)
                   && Objects.equals(lastLogin, user.lastLogin) && Objects.equals(tasks, user.tasks)
                   && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstName, lastName, employeeId, lastLogin, tasks, roles);
    }
}
