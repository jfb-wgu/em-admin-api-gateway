package edu.wgu.dmadmin.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dreamcatcher.domain.model.AssessmentModel;

@Service
public class MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendUpdate(AssessmentModel model) {
        String studentId = model.getStudentId();
        String assessmentCode = model.getAssessmentCode();
        logger.debug(String.format("Sending Assessment details to ARP for studentId: %s and assessment: %s. ", studentId, assessmentCode));
        this.rabbitTemplate.convertAndSend(QueueNames.SEND_UPDATES_QUEUE, model);
    }

    public void sendError(AssessmentModel model) {
        String studentId = model.getStudentId();
        String assessmentCode = model.getAssessmentCode();
        logger.debug(String.format("Retrying failed update to ARP for studentId: %s and assessment: %s. ", studentId, assessmentCode));
        this.rabbitTemplate.convertAndSend(QueueNames.ERROR_QUEUE, model);
    }
    
    public void setRabbitTemplate(RabbitTemplate template) {
        this.rabbitTemplate = template;
    }
}
