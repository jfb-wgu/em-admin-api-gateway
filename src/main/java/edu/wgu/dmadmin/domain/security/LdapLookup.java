package edu.wgu.dmadmin.domain.security;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

@Component
public class LdapLookup {
	
	private static Logger logger = LoggerFactory.getLogger(LdapLookup.class);
	
	@Value("${ldap.user.base}")
	private String ldapUserBase;
	
	@Value("${ldap.user.lookupAttribute}")
	private String userLookupAttribute;
	
	@Value("${ldap.group.base}")
	private String ldapGroupBase;
	
	@Value("${ldap.group.lookupAttribute}")
	private String groupLookupAttribute;

	@Autowired
	private LdapTemplate ldapTemplate;

	@Cacheable(cacheNames="ldap.groups")
	public LdapGroup getGroup(String groupName) {
		try {
			ldapTemplate.setIgnorePartialResultException(true);
			LdapGroup group = ldapTemplate.findOne(
					query().base(ldapGroupBase)
			         .where("objectclass").is("group")
			         .and(groupLookupAttribute).is(groupName),
			         LdapGroup.class);
			return group;
		} catch(EmptyResultDataAccessException e) {
			logger.debug(Arrays.toString(e.getStackTrace()));
			logger.debug("unable to find group [" + groupName + "]");
			return null;
		}
	}
	
	@Cacheable(cacheNames="ldap.users")
	public List<LdapUser> getUsers(String userName) {
		try {
			ldapTemplate.setIgnorePartialResultException(true);
			List<LdapUser> users = ldapTemplate.find(
					query().base(ldapUserBase)
					.where("objectclass").is("person")
					.and(userLookupAttribute).is(userName),
					LdapUser.class);
			return users;
		} catch(EmptyResultDataAccessException e) {
			logger.error(e.getMessage() + " Unable to find user [" + userName + "]");
			return null;
		}	
	}
}
