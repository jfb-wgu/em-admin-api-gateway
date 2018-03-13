package edu.wgu.dmadmin.domain.security;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkCreateResponse {
	List<User> users;
	List<String> failed;
}
