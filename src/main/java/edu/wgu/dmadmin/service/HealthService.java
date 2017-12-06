package edu.wgu.dmadmin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.repo.OracleRepo;
import edu.wgu.dmadmin.repo.oracle.DRF;
import edu.wgu.dmadmin.repo.oracle.DRFTask;
import edu.wgu.dmadmin.repo.oracle.StatusEntry;
import edu.wgu.dreamcatcher.domain.model.AssessmentModel;
import edu.wgu.dreamcatcher.domain.model.TaskModel;
import edu.wgu.dreammachine.messaging.MessageSender;
import edu.wgu.dreammachine.model.audit.StatusLogByAssessmentModel;
import edu.wgu.dreammachine.model.audit.StatusLogByStudentModel;
import edu.wgu.dreammachine.model.publish.TaskByAssessmentModel;
import edu.wgu.dreammachine.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dreammachine.util.DateUtil;

@Service
public class HealthService {

	private static Logger logger = LoggerFactory.getLogger(HealthService.class);

	private CassandraRepo cassandraRepo;
	private OracleRepo oracleRepo;
	RabbitTemplate rabbitTemplate;

	@Autowired
	private Environment env;

	@Autowired
	public void setCassandraRepo(CassandraRepo cRepo) {
		this.cassandraRepo = cRepo;
	}

	@Autowired
	public void setOracleRepo(OracleRepo oRepo) {
		this.oracleRepo = oRepo;
	}

	@Autowired
	public void setRabbitTemplate(RabbitTemplate template) {
		this.rabbitTemplate = template;
	}

	public List<StatusEntry> compareDRFData(List<UUID> assessments) {
		List<StatusLogByAssessmentModel> stats = this.cassandraRepo.getAssessmentStatus(assessments);
		List<DRF> drfs = this.oracleRepo
				.findByTitleIn(assessments.stream().map(a -> a.toString()).collect(Collectors.toList()));

		return compareEntries(stats, drfs);
	}

	public List<StatusEntry> compareDRFData(Date activityDate) {
		List<StatusLogByAssessmentModel> stats = this.cassandraRepo.getAssessmentStatus(activityDate);
		List<DRF> drfs = this.oracleRepo.findByVendorIdAndTasksActivityDateGreaterThanEqual(new Long(57),
				new java.sql.Date(activityDate.getTime()));

		return compareEntries(stats, drfs);
	}

	private static List<StatusEntry> compareEntries(List<StatusLogByAssessmentModel> stats, List<DRF> drfs) {
		List<StatusEntry> cassandra = stats.stream().map(s -> new StatusEntry(s)).collect(Collectors.toList());

		List<StatusEntry> oracle = new ArrayList<>();
		drfs.forEach(drf -> {
			drf.getTasks().forEach(task -> {
				oracle.add(new StatusEntry(drf, task));
			});
		});

		List<StatusEntry> result = new ArrayList<>();

		cassandra.forEach(e -> {
			if (!oracle.contains(e)) {
				result.add(e);
			}
		});

		Collections.sort(result);
		return result;
	}

	public AssessmentModel resendAssessmentUpdate(String studentId, UUID assessmentId) {
		MessageSender sender = new MessageSender();
		sender.setRabbitTemplate(this.rabbitTemplate);

		List<TaskByAssessmentModel> basicTasks = this.cassandraRepo.getBasicTasksByAssessment(assessmentId);

		AssessmentModel model = new AssessmentModel();
		model.setAssessmentCode(basicTasks.get(0).getAssessmentCode());
		model.setAssessmentId(assessmentId.toString());
		model.setStudentId(studentId);

		List<TaskModel> tasks = new ArrayList<TaskModel>();
		SubmissionByStudentAndTaskModel submission = null;

		for (TaskByAssessmentModel task : basicTasks) {
			TaskModel taskModel = new TaskModel();
			taskModel.setTaskId(task.getTaskId().toString());
			taskModel.setTaskName(task.getTaskName());
			taskModel.setNumber(task.getTaskOrder());
			
			// Find the latest status record in Oracle
			List<DRF> drfs = this.oracleRepo.findByWguainfSpridenBannerIdAndTasksTaskId(studentId,
					task.getTaskId().toString());
			DRFTask latest = null;
			if (drfs.size() > 0) {
				Collections.sort(drfs);
				DRF drf = drfs.get(0);
				latest = drf.getTasks().get(0);
			}

			Optional<SubmissionByStudentAndTaskModel> optSubmission = this.cassandraRepo
					.getLastSubmissionForTask(studentId, task.getTaskId());
			if (optSubmission.isPresent()) {
				submission = optSubmission.get();
				taskModel.setSubmissionId(submission.getSubmissionId().toString());

				Optional<StatusLogByStudentModel> optStatus = this.cassandraRepo.getLastStatus(studentId,
						submission.getSubmissionId());
				if (optStatus.isPresent()) {
					StatusLogByStudentModel status = optStatus.get();
					if (latest == null || !status.getNewStatus().equals(latest.getStatus())) {
						taskModel.setDateUpdated(status.getActivityDate());

						if (!status.getStudentId().equals(status.getUserId())) {
							taskModel.setEvaluatorId(status.getUserId());
						}

						taskModel.setStatus(Integer.valueOf(status.getNewStatus()));
						tasks.add(taskModel);
					}
				}
			} else if (latest == null || !latest.getStatus().equals("0")) {
				taskModel.setStatus(new Integer(0));
				taskModel.setDateUpdated(DateUtil.getZonedNow());
				tasks.add(taskModel);
			}
		}

		model.setTasks(tasks);
		if (model.getTasks().size() == 0) return null;

		sender.sendUpdate(model);
		return model;
	}

	public Map<String, String> getEnvironment() {
		List<String> propertyNames = new ArrayList<>();
		for (Iterator<?> it = ((AbstractEnvironment) this.env).getPropertySources().iterator(); it.hasNext();) {
			PropertySource<?> propertySource = (PropertySource<?>) it.next();
			if (propertySource instanceof EnumerablePropertySource) {
				String[] names = ((EnumerablePropertySource<?>) propertySource).getPropertyNames();
				propertyNames.addAll(Arrays.asList(names));
				logger.debug("Properties for " + propertySource.getName() + ": " + Arrays.asList(names));
			}
		}

		// this ensures property precedence is respected.
		Map<String, String> properties = new HashMap<>();
		propertyNames.forEach(key -> {
			if (!key.contains("password"))
				properties.put(key, this.env.getProperty(key));
		});

		return properties;
	}
}
