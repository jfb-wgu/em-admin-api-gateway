package edu.wgu.dmadmin.model.user;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "user_by_first_name", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class UserByFirstNameModel extends UserModel {

	@PartitionKey(0)
	public String getFirstName() {
		return this.firstName;
	}

	@PartitionKey(1)
	public String getUserId() {
		return this.userId;
	}
}
