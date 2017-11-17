package edu.wgu.dmadmin.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dreammachine.domain.assessment.Evaluation;
import edu.wgu.dreammachine.domain.submission.Submission;
import edu.wgu.dreammachine.domain.submission.SubmissionData;
import edu.wgu.dreammachine.model.assessment.EvaluationByIdModel;
import edu.wgu.dreammachine.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dreammachine.model.submission.SubmissionAttachmentModel;
import edu.wgu.dreammachine.repo.CassandraRepo;

@Service
public class HelperService {

    @Autowired
    CassandraRepo cassandraRepo;

    private static Logger logger = LoggerFactory.getLogger(HelperService.class);

    public void deleteSubmission(UUID submissionId) {
        this.cassandraRepo.deleteSubmission(this.cassandraRepo.getSubmissionById(submissionId).get());
        List<EvaluationBySubmissionModel> evaluations = this.cassandraRepo.getEvaluationsBySubmission(submissionId);
        evaluations.forEach(evaluation -> {
            this.cassandraRepo.deleteEvaluation(new EvaluationByIdModel(evaluation));
        });
    }

    public SubmissionData getSubmission(UUID submissionId) {
        SubmissionData data = new SubmissionData();
        List<SubmissionAttachmentModel> attachments = this.cassandraRepo.getAttachmentsForSubmission(submissionId);
        data.setSubmission(new Submission(this.cassandraRepo.getSubmissionById(submissionId).get(), attachments));
        data.setEvaluations(this.cassandraRepo.getEvaluationsBySubmission(submissionId).stream().map(e -> new Evaluation(e)).collect(Collectors.toList()));
        return data;
    }
}
