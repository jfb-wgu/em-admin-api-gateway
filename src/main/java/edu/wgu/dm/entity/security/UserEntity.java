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
import java.util.Objects;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
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
    private String userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "employee_id")
    private String employeeId;

    @CreatedDate
    @Column(name = "date_created", updatable = false)
    private Date dateCreated;

    @LastModifiedDate
    @Column(name = "date_updated")
    private Date dateUpdated;

    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_tasks", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "task_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<TaskIdEntity> tasks = new HashSet<>();

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

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

    public UserEntity() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public Set<TaskIdEntity> getTasks() {
        return tasks;
    }

    public void setTasks(Set<TaskIdEntity> tasks) {
        this.tasks = tasks;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserEntity that = (UserEntity) o;
        return Objects.equals(userId, that.userId) && Objects.equals(firstName, that.firstName)
                   && Objects.equals(lastName, that.lastName) && Objects.equals(lastLogin,
                                                                                that.lastLogin)
                   && Objects.equals(employeeId, that.employeeId) && Objects.equals(dateCreated,
                                                                                    that.dateCreated)
                   && Objects.equals(dateUpdated, that.dateUpdated) && Objects.equals(roles, that.roles)
                   && Objects.equals(tasks, that.tasks) && Objects.equals(createdBy, that.createdBy)
                   && Objects.equals(lastModifiedBy, that.lastModifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstName, lastName, lastLogin, employeeId, dateCreated, dateUpdated, roles, tasks,
                            createdBy, lastModifiedBy);
    }
}
