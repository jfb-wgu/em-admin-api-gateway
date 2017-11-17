package edu.wgu.dmadmin.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
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
import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.repo.CassandraRepo;

@Service
public class DirectoryService {
	PersonService personService;
	CassandraRepo cassandraRepo;
	LdapLookup lookup;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}

	@Autowired 
	public void setPersonService(PersonService pService) {
		this.personService = pService;
	}
	
	@Autowired
	public void setLdapLookup(LdapLookup ldapLookup) {
		this.lookup = ldapLookup;
	}
	
	private static Logger logger = LoggerFactory.getLogger(DirectoryService.class);

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
		
		LdapGroup group = this.lookup.getGroup(groupName);
		if (group != null) {
			members.addAll(group.getMembers());
		}
		
		Set<LdapUser> users = new HashSet<LdapUser>();
		for (Name member : members) {
			users.addAll(this.lookup.getUsers(LdapUtils.getStringValue(member, member.size()-1)));
		}
		
		try {
			LdapName ldapGroup = new LdapName(groupName);
			users = users.stream().filter(user -> user.getGroups().contains(ldapGroup)).collect(Collectors.toSet());
		} catch(InvalidNameException e) {
			logger.debug(Arrays.toString(e.getStackTrace()));
		}
		
		return users;
	}
	
	
	public Set<Person> getMissingUsers(String groupName) {
		Set<String> accountNames = getMembersForGroup(groupName).stream().map(member -> member.getSAMAccountName()).collect(Collectors.toSet());
		Set<Person> missing = new HashSet<Person>();
		
		accountNames.forEach(account -> {
			try {
				logger.debug("Looking up user: " + account);
				Optional<Person> user = Optional.of(this.personService.getPersonByUsername(account));
				if (user.isPresent()) {
					Person person = user.get();
					if (!this.cassandraRepo.getUser(person.getUserId()).isPresent()) 
						missing.add(person);
				}
			} catch(Exception e) {
				logger.debug(e.getMessage());
			}
		});
		
		return missing;
	}
}
