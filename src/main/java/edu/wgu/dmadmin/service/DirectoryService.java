package edu.wgu.dmadmin.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.security.LdapGroup;
import edu.wgu.dmadmin.domain.security.LdapLookup;
import edu.wgu.dmadmin.domain.security.LdapUser;

@Service
public class DirectoryService {
	
	private static Logger logger = LoggerFactory.getLogger(DirectoryService.class);

	@Autowired
	private LdapLookup lookup;
	
	@Value("${ldap.group.dmAdmin}")
	private String adminGroup;
	
	@Value("${ldap.group.dmPublish}")
	private String publishGroup;
	
	@Value("${ldap.group.dmFaculty}")
	private String facultyGroup;
	
	@Value("${ldap.group.dmEvaluator}")
	private String evaluatorGroup;

	public Set<LdapUser> getMembersForGroup(String groupName) {
		Set<Name> members = new HashSet<Name>();
		
		LdapGroup group = lookup.getGroup(groupName);
		if (group != null) {
			members.addAll(group.getMembers());
		}
		
		Set<LdapUser> users = new HashSet<LdapUser>();
		members.forEach(member -> {
			users.addAll(lookup.getUsers(LdapUtils.getStringValue(member, member.size()-1)));
		});
		
		try {
			LdapName ldapGroup = new LdapName(groupName);
			users.stream().filter(user -> user.getGroups().contains(ldapGroup)).collect(Collectors.toSet());
		} catch(InvalidNameException e) {
			logger.debug(Arrays.toString(e.getStackTrace()));
		}
		
		return users;
	}
}
