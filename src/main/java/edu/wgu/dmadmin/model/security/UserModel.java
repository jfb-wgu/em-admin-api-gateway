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

    @Column(name="user_id")
    String userId;
    
    @Column(name="first_name")
    String firstName;
    
    @Column(name="last_name")
    String lastName;
    
    Set<UUID> teams;
    Set<UUID> roles;
    Set<String> permissions;
    Set<UUID> tasks;
    Set<String> landings;
    
    @Column(name="last_login")
    Date lastLogin;
    
    @Column(name="employee_id")
    String employeeId;
    
    public Set<String> getPermissions() {
    	if (permissions == null) permissions = new HashSet<String>();
    	return permissions;
    }
    
    public Set<String> getLandings() {
    	if (landings == null) landings = new HashSet<String>();
    	return landings;
    }
    
    public Set<UUID> getRoles() {
    	if (roles == null) roles = new HashSet<UUID>();
    	return roles;
    }
    
    public Set<UUID> getTasks() {
    	if (tasks == null) tasks = new HashSet<UUID>();
    	return tasks;
    }
    
    public Set<UUID> getTeams() {
    	if (teams == null) teams = new HashSet<UUID>();
    	return teams;
    }
}
