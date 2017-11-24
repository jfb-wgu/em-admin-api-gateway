package edu.wgu.dmadmin.domain.security;

import java.util.List;

import edu.wgu.dreammachine.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkCreateResponse {
	List<User> users;
	List<String> failed;
}
