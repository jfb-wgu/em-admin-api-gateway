package edu.wgu.dmadmin.model.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.security.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "user_by_id", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class UserModel {

	@PartitionKey(0)
	@Column(name = "user_id")
	String userId;

	@Column(name = "first_name")
	String firstName;

	@Column(name = "last_name")
	String lastName;

	Set<UUID> teams;
	Set<UUID> roles;
	Set<String> permissions;
	Set<UUID> tasks;
	Set<String> landings;

	@Column(name = "last_login")
	Date lastLogin;

	@Column(name = "employee_id")
	String employeeId;

	public Set<String> getPermissions() {
		if (this.permissions == null)
			this.permissions = new HashSet<String>();
		return this.permissions;
	}

	public Set<String> getLandings() {
		if (this.landings == null)
			this.landings = new HashSet<String>();
		return this.landings;
	}

	public Set<UUID> getRoles() {
		if (this.roles == null)
			this.roles = new HashSet<UUID>();
		return this.roles;
	}

	public Set<UUID> getTasks() {
		if (this.tasks == null)
			this.tasks = new HashSet<UUID>();
		return this.tasks;
	}

	public Set<UUID> getTeams() {
		if (this.teams == null)
			this.teams = new HashSet<UUID>();
		return this.teams;
	}

	public UserModel(User user) {
		this.userId = user.getUserId();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.roles = user.getRoles();
		this.permissions = user.getPermissions();
		this.teams = user.getTeams();
		this.tasks = user.getTasks();
		this.landings = user.getLandings();
		this.lastLogin = user.getLastLogin();
		this.employeeId = user.getEmployeeId();
	}
}
