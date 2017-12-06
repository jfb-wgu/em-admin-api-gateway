package edu.wgu.dmadmin.repo.oracle;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="TBL_TASKSTREAM_DRF_TASKS", schema="WGURESULTS")
public class DRFTask implements Serializable, Comparable<DRFTask> {

	private static final long serialVersionUID = 7080639488029405743L;

	@Id
	@Column(name="TSTASK_TBLKEY")
	Long tblKey;
	
	@Column(name="TSTASK_TS_ID")
	String taskId;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="TSTASK_TSDRF_FK",referencedColumnName="TSDRF_TBLKEY")
	DRF drf;
	
	@Column(name="TSTASK_TITLE")
	String title;
	
	@Column(name="TSTASK_ENDDATE")
	Date endDate;
	
	@Column(name="TSTASK_STATUS")
	String status;
	
	@Column(name="TSTASK_ACTIVITY_DATE")
	Date activityDate;
	
	@Column(name="TSTASK_TASK_NUMBER")
	Integer taskNumber;
	
	@Column(name="TSTASK_EVALUATOR_ID")
	String evaluatorId;
	
	@Column(name="TSTASK_PROVISIONAL_EVAL")
	String provisionalEval;
	
	@Column(name="TSTASK_FOLDER_ID")
	String folderId;
	
	@Column(name="TSTASK_TSTASK_REPLACE_FK")
	Long taskReplaceFK;

	@Override
	public int compareTo(DRFTask o) {
		return o.getActivityDate().compareTo(this.getActivityDate());
	}
}
