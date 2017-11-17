package edu.wgu.dmadmin.domain.security;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entry(objectClasses = {"person"})
public final class LdapUser implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6611800490175092636L;

	@JsonIgnore
	@Id
    private Name dn;

    @Attribute(name="cn")
    @DnAttribute("cn")
    private String name;

    @JsonIgnore
    @Attribute(name="memberOf")
    private Set<Name> groups;
    
    @Attribute(name="userPrincipalName")
    private String userPrincipalName;
    
    @Attribute(name="mailNickname")
    private String mailNickname;
    
    @Attribute(name="givenName")
    private String givenName;
    
    @Attribute(name="sn")
    private String sn;
    
    @Attribute(name="sAMAccountName")
    private String sAMAccountName;
    
    public String getCommonName() {
        return LdapUtils.getStringValue(this.dn, this.dn.size()-1);
    }
    
    public Set<String> getGroupCommonNames() {
    	return this.groups.stream().map(group -> LdapUtils.getStringValue(group, group.size()-1)).collect(Collectors.toSet());
    }
}
