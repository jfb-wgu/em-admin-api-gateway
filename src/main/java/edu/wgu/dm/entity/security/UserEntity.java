package edu.wgu.dm.entity.security;

import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.entity.publish.TaskIdEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user")
@NamedEntityGraph(name = "roles",
                  attributeNodes = {@NamedAttributeNode(value = "roles", subgraph = "roles.permissions")},
                  subgraphs = {@NamedSubgraph(name = "roles.permissions", attributeNodes = {
                      @NamedAttributeNode("permissions")})})
public class UserEntity implements Serializable {

    private static final long serialVersionUID = -7608313040147280987L;

    @Id
    @Column(name = "user_id")
    @Access(AccessType.PROPERTY)
    String userId;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "last_login")
    Date lastLogin;

    @Column(name = "employee_id")
    String employeeId;

    @CreatedDate
    @Column(name = "date_created", updatable = false)
    Date dateCreated;

    @LastModifiedDate
    @Column(name = "date_updated")
    Date dateUpdated;

    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_tasks", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "task_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    Set<TaskIdEntity> tasks = new HashSet<>();

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    String lastModifiedBy;

    public UserEntity(String userId) {
        this.userId = userId;
    }

    public UserEntity(User user) {
        this.setEmployeeId(user.getEmployeeId());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setLastLogin(user.getLastLogin());
        this.setUserId(user.getUserId());
        for (Long id : user.getTasks()) {
            this.getTasks()
                .add(new TaskIdEntity(id));
        }
        for (Role role : user.getRoles()) {
            this.getRoles()
                .add(new RoleEntity(role.getRoleId()));
        }
    }

    public User toUser() {
        User user = new User();

        user.setUserId(this.getUserId());
        user.setEmployeeId(this.getEmployeeId());
        user.setFirstName(this.getFirstName());
        user.setLastName(this.getLastName());
        user.setLastLogin(this.getLastLogin());
        user.setUserId(this.getUserId());
        user.setRoles(RoleEntity.toRoles(new ArrayList<>(this.getRoles())));
        user.setTasks(this.getTasks()
                          .stream()
                          .map(t -> t.getTaskId())
                          .collect(Collectors.toList()));

        return user;
    }

    public static List<User> toUsers(List<UserEntity> models) {
        if (models == null) {
            return Collections.emptyList();
        }
        return models.stream()
                     .map(u -> u.toUser())
                     .collect(Collectors.toList());
    }

    public static Optional<User> toUser(UserEntity model) {
        if (model == null) {
            return Optional.empty();
        }
        return Optional.of(model.toUser());
    }

    public static Optional<User> toUser(Optional<UserEntity> model) {
        if (model.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(model.get()
                                .toUser());
    }
}
