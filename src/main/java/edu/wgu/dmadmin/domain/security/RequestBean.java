package edu.wgu.dmadmin.domain.security;

import java.util.Set;

import edu.wgu.dmadmin.model.security.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBean {

	private static Logger logger = LoggerFactory.getLogger(RequestBean.class);

	private UserModel user;
	private Set<String> authRoles;

	public UserModel getUser() {
		logger.debug("RequestBean.getUser() called, returning: " + this.user);
		return this.user;
	}

	public void setUser(UserModel model) {
		logger.debug("RequestBean.setUser() called, value was " + this.user);
		this.user = model;
	}

	public Set<String> getAuthRoles() {
		logger.debug("RequestBean.getAuthRoles() called, returning " + this.authRoles);
		return this.authRoles;
	}

	public void setAuthRoles(Set<String> roles) {
		logger.debug("RequestBean.setAuthRoles() called, was " + this.authRoles);
		this.authRoles = roles;
	}
}
