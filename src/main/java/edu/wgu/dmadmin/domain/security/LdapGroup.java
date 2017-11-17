package edu.wgu.dmadmin.domain.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapUtils;

import lombok.Data;

@Data
@Entry(objectClasses = {"group"})
public final class LdapGroup implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6878617939623507473L;

	@Id
    private Name dn;

    @Attribute(name="cn")
    @DnAttribute("cn")
    private String name;

    @Attribute(name="member")
    private Set<Name> members;

    public String getCommonName() {
        return LdapUtils.getStringValue(this.dn, this.dn.size()-1);
    }
    
    public Set<Name> getMembers() {
    		if (this.members == null) this.members = new HashSet<>();
    		return this.members;
    }
}
