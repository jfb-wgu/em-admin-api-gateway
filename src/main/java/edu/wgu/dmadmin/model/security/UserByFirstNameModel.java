package edu.wgu.dmadmin.model.security;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "user_by_first_name", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class UserByFirstNameModel extends UserModel {

	@PartitionKey(0)
	public String getFirstName() {
		return firstName;
	}

	@PartitionKey(1)
	public String getUserId() {
		return userId;
	}
}
