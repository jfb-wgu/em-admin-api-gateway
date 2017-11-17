package edu.wgu.dmadmin.domain.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import edu.wgu.dmadmin.model.user.UserByIdModel;
import lombok.Data;

@Data
@SuppressWarnings("boxing")
public class Person implements Serializable {

   private static final long serialVersionUID = 1L;
   
   private Long pidm;
   private String username;
   private String studentId;
   private String firstName;
   private String lastName;
   private Boolean isEmployee;
   
   @JsonIgnore
   private String wguEmailAddress;
   
   @JsonIgnore
   private String personType;
   
   @JsonIgnore
   private String primaryPhone;

   @JsonIgnore
   private Set<String> roles = new HashSet<>();
   
   @JsonInclude(value=Include.NON_EMPTY)
   private Set<UUID> teams;
   
   @JsonInclude(value=Include.NON_EMPTY)
   private Set<UUID> emaRoles;
   
   @JsonInclude(value=Include.NON_EMPTY)
   private Set<String> permissions;
   
   @JsonInclude(value=Include.NON_EMPTY)
   private Set<UUID> tasks;
   
   @JsonInclude(value=Include.NON_EMPTY)
   private Set<String> landings;
   
   @JsonInclude(value=Include.NON_EMPTY)
   private Date lastLogin;
   
   
   @JsonInclude(value=Include.NON_EMPTY)
   public String getStudentId() {
	   if (this.isEmployee) return "";
	   return this.studentId;
   }
   
   @JsonInclude(value=Include.NON_EMPTY)
   public String getUserId() {
	   if (this.isEmployee) return this.studentId;
	   return "";
   }
   
   public void setUserInfo(UserByIdModel user) {
	   this.teams = user.getTeams();
	   this.emaRoles = user.getRoles();
	   this.permissions = user.getPermissions();
	   this.tasks = user.getTasks();
	   this.landings = user.getLandings();
	   this.lastLogin = user.getLastLogin();
   }
}
