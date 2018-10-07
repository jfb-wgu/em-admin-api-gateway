package edu.wgu.dm.admin.service;

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
import edu.wgu.dm.dto.security.LdapGroup;
import edu.wgu.dm.dto.security.LdapUser;

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

    @Cacheable(cacheNames = "ldap.groups")
    public LdapGroup getGroup(String groupName) {
        try {
            this.ldapTemplate.setIgnorePartialResultException(true);
            LdapGroup group = this.ldapTemplate.findOne(query().base(this.ldapGroupBase)
                                                               .where("objectclass")
                                                               .is("group")
                                                               .and(this.groupLookupAttribute)
                                                               .is(groupName),
                    LdapGroup.class);
            return group;
        } catch (EmptyResultDataAccessException e) {
            logger.debug(Arrays.toString(e.getStackTrace()));
            logger.debug("unable to find group [" + groupName + "]");
            return null;
        }
    }

    @Cacheable(cacheNames = "ldap.users")
    public List<LdapUser> getUsers(String userName) {
        try {
            this.ldapTemplate.setIgnorePartialResultException(true);
            List<LdapUser> users = this.ldapTemplate.find(query().base(this.ldapUserBase)
                                                                 .where("objectclass")
                                                                 .is("person")
                                                                 .and(this.userLookupAttribute)
                                                                 .is(userName),
                    LdapUser.class);
            return users;
        } catch (EmptyResultDataAccessException e) {
            logger.error(e.getMessage() + " Unable to find user [" + userName + "]");
            return null;
        }
    }
}
