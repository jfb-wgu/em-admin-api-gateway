package edu.wgu.dm.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {

    @Autowired
    Environment env;

    @Value("${ldap.group.dmAdmin}")
    private String adminGroup;

    @Value("${ldap.group.dmPublish}")
    private String publishGroup;

    @Value("${ldap.group.dmFaculty}")
    private String facultyGroup;

    @Value("${ldap.group.dmEvaluator}")
    private String evaluatorGroup;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(this.env.getRequiredProperty("ldap.url"));
        contextSource.setUserDn(this.env.getRequiredProperty("ldap.user"));
        contextSource.setPassword(this.env.getRequiredProperty("ldap.password"));
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
}
