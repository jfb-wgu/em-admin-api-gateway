package edu.wgu.dmadmin.domain.security;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.wgu.dmadmin.model.security.UserModel;

@Data
@NoArgsConstructor
public class User  implements Comparable<User> {
	
    String userId;
    String firstName;
    String lastName;
    Set<UUID> roles;
    Set<String> permissions;
    Set<UUID> teams;
    Set<UUID> tasks;
    Set<String> landings;
    Date lastLogin;
    String employeeId;

    @JsonIgnore
    Set<String> roleNames;
    
    @JsonIgnore
    Set<String> taskNames;
    
    public Set<String> getRoleNames() {
    	if (roleNames == null) roleNames = new HashSet<String>();
    	return roleNames;
    }
    
    public Set<String> getTaskNames() {
    	if (taskNames == null) taskNames = new HashSet<String>();
    	return taskNames;
    }
    
    @JsonGetter("roles")
    public Set<?> getDisplayRoles() {
    	return this.getRoleNames().isEmpty() ? roles : roleNames;
    }
    
    @JsonGetter("tasks")
    public Set<?> getDisplayTasks() {
    	return this.getTaskNames().isEmpty() ? tasks : taskNames;
    }
    
    @JsonGetter("queues")
    public Set<String> getQueues() {
    	Set<String> queues = new HashSet<String>();
    	queues.addAll(SetUtils.intersection(Permissions.getQueues(), this.getPermissions()));
		
		// Only include PENDING submissions if the user has tasks configured
		if (CollectionUtils.isNotEmpty(this.getTasks()) && this.getPermissions().contains(Permissions.TASK_QUEUE)) {
			queues.add(Permissions.TASK_QUEUE);
		}
		
		return queues;
    }

    public User(UserModel model) {
    	this.userId = model.getUserId();
    	this.firstName = model.getFirstName();
    	this.lastName = model.getLastName();
    	this.roles = model.getRoles();
    	this.permissions = model.getPermissions();
    	this.teams = model.getTeams();
    	this.tasks = model.getTasks();
    	this.landings = model.getLandings();
    	this.lastLogin = model.getLastLogin();
    	this.employeeId = model.getEmployeeId();
    }

	@Override
	public int compareTo(User o) {
		if (this.getLastName().equals(o.getLastName())) {
			return this.getFirstName().compareTo(o.getFirstName());
		} else {
			return this.getLastName().compareTo(o.getLastName());
		}
	}
}
