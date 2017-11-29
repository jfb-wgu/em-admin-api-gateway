package edu.wgu.dmadmin.repo.oracle;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="TBL_TASKSTREAM_DRF", schema="WGURESULTS")
public class DRF implements Serializable {

	private static final long serialVersionUID = 3928420037626249967L;
	
	@Id
	@Column(name="TSDRF_TBLKEY")
	Long tblKey;
	
	@OneToOne(optional=false)
	@JoinColumn(name="TSDRF_WGUAINF_ID",referencedColumnName="WGUAINF_ID")
	Wguainf wguainf;
	
	@Column(name="TSDRF_ASSESSMENT_CODE")
	String assessmentCode;
	
	@Column(name="TSDRF_TITLE")
	String title;
	
	@Column(name="TSDRF_PROGRAM_ID")
	String programId;
	
	@Column(name="TSDRF_ACTIVITY_DATE")
	Date activityDate;
	
	@Column(name="TSDRF_OVERALL_STATUS")
	String overallStatus;
	
	@Column(name="TSDRF_VENDOR_ID")
	Long vendorId;
	
	@OneToMany(mappedBy="drf",targetEntity=DRFTask.class,fetch=FetchType.EAGER)
	private Collection<DRFTask> tasks;
}
