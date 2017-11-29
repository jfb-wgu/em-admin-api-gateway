package edu.wgu.dmadmin.repo.oracle;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="WGUAINF", schema="WGUREC")
public class Wguainf implements Serializable {

	private static final long serialVersionUID = 4264011070115563028L;

	@Column(name="WGUAINF_AROL_PIDM")
	Long arolPidm;
	
	@Column(name="WGUAINF_RIDM")
	Long ridm;
	
	@Column(name="WGUAINF_AIDM")
	Long aidm;
	
	@OneToOne(optional=false)
	@JoinColumn(name="WGUAINF_PIDM",referencedColumnName="SPRIDEN_PIDM")
	Spriden spriden;
	
	@Column(name="WGUAINF_SEQNO")
	Long seqNo;
	
	@Column(name="WGUAINF_STATUS")
	String status;
	
	@Column(name="WGUAINF_MODULE")
	String module;
	
	@Column(name="WGUAINF_ACTIVITY_DATE")
	Date activityDate;
	
	@Column(name="WGUAINF_SOURCE")
	String source;
	
	@Column(name="WGUAINF_PROGRAM_CODE")
	String programCode;
	
	@Id
	@Column(name="WGUAINF_ID")
	Long wguainfId;
	
	@Column(name="WGUAINF_LAST_STAGE")
	String lastStage;
	
	@Column(name="WGUAINF_LAST_STAGE_DATE")
	Date lastStageDate;
	
	@Column(name="WGUAINF_SCH")
	String sch;
	
	@Column(name="WGUAINF_STOP_EMAIL")
	String stopEmail;
	
	@Column(name="WGUAINF_STOP_CONTACT")
	String stopContact;
	
	@Column(name="WGUAINF_CONTACT_STATUS_ID")
	Long contactStatusId;
	
	@Column(name="WGUAINF_FIN_AID_IND")
	String finAidInd;
	
	@Column(name="WGUAINF_FIN_AID_DATE")
	Date finAidDate;
	
	@Column(name="WGUAINF_FIN_AID_PROCESSED")
	String finAidProcessed;
	
	@Column(name="WGUAINF_COMM_VENDOR")
	Long commVendor;
	
	@Column(name="WGUAINF_PREASIGN")
	Long preassign;
	
	@Column(name="WGUAINF_ORG_PROGRAM")
	String orgProgram;
	
	@Column(name="WGUAINF_LEAD_DATE")
	Date leadDate;
	
	@Column(name="WGUAINF_STOP_DIALER")
	String stopDialer;
	
	@Column(name="WGUAINF_CAMPUS_CODE")
	String campusCode;
}
