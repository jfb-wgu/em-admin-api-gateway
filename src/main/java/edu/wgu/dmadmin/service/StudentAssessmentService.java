package edu.wgu.dmadmin.service;

import edu.wgu.dmadmin.domain.assessment.Assessment;
import edu.wgu.dmadmin.domain.assessment.Course;
import edu.wgu.dmadmin.domain.assessment.Task;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.exception.CourseNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.publish.TaskByCourseModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.StatusUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Jessica Pamdeth
 */

@Service
public class StudentAssessmentService {
    @Autowired
    private CassandraRepo cassandraRepo;

    @Autowired
    private FeedbackService feedbackService;
	
	public Course getAssessmentsForCourse(String studentId, Long courseId) {
		List<TaskByCourseModel> allCourseTaskModels = cassandraRepo.getTasksByCourseId(courseId);
		if (allCourseTaskModels.isEmpty()) throw new CourseNotFoundException(courseId);
		return getAssessmentsForCourse(allCourseTaskModels, studentId);
	}
	
	public Course getAssessmentsForCourse(List<? extends TaskModel> tasks, String studentId) {
		
		Course course = new Course(tasks);

		for (Assessment assessment : course.getAssessments()) {
			for (Task task : assessment.getTasks()) {
				SubmissionByStudentAndTaskModel submission = cassandraRepo.getLastSubmissionByStudentAndTaskId(studentId, task.getTaskId());
				if (submission != null) {
					List<SubmissionByStudentAndTaskModel> previous = cassandraRepo.getSubmissionHistoryByStudentAndTask(studentId, task.getTaskId());
					task.setSubmissionHistory(previous.stream().filter(s -> !s.getSubmissionId().equals(submission.getSubmissionId())).map(s -> new Submission(s)).collect(Collectors.toList()));
					task.setSubmissionInfo(submission);
	
					if (StatusUtil.isEvaluated(submission.getStatus())) {
						EvaluationByIdModel evaluation = cassandraRepo.getEvaluationById(submission.getEvaluationId()).orElseThrow(() -> new EvaluationNotFoundException(submission.getEvaluationId()));
						task.getScoreReport().prepareScoreReport(evaluation.getScoreReport(), submission);
						course.setRequestFeedback(StatusUtil.isPassed(submission.getStatus()));
					}
				}
			}
		}

		course.setRequestFeedback(course.isRequestFeedback() && !feedbackService.hasStudentFeedback(studentId));

		return course;
	}
	
	public Task getScoreReport(String studentId, UUID submissionId) {
		SubmissionByIdModel submission = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId));
		TaskByIdModel assessmentTask = cassandraRepo.getTaskById(submission.getTaskId()).orElseThrow(() -> new TaskNotFoundException(submission.getTaskId()));

		Task task = new Task(assessmentTask);
		task.setSubmissionInfo(submission);

		if (StatusUtil.isEvaluated(submission.getStatus())) {
			EvaluationByIdModel evaluation = cassandraRepo.getEvaluationById(submission.getEvaluationId()).orElseThrow(() -> new EvaluationNotFoundException(submission.getEvaluationId()));
			task.getScoreReport().prepareScoreReport(evaluation.getScoreReport(), submission);
		}

		return task;
	}
}
