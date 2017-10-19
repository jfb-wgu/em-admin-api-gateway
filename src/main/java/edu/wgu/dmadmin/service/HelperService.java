package edu.wgu.dmadmin.service;

import com.datastax.driver.core.Session;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.assessment.Evaluation;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.domain.submission.SubmissionData;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HelperService {

    @Autowired
    CassandraRepo cassandraRepo;

    @Autowired
    StudentWorkService studentWorkService;

    @Autowired
    PublishAcademicActivityService publishAcademicActivityService;

    @Autowired
    Session session;

    @Value("${dm.academicActivity.title:dm.assessment.}")
    String academicActivityTitle;

    @Value("${dm.academicActivity.submit.for.evaluation:submit.for.evaluation}")
    String academicActivitySubmitForEvaluation;

    @Value("${dm.academicActivity.return.for.revision:return.for.revision}")
    String academicActivityReturnForRevision;

    @Value("${dm.academicActivity.passed:passed}")
    String academicActivityPassed;

    private static Logger logger = LoggerFactory.getLogger(HelperService.class);

    public Submission completeEvaluation(Submission sub, String userId) {
        if (sub.getSubmissionId() == null || sub.getEvaluatorId() == null || sub.getStatus() == null) {
            throw new IllegalArgumentException("submission ID, evaluator ID and status are required.");
        }

        Date now = DateUtil.getZonedNow();

        Calendar submitted = Calendar.getInstance();
        submitted.setTime(now);
        int minutes = new Random().nextInt(6000);
        submitted.add(Calendar.HOUR, -minutes);

        Calendar estimated = Calendar.getInstance();
        estimated.setTime(submitted.getTime());
        estimated.add(Calendar.DATE, 3);

        Calendar evalStarted = Calendar.getInstance();
        evalStarted.setTime(now);
        minutes = new Random().nextInt(500);
        evalStarted.add(Calendar.HOUR, -minutes);

        try {
            SubmissionByIdModel submission = this.cassandraRepo.getSubmissionById(sub.getSubmissionId()).get();
            String oldStatus = submission.getStatus();
            EvaluationByIdModel currentEval = null;
            EvaluationByIdModel prevEval = null;

            if (submission.getEvaluationId() != null) {
                currentEval = this.cassandraRepo.getEvaluationById(submission.getEvaluationId()).get();
            }

            if (submission.getPreviousEvaluationId() != null) {
                prevEval = this.cassandraRepo.getEvaluationById(submission.getPreviousEvaluationId()).get();
            }

            EvaluationModel eval;

            if (currentEval != null) {
                eval = currentEval;
            } else {
                TaskByIdModel task = this.cassandraRepo.getTaskById(submission.getTaskId()).get();
                UserByIdModel user = this.cassandraRepo.getUser(sub.getEvaluatorId()).get();
                eval = new EvaluationModel(user, submission);
                eval.setDateStarted(evalStarted.getTime());

                submission.setEvaluationId(eval.getEvaluationId());
                submission.setDateStarted(evalStarted.getTime());
                submission.setEvaluatorFirstName(user.getFirstName());
                submission.setEvaluatorLastName(user.getLastName());
                submission.setEvaluatorId(sub.getEvaluatorId());

                if (prevEval != null) {
                    eval.importScoreReport(prevEval.getScoreReport());
                } else {
                    eval.setScoreReport(new ScoreReportModel(task.getRubric()));
                }
            }

            eval.complete(StatusUtil.COMPLETED);

            ScoreReportModel report = eval.getScoreReport();

            report.getScores().forEach((name, score) -> {
                logger.debug("aspect name: " + name);
                score.setAssignedScore(score.getPassingScore());
                Comment comment = new Comment();
                comment.setType(CommentTypes.STUDENT);
                comment.setComments("auto-graded aspect");
                report.setAspectComment(comment, name, submission.getAttempt(), submission.getEvaluatorId(), submission.getEvaluatorFirstName(), submission.getEvaluatorLastName());
                logger.debug("score is: " + score);
            });

            if (StatusUtil.isFailed(sub.getStatus())) {
                int fail = new Random().nextInt(report.getScores().size());
                logger.debug("random int: " + fail);
                ScoreModel score = report.getScores().values().toArray(new ScoreModel[report.getScores().size()])[fail];
                score.setAssignedScore(0);
                Comment comment = new Comment();
                comment.setType(CommentTypes.STUDENT);
                comment.setComments("auto-failed aspect");
                report.setAspectComment(comment, score.getName(), submission.getAttempt(), submission.getEvaluatorId(), submission.getEvaluatorFirstName(), submission.getEvaluatorLastName());
            }

            Comment comment = new Comment();
            comment.setType(CommentTypes.STUDENT);
            comment.setComments("auto-comment for report");
            report.setReportComment(submission.getEvaluatorId(), submission.getEvaluatorFirstName(), submission.getEvaluatorLastName(), comment, submission.getAttempt());

            if (submission.getDateSubmitted() == null) {
                submission.setDateSubmitted(submitted.getTime());
                submission.setDateEstimated(estimated.getTime());
            }

            submission.setStatus(sub.getStatus());
            submission.setDateCompleted(now);

            this.cassandraRepo.saveSubmission(submission, new EvaluationByIdModel(eval), userId, oldStatus, true);

            if (StatusUtil.isFailed(submission.getStatus())) {
                this.publishAcademicActivityService.publishAcademicActivity(submission, this.academicActivityTitle + this.academicActivityReturnForRevision + ".helperMethod");
            } else if (StatusUtil.isPassed(submission.getStatus())) {
                this.publishAcademicActivityService.publishAcademicActivity(submission, this.academicActivityTitle + this.academicActivityPassed + ".helperMethod");
            }

            return new Submission(this.cassandraRepo.getSubmissionById(submission.getSubmissionId()).get());
        } catch (Exception e) {
            logger.debug(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public void deleteSubmission(UUID submissionId) {
        this.cassandraRepo.deleteSubmission(this.cassandraRepo.getSubmissionById(submissionId).get());
        List<EvaluationBySubmissionModel> evaluations = this.cassandraRepo.getEvaluationsBySubmission(submissionId);
        evaluations.forEach(evaluation -> {
            this.cassandraRepo.deleteEvaluation(new EvaluationByIdModel(evaluation));
        });
    }

    public SubmissionData getSubmission(UUID submissionId) {
        SubmissionData data = new SubmissionData();
        data.setSubmission(new Submission(this.cassandraRepo.getSubmissionById(submissionId).get()));
        data.setEvaluations(this.cassandraRepo.getEvaluationsBySubmission(submissionId).stream().map(e -> new Evaluation(e)).collect(Collectors.toList()));
        return data;
    }

}
