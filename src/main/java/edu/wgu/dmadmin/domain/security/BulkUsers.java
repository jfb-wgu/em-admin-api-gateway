package edu.wgu.dmadmin.domain.security;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Data;

@Data
public class BulkUsers {
	List<String> usernames;
	Set<UUID> roles;
	Set<UUID> tasks;
}
