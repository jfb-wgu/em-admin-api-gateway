package edu.wgu.dmadmin.service;

import edu.wgu.dmadmin.domain.submission.AcademicActivitySubmission;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublishAcademicActivityService {

    @Autowired
    EventPublisher eventPublisher;

    /**
     * This will submit an activity to the Academic activity Servcice to record specific events.
     *
     * @param submission - the submission that is currently being used
     * @param activityType - the type of activity that is happening. This is for a Rabbit exchange, the pattern for
     *                     the exchange is dm.submission.*. You must of dm.submission for this to work, but anything after
     *                     the * is to describe the activity in dot notation. For example if a student submitted work
     *                     for evaluation the activity would be dm.submission.submit.for.evaluation.
     */
    public void publishAcademicActivity(SubmissionModel submission, String activityType){
        Submission sub = new Submission(submission);
        publishAcademicActivity(sub, activityType);
    }

    public void publishAcademicActivity(Submission submission, String activityType){
        AcademicActivitySubmission academicActivitySubmission = new AcademicActivitySubmission(submission);
        eventPublisher.publish(academicActivitySubmission, activityType, "Dream Machine");
    }
    
    public void setEventPublisher(EventPublisher publisher) {
    	this.eventPublisher = publisher;
    }
}
