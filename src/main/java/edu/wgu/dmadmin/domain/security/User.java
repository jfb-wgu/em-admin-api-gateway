package edu.wgu.dmadmin.domain.security;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.wgu.dmadmin.util.StatusUtil;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.model.security.UserModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Comparable<User> {

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
	List<String> roleNames;

	@JsonIgnore
	List<String> taskNames;

	public List<String> getRoleNames() {
		if (this.roleNames == null) this.roleNames = new ArrayList<>();
		return this.roleNames;
	}

	public List<String> getTaskNames() {
		if (this.taskNames == null) this.taskNames = new ArrayList<>();
		return this.taskNames;
	}

	@JsonGetter("roles")
	public Set<?> getDisplayRoles() {
		return this.getRoleNames().isEmpty() ? this.roles : new HashSet<>(this.getRoleNames());
	}

	@JsonGetter("tasks")
	public Set<?> getDisplayTasks() {
		return this.getTaskNames().isEmpty() ? this.tasks : new HashSet<>(this.getTaskNames());
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
	
	public boolean isQualified(String status, UUID taskId) {
		return this.getPermissions().contains(StatusUtil.getQueueForStatus(status))
				&& (this.getTasks().isEmpty() || this.getTasks().contains(taskId));
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
		return Comparator.comparing(User::getLastName)
                .thenComparing(User::getFirstName)
                .compare(this, o);
	}
}
