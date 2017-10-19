package edu.wgu.dmadmin.model.security;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.security.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "user_by_id", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class UserByIdModel extends UserModel {

    @PartitionKey(0)
    public String getUserId() {
    	return userId;
    }

    public UserByIdModel(User user) {
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
