package edu.wgu.dm.admin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.dto.security.LdapGroup;
import edu.wgu.dm.dto.security.LdapUser;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.service.feign.PersonService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private static Logger logger = LoggerFactory.getLogger(DirectoryService.class);

    final PersonService personService;

    final AdminRepository repo;

    final LdapLookup lookup;

    public List<LdapUser> getMembersForGroup(@Nonnull String groupName) {
        List<Name> members = new ArrayList<Name>();

        LdapGroup group = this.lookup.getGroup(groupName);
        if (group != null) {
            members.addAll(group.getMembers());
        }

        List<LdapUser> users = new ArrayList<LdapUser>();
        for (Name member : members) {
            users.addAll(this.lookup.getUsers(LdapUtils.getStringValue(member, member.size() - 1)));
        }

        try {
            LdapName ldapGroup = new LdapName(groupName);
            users = users.stream()
                         .filter(user -> user.getGroups()
                                             .contains(ldapGroup))
                         .collect(Collectors.toList());
        } catch (InvalidNameException e) {
            logger.debug(Arrays.toString(e.getStackTrace()));
        }

        return users;
    }

    public Set<Person> getMissingUsers(@Nonnull String groupName) {
        List<String> accountNames = getMembersForGroup(groupName).stream()
                                                                 .map(member -> member.getSAMAccountName())
                                                                 .collect(Collectors.toList());
        Set<Person> missing = new HashSet<>();
        accountNames.forEach(account -> {
            try {
                logger.debug("Looking up user: " + account);
                Optional<Person> user = Optional.of(this.personService.getPersonByUsername(account));
                if (user.isPresent()) {
                    Person person = user.get();
                    if (!this.repo.getUserById(person.getUserId())
                                  .isPresent())
                        missing.add(person);
                }
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        });
        return missing;
    }
}
