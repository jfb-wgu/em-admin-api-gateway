package edu.wgu.dmadmin.repo.oracle;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="SPRIDEN", schema="SATURN")
public class Spriden implements Serializable {

	private static final long serialVersionUID = 7711224814762930683L;

	@Column(name="SPRIDEN_PIDM")
	Long pidm;
	
	@Id
	@Column(name="SPRIDEN_ID")
	String bannerId;
	
	@Column(name="SPRIDEN_LAST_NAME")
	String lastName;
	
	@Column(name="SPRIDEN_FIRST_NAME")
	String firstName;
	
	@Column(name="SPRIDEN_MI")
	String middleInitial;
	
	@Column(name="SPRIDEN_CHANGE_IND")
	String changeInd;
	
	@Column(name="SPRIDEN_ENTITY_IND")
	String entityInd;
	
	@Column(name="SPRIDEN_ACTIVITY_DATE")
	Date acitvityDate;
	
	@Column(name="SPRIDEN_USER")
	String user;
	
	@Column(name="SPRIDEN_ORIGIN")
	String origin;
	
	@Column(name="SPRIDEN_SEARCH_LAST_NAME")
	String searchLastName;
	
	@Column(name="SPRIDEN_SEARCH_FIRST_NAME")
	String searchFirstName;
	
	@Column(name="SPRIDEN_SEARCH_MI")
	String searchMiddleInitial;
	
	@Column(name="SPRIDEN_SOUNDEX_LAST_NAME")
	String soundexLastName;
	
	@Column(name="SPRIDEN_SOUNDEX_FIRST_NAME")
	String soundexFirstName;
	
	@Column(name="SPRIDEN_NTYP_CODE")
	String ntypCode;
	
	@Column(name="SPRIDEN_CREATE_USER")
	String createUser;
	
	@Column(name="SPRIDEN_CREATE_DATE")
	Date createDate;
	
	@Column(name="SPRIDEN_DATA_ORIGIN")
	String dataOrigin;
	
	@Column(name="SPRIDEN_CREATE_FDMN_CODE")
	String createFdmnCode;
	
	@Column(name="SPRIDEN_SURNAME_PREFIX")
	String surnamePrefix;
	
	@Column(name="SPRIDEN_SURROGATE_ID")
	Long surrogateId;
	
	@Column(name="SPRIDEN_VERSION")
	Long version;
	
	@Column(name="SPRIDEN_USER_ID")
	String userId;
	
	@Column(name="SPRIDEN_VPDI_CODE")
	String vpdiCode;
}
