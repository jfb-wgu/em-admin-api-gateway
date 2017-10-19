package edu.wgu.dmadmin.domain.submission;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.wgu.dmadmin.util.StatusUtil;
import lombok.Data;

@Data
public class Status {
	private String status;
	private int attempt;
	
	@JsonGetter("studentStatus")
	public String getStudentStatus() {
		return StatusUtil.getStudentStatus(status, attempt);
	}
	
	@JsonGetter("searchStatus")
	public String getSearchStatus() {
		return StatusUtil.getSearchStatus(status);
	}
	
	public Status(String inStatus, int inAttempt) {
		this.status = inStatus;
		this.attempt = inAttempt;
	}
}
