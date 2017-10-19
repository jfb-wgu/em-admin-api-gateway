package edu.wgu.dmadmin.service;

import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.domain.submission.SubmissionHistoryEntry;
import edu.wgu.dmadmin.exception.AttachmentSizeException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.factory.SubmissionFactory;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentWorkService {
    private static final int DAYS_PER_TASK = 3;

    @SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(StudentWorkService.class);

    @Autowired
    private CassandraRepo cassandraRepo;

    @Autowired
    EmailService emailService;
    
    @Autowired
    SubmissionFactory factory;

    @Autowired
    PublishAcademicActivityService academicActivityService;

    @Value("${dm.academicActivity.title:dm.assessment.}")
    String academicActivityTitle;

    @Value("${dm.academicActivity.submit.for.evaluation:submit.for.evaluation}")
    String academicActivitySubmitForEvaluation;

    @Value("${dm.academicActivity.return.for.revision:return.for.revision}")
    String academicActivityReturnForRevision;

    @Value("${dm.academicActivity.passed:passed}")
    String academicActivityPassed;

    /**
     * A new Submission can be created if the student has never attempted this task before, or if the 
     * previous attempt has been evaluated and was unsuccessful.
     * 
     * @param studentId
     * @param taskId
     * @param pidm
     * @return
     * @throws SubmissionStatusException
     */
    public Submission beginSubmission(String studentId, UUID taskId, Long pidm) throws SubmissionStatusException {
    	if (studentId == null || taskId == null || pidm == null) throw new IllegalArgumentException("Null arguments not allowed");
    	
    	List<SubmissionByStudentAndTaskModel> previous = cassandraRepo.getSubmissionByStudentByTask(studentId, taskId);
    	TaskByIdModel task = cassandraRepo.getTaskForSubmission(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    	SubmissionByIdModel submission = factory.getSubmission(studentId, task, previous, pidm);

    	cassandraRepo.saveSubmission(submission, studentId, "0");
    	
    	Submission result = new Submission(submission);
    	result.setSubmissionHistory(previous.stream()
    			.filter(s -> !s.getSubmissionId().equals(submission.getSubmissionId()))
    			.map(s -> new SubmissionHistoryEntry(s))
    			.collect(Collectors.toList()));
    	return result;
    }

    public Submission saveSubmission(String studentId, UUID submissionId, Submission submission) throws SubmissionStatusException {
        SubmissionByIdModel submissionById = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId, studentId));
        String oldStatus = submissionById.getStatus();

        validateWorkStatus(submissionById);
        
        if (submission.getComments() != null)
            submissionById.setComments(submission.getComments());
        
        if (submission.getAttachments() != null) {
            submissionById.setAttachments(submission.getAttachments().stream().collect(Collectors.toMap(x -> x.getTitle(), x -> new AttachmentModel(x))));
        }

        cassandraRepo.saveSubmission(submissionById, studentId, oldStatus);
        return new Submission(cassandraRepo.getSubmissionById(submissionId).get());
    }

    public Submission addComments(String studentId, UUID submissionId, String comments) throws SubmissionStatusException {
        SubmissionByIdModel submissionById = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId, studentId));
        String oldStatus = submissionById.getStatus();

        validateWorkStatus(submissionById);
        
        if (comments == null)
            return new Submission(submissionById);
        
        submissionById.setComments(comments);
        cassandraRepo.saveSubmission(submissionById, studentId, oldStatus);

        return new Submission(cassandraRepo.getSubmissionById(submissionId).get());
    }

    public Submission submitForEvaluation(String studentId, UUID submissionId, String comments) throws SubmissionStatusException {
    	SubmissionByIdModel submissionById = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId, studentId));
    	String oldStatus = submissionById.getStatus();

        validateAttachments(submissionById);

        validateWorkStatus(submissionById, "A submission cannot be submitted more than once.");

    	submissionById.setComments(comments);
    	submissionById.setDateSubmitted(DateUtil.getZonedNow());
    	submissionById.setStatus(StatusUtil.getSubmittedStatus(submissionById.getStatus()));
    	submissionById.setDateEstimated(calculateCompletionTime());

        cassandraRepo.saveSubmission(submissionById, studentId, oldStatus);
        emailService.sendEmail(submissionById);
        academicActivityService.publishAcademicActivity(submissionById, academicActivityTitle + academicActivitySubmitForEvaluation);
    	return new Submission(cassandraRepo.getSubmissionById(submissionId).get());
    }

    // must be at least one good attachment
    // any non-URL attachment must have size > 0
    @SuppressWarnings({ "boxing", "static-method" })
	private void validateAttachments(SubmissionByIdModel submissionById) {
        if(submissionById.getAttachments() == null
                || submissionById.getAttachments().values().stream().filter(a -> a.getIsUrl() || a.getSize() > 0).count() == 0
                || submissionById.getAttachments().values().stream().filter(a -> !a.getIsUrl() && a.getSize() == 0).count() > 0){
            throw new AttachmentSizeException();
        }
    }

    private static Date calculateCompletionTime() {
        ZonedDateTime zdt = DateUtil.getZonedDateTime();
        ZonedDateTime estimatedCompletionDate = zdt.plusDays(DAYS_PER_TASK);
        return Date.from(estimatedCompletionDate.toInstant());
    }

    public Submission getSubmission(String studentId, UUID submissionId) {
        SubmissionByIdModel modelSub = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId, studentId));
        return new Submission(modelSub);
    }

    public List<Submission> getSubmissions(String studentId) {
        List<SubmissionByStudentAndTaskModel> submissions = cassandraRepo.getSubmissionsByStudentId(studentId);
        return submissions.stream().map(submission -> new Submission(submission)).collect(Collectors.toList());
    }

    public Submission cancelSubmission(String studentId, UUID submissionId) throws SubmissionStatusException {
        SubmissionByIdModel submissionById = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId, studentId));
        String oldStatus = submissionById.getStatus();

        if (StatusUtil.isSubmitted(submissionById.getStatus())) {
            submissionById.setStatus(StatusUtil.SUBMISSION_CANCELLED);
            cassandraRepo.saveSubmission(submissionById, studentId, oldStatus);
        } else {
            throw new SubmissionStatusException(submissionId, submissionById.getStatus());
        }
        
        return new Submission(cassandraRepo.getSubmissionStatus(submissionId).get());
    }

    public void addAttachment(String studentId, UUID submissionId, AttachmentModel attachment) throws SubmissionStatusException {
        if (StringUtils.isBlank(attachment.getTitle()))
            throw new IllegalArgumentException("Attachment's title must not be blank.");
        
        SubmissionByIdModel submissionById = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId, studentId));
        String oldStatus = submissionById.getStatus();

        validateWorkStatus(submissionById);
        
        submissionById.getAttachmentsNS().put(attachment.getTitle(), attachment);
        cassandraRepo.saveSubmission(submissionById, studentId, oldStatus);
    }

    public void removeAttachment(String studentId, UUID submissionId, String title) throws SubmissionStatusException {
        SubmissionByIdModel submissionById = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId, studentId));
        String oldStatus = submissionById.getStatus();

        validateWorkStatus(submissionById);
        
        if (submissionById.getAttachmentsNS().remove(title) != null) {
            cassandraRepo.saveSubmission(submissionById, studentId, oldStatus);
        }
    }

    public void removeAttachments(String studentId, UUID submissionId) throws SubmissionStatusException {
        SubmissionByIdModel submissionById = cassandraRepo.getSubmissionByStudentById(studentId, submissionId).orElseThrow(() -> new SubmissionNotFoundException(submissionId, studentId));
        String oldStatus = submissionById.getStatus();

        validateWorkStatus(submissionById);
        
        if (submissionById.getAttachmentsNS().size() > 0) {
            submissionById.getAttachmentsNS().clear();
            cassandraRepo.saveSubmission(submissionById, studentId, oldStatus);
        }
    }
    
    private static void validateWorkStatus(SubmissionByIdModel submissionById, String message) throws SubmissionStatusException {
        if (!StatusUtil.isStarted(submissionById.getStatus()))
            throw new SubmissionStatusException(message);
    }
    private static void validateWorkStatus(SubmissionByIdModel submissionById) throws SubmissionStatusException {
        validateWorkStatus(submissionById, "A submission cannot be changed after work was submitted.");
    }

    public void setCassandraRepo(CassandraRepo cassandraRepo) {
        this.cassandraRepo = cassandraRepo;
    }

    public void setPublishAcademicActivityService(PublishAcademicActivityService publishAcademicActivityService) {
        this.academicActivityService = publishAcademicActivityService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    
    public void setSubmissionFactory(SubmissionFactory inFactory) {
    	this.factory = inFactory;
    }
}
