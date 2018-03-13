package edu.wgu.dmadmin.model.security;

import com.datastax.driver.mapping.annotations.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserModel {

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
}
