package edu.wgu.dmadmin.domain.security;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class BulkUsers {
	List<String> usernames;
	List<UUID> roles;
	List<UUID> tasks;
}
