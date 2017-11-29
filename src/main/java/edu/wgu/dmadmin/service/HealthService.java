package edu.wgu.dmadmin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.repo.OracleRepo;
import edu.wgu.dmadmin.repo.oracle.DRF;
import edu.wgu.dmadmin.repo.oracle.StatusEntry;
import edu.wgu.dreamcatcher.domain.model.AssessmentModel;
import edu.wgu.dreamcatcher.domain.model.TaskModel;
import edu.wgu.dreammachine.exception.TaskNotFoundException;
import edu.wgu.dreammachine.model.audit.StatusLogByAssessmentModel;
import edu.wgu.dreammachine.model.publish.TaskByIdModel;

@Service
public class HealthService {

	private CassandraRepo cassandraRepo;
	private OracleRepo oracleRepo;

	@Autowired
	public void setCassandraRepo(CassandraRepo cRepo) {
		this.cassandraRepo = cRepo;
	}

	@Autowired
	public void setOracleRepo(OracleRepo oRepo) {
		this.oracleRepo = oRepo;
	}

	public List<AssessmentModel> compareDRFData(List<UUID> assessments) {
		List<StatusLogByAssessmentModel> stats = this.cassandraRepo.getAssessmentStatus(assessments);

		List<DRF> drfs = new ArrayList<>();
		assessments.forEach(id -> {
			drfs.addAll(this.oracleRepo.findByTitle(id.toString()));
		});

		List<StatusEntry> cassandra = stats.stream().map(s -> new StatusEntry(s)).collect(Collectors.toList());

		List<StatusEntry> oracle = new ArrayList<>();
		drfs.forEach(drf -> {
			drf.getTasks().forEach(task -> {
				oracle.add(new StatusEntry(drf, task));
			});
		});

		return compareEntries(cassandra, oracle);
	}
	
	public List<AssessmentModel> compareDRFData(Date activityDate) {
		List<StatusLogByAssessmentModel> stats = this.cassandraRepo.getAssessmentStatus(activityDate);
		List<DRF> drfs = this.oracleRepo.findByVendorIdAndActivityDateGreaterThanEqual(new Long(57), new java.sql.Date(activityDate.getTime()));

		List<StatusEntry> cassandra = stats.stream().map(s -> new StatusEntry(s)).collect(Collectors.toList());

		List<StatusEntry> oracle = new ArrayList<>();
		drfs.forEach(drf -> {
			drf.getTasks().forEach(task -> {
				oracle.add(new StatusEntry(drf, task));
			});
		});

		return compareEntries(cassandra, oracle);
	}

	private List<AssessmentModel> compareEntries(List<StatusEntry> cassandra, List<StatusEntry> oracle) {
		List<AssessmentModel> result = new ArrayList<>();

		List<StatusEntry> missing = new ArrayList<>();
		cassandra.forEach(e -> {
			if (!oracle.contains(e))
				missing.add(e);
		});

		missing.forEach(entry -> {
			TaskByIdModel taskById = this.cassandraRepo.getTask(entry.getTaskId())
					.orElseThrow(() -> new TaskNotFoundException(entry.getTaskId()));

			AssessmentModel assessment = new AssessmentModel();
			assessment.setAssessmentCode(taskById.getAssessmentCode());
			assessment.setAssessmentId(taskById.getAssessmentId().toString());
			assessment.setStudentId(entry.getStudentId());

			TaskModel task = new TaskModel();
			task.setDateUpdated(entry.getActivityDate());
			task.setEvaluatorId(entry.getEvaluatorId());
			task.setStatus(Integer.valueOf(entry.getStatus()));
			task.setSubmissionId(entry.getSubmissionId().toString());
			task.setTaskId(taskById.getTaskId().toString());
			task.setTaskName(taskById.getTaskName());
			task.setNumber(taskById.getTaskOrder());

			assessment.setTasks(Arrays.asList(task));
			result.add(assessment);
		});

		return result;
	}
}
