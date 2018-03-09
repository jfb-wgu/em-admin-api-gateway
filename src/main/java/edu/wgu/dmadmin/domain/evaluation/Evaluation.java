package edu.wgu.dmadmin.domain.evaluation;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.datastax.driver.mapping.annotations.Transient;
import com.fasterxml.jackson.annotation.JsonGetter;

import edu.wgu.dmadmin.domain.publish.Rubric;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.model.evaluation.EvaluationAttachmentModel;
import edu.wgu.dmadmin.model.evaluation.EvaluationModel;
import edu.wgu.dmadmin.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evaluation implements Comparable<Evaluation> {
	UUID evaluationId;
	String evaluatorId;
	UUID submissionId;
	UUID taskId;
	String studentId;
	int attempt;
	String status;
	int minutesSpent;
	Date dateStarted;
	Date dateCompleted;
	Date dateUpdated;
	Map<String, EvaluationAspect> aspects;
	List<EvaluationAttachment> attachments;
	String evaluatorFirstName;
	String evaluatorLastName;
	String comments;

	@JsonGetter("passed")
	public boolean isPassed() {
		return this.aspects.values().stream().filter(a -> a.getAssignedScore() < a.getPassingScore()).count() == 0;
	}

	@Transient
	public Set<String> getUnscoredAspects() {
		return this.aspects.values().stream().filter(s -> s.getAssignedScore() < 0).map(s -> s.getAspectName())
				.collect(Collectors.toSet());
	}

	public void closeEvaluation(String inStatus) {
		this.setStatus(inStatus);
		this.setDateCompleted(DateUtil.getZonedNow());
		this.setMinutesSpent(Math.toIntExact(
				TimeUnit.MILLISECONDS.toMinutes(this.getDateCompleted().getTime() - this.getDateStarted().getTime())));
	}

	public Evaluation(EvaluationModel evaluation, List<EvaluationAttachmentModel> inAttachments) {
		this.evaluationId = evaluation.getEvaluationId();
		this.evaluatorId = evaluation.getEvaluatorId();
		this.submissionId = evaluation.getSubmissionId();
		this.taskId = evaluation.getTaskId();
		this.studentId = evaluation.getStudentId();
		this.attempt = evaluation.getAttempt();
		this.status = evaluation.getStatus();
		this.minutesSpent = evaluation.getMinutesSpent();
		this.dateStarted = evaluation.getDateStarted();
		this.dateCompleted = evaluation.getDateCompleted();
		this.dateUpdated = evaluation.getDateUpdated();
		this.evaluatorFirstName = evaluation.getEvaluatorFirstName();
		this.evaluatorLastName = evaluation.getEvaluatorLastName();
		this.comments = evaluation.getComments();
		this.attachments = inAttachments.stream().map(a -> new EvaluationAttachment(a)).collect(Collectors.toList());

		this.aspects = MapUtils.emptyIfNull(evaluation.getAspects()).values().stream()
				.map(a -> new EvaluationAspect(a, evaluation.getEvaluationId()))
				.collect(Collectors.toMap(a -> a.getAspectName(), a -> a));
	}

	public Evaluation(Submission sub, User user, Rubric rubric) {
		this.evaluationId = UUID.randomUUID();
		this.evaluatorId = user.getUserId();
		this.submissionId = sub.getSubmissionId();
		this.taskId = sub.getTaskId();
		this.studentId = sub.getStudentId();
		this.attempt = sub.getAttempt();
		this.dateStarted = DateUtil.getZonedNow();
		this.evaluatorFirstName = user.getFirstName();
		this.evaluatorLastName = user.getLastName();
		this.comments = StringUtils.EMPTY;
		this.attachments = Collections.emptyList();

		this.aspects = rubric.getAspects().stream().map(a -> new EvaluationAspect(a, this.evaluationId))
				.collect(Collectors.toMap(a -> a.getAspectName(), a -> a));
	}

	public Evaluation(Evaluation eval, User user) {
		this.evaluationId = UUID.randomUUID();
		this.evaluatorId = user.getUserId();
		this.submissionId = eval.getSubmissionId();
		this.taskId = eval.getTaskId();
		this.studentId = eval.getStudentId();
		this.attempt = eval.getAttempt();
		this.status = eval.getStatus();
		this.dateStarted = DateUtil.getZonedNow();
		this.evaluatorFirstName = user.getFirstName();
		this.evaluatorLastName = user.getLastName();
		this.comments = eval.getComments();

		this.aspects = eval.getAspects().values().stream().map(a -> new EvaluationAspect(a, this.evaluationId))
				.collect(Collectors.toMap(a -> a.getAspectName(), a -> a));
	}

	@Override
	public int compareTo(Evaluation o) {
		return Comparator.comparing(Evaluation::getAttempt).thenComparing(Evaluation::getDateUpdated)
				.reversed().compare(this, o);
	}
}
