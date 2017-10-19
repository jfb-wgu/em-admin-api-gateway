package edu.wgu.dmadmin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import edu.wgu.autoconfigure.security.EnableOAuth2Transaction;
import edu.wgu.autoconfigure.security.OAuth2TransactionType;
import edu.wgu.dreamcatcher.client.DreamCatcherClient;
import edu.wgu.dreamcatcher.domain.model.AssessmentModel;
import edu.wgu.dreamcatcher.domain.model.TaskModel;
import edu.wgu.dmadmin.config.ApplicationContextHolder;
import edu.wgu.dmadmin.messaging.MessageSender;
import edu.wgu.dmadmin.messaging.QueueNames;
import edu.wgu.dmadmin.model.publish.TaskByAssessmentModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.StatusUtil;

@Service
public class SubmissionUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionUpdateService.class);

    @Autowired
    MessageSender messageSender;

    @Autowired
    DreamCatcherClient arpIntegrationService;

    /**
     * External university systems track the student's progress through their assessments.  When the
     * status for a submission changes, those systems need to be notified so they can update the 
     * official record.
     * 
     * An assessment is passed when all of its tasks have passing evaluations.  To support that
     * determination in the external system, a list of all tasks for the assessment is sent when
     * the first submission is received for any of those tasks by the student.  From there, each
     * task is updated individually as work progresses on the related evaluations.
     */
	public void notify(String assessmentCode, UUID assessmentId, UUID taskId, String taskName, 
			String studentId, String evaluatorId, Date dateUpdated,	String oldStatus, String newStatus, 
			UUID submissionId) {
		
    	// if the status has changed, notify ARP
    	if (!newStatus.equals(oldStatus)) {
	        AssessmentModel model = new AssessmentModel();
	        model.setAssessmentCode(assessmentCode);
	        model.setAssessmentId(assessmentId.toString());
	        model.setStudentId(studentId);
	        
	        List<TaskModel> tasks = new ArrayList<TaskModel>();

	        // if this is a new submission, check if it's the first for the assessment
	    	if (StatusUtil.isStarted(newStatus)) {
	    		CassandraRepo cassandraRepo = ApplicationContextHolder.getContext().getBean(CassandraRepo.class);
	    		int submissions = cassandraRepo.getCountSubmissionsByStudentAndAssessment(studentId, assessmentId);
	    		if (submissions == 0) {
	                List<TaskByAssessmentModel> basicTasks = cassandraRepo.getBasicTasksByAssessment(assessmentId);
	                basicTasks.stream().filter(t -> !t.getTaskId().equals(taskId)).forEach(assessmentTask -> {
	                    TaskModel task = new TaskModel();
	                    task.setDateUpdated(dateUpdated);
	                    task.setStatus(Integer.valueOf(0));
	                    task.setTaskId(assessmentTask.getTaskId().toString());
	                    task.setTaskName(assessmentTask.getTaskName());
	                    tasks.add(task);
	                });
	    		}
	    	}	

	    	// handle the current task
	        TaskModel task = new TaskModel();
	        task.setDateUpdated(dateUpdated);
	        task.setEvaluatorId(evaluatorId);
	        task.setStatus(Integer.valueOf(newStatus));
	        task.setSubmissionId(submissionId.toString());
	        task.setTaskId(taskId.toString());
	        task.setTaskName(taskName);
	        tasks.add(task);
	        
	        model.setTasks(tasks);
	        messageSender.sendUpdate(model);
    	}
	}
    
    @Retryable(value = { RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 3000, maxDelay = 6000))
    @EnableOAuth2Transaction(OAuth2TransactionType.ALWAYS)
    public void sendUpdates(AssessmentModel model) {
        arpIntegrationService.submit(model);
    }

    @Recover
    public void recover(AssessmentModel studentAssessment) {
        messageSender.sendUpdate(studentAssessment);
    }

    @RabbitListener(queues = QueueNames.SEND_UPDATES_QUEUE)
    @EnableOAuth2Transaction(OAuth2TransactionType.ALWAYS)
    public void onArpUpdateRequest(@Payload final AssessmentModel model) {
        String studentId = model.getStudentId();
        String assessmentCode = model.getAssessmentCode();
        logger.debug(String.format("Started processing Assessment details for studentId: %s and assessment: %s.", studentId, assessmentCode));

        try {
            arpIntegrationService.submit(model);
        } catch (Exception e) {
            logger.error(String.format("Failed to send update for studentId: %s and assessment: %s %s.", studentId, assessmentCode, e.getMessage()));
            logger.debug(Arrays.toString(e.getStackTrace()));
            messageSender.sendError(model);
        }

        logger.debug(String.format("Finished processing Assessment update for studentId: %s and assessment: %s. ", studentId, assessmentCode));
    }

    @Retryable(value = { RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 3000, maxDelay = 6000))
    @RabbitListener(queues = QueueNames.ERROR_QUEUE)
    @EnableOAuth2Transaction(OAuth2TransactionType.ALWAYS)
    public void onErrorRequest(@Payload final AssessmentModel model) {
        String studentId = model.getStudentId();
        String assessmentCode = model.getAssessmentCode();
        sendUpdates(model);
        logger.debug(String.format("Finished processing Assessment update for studentId: %s and assessment: %s. ", studentId, assessmentCode));
    }
    
    public void setMessageSender(MessageSender sender) {
    	this.messageSender = sender;
    }
    
    public void setDreamCatcherClient(DreamCatcherClient client) {
    	this.arpIntegrationService = client;
    }
}
