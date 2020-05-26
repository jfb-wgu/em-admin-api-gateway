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
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(allowGetters = true, value = {"landings", "queues", "permissions"})
public class User implements Comparable<User>, Serializable, FirstAndLastName {

    private static final long serialVersionUID = -450361831269495272L;

    String userId;
    String firstName;
    String lastName;
    String employeeId;
    Date lastLogin;

    List<Long> tasks = new ArrayList<>();

    @ApiModelProperty(dataType = "java.util.Set", example = "[0]")
    List<Role> roles = new ArrayList<>();

    // unused for now
    List<Long> teams;

    @JsonGetter("roles")
    public Set<Long> getRoleIds() {
        return this.getRoles()
                   .stream()
                   .map(r -> r.getRoleId())
                   .collect(Collectors.toSet());
    }

    @JsonGetter("permissions")
    public Set<String> getPermissions() {
        return this.getRolePermissions()
                   .stream()
                   .map(p -> p.getPermission())
                   .collect(Collectors.toSet());
    }

    @JsonGetter("landings")
    public Set<String> getLandings() {
        return this.getRolePermissions()
                   .stream()
                   .map(p -> p.getLanding())
                   .collect(Collectors.toSet());
    }

    @JsonGetter("queues")
    public List<String> getQueues() {
        List<String> queues = new ArrayList<String>();
        queues.addAll(CollectionUtils.intersection(Permissions.getQueues(), this.getPermissions()));

        // Only include PENDING submissions if the user has tasks configured
        if (CollectionUtils.isNotEmpty(this.getTasks()) && this.getPermissions()
                                                               .contains(Permissions.TASK_QUEUE)) {
            queues.add(Permissions.TASK_QUEUE);
        }

        List<String> allowedStatuses = StatusUtil.getStatusesForQueues(queues);
        return allowedStatuses;
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
}
