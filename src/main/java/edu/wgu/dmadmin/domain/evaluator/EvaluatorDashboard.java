package edu.wgu.dmadmin.domain.evaluator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.wgu.dmadmin.domain.submission.DashboardSubmission;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import lombok.Data;

@Data
public class EvaluatorDashboard {

	private String firstName;
	private String lastName;
	private List<DashboardSubmission> workingQueue;
	private List<DashboardSubmission> pendingQueue;
	
	public EvaluatorDashboard(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public void setWorkingQueue(List<? extends SubmissionModel> submissions) {
		this.workingQueue = submissions.stream().map(s -> new DashboardSubmission(s)).collect(Collectors.toList());
	}
	
	public void setPendingQueue(List<SubmissionModel> submissions) {
		this.pendingQueue = submissions.stream().map(s -> new DashboardSubmission(s)).collect(Collectors.toList());
	}
	
	public List<DashboardSubmission> getWorkingQueue() {
		if (this.workingQueue == null) return Collections.emptyList();
		Collections.sort(this.workingQueue);
		return workingQueue;
	}
	
	public List<DashboardSubmission> getPendingQueue() {
		if (this.pendingQueue == null) return Collections.emptyList();
		Collections.sort(this.pendingQueue);
		return pendingQueue;
	}
}
