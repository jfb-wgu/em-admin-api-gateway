package edu.wgu.dmadmin.service;

import edu.wgu.dmadmin.model.submission.SubmissionModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    UserManagementService userManagementService;

    @Autowired
    PersonService personService;

    @Value("${dm.mail.host:relay.wgu.edu}")
    private String host;

    @Value("${dm.mail.port:25}")
    private String port;

    @Value("${dm.mail.environment.toAddresss}")
    private String environmentEmail;

    @Value("${dm.mail.toAddresss}")
    private String toAddress;

    @Value("${dm.mail.fromAddresss:noreply@wgu.edu}")
    private String fromAddress;

    @Value("${dm.mail.developer.email}")
    private String developerEmails;

    @Value("${dm.mail.message}")
    private String emailMessage;

    private Logger logger = Logger.getLogger(EmailService.class);


    public void sendEmail(SubmissionModel submission) {

        String toAddressesAsString;

        toAddressesAsString = setToAddress();

        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", this.host);
        properties.setProperty("mail.smtp.port", this.port);

        Session session = Session.getDefaultInstance(properties);

        try {

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(this.fromAddress));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddressesAsString));

            message.setSubject("Student assessment submitted for grading.");

            String messageText = setMessageText(submission);

            message.setText(messageText);

            Transport.send(message);
            this.logger.info("Email Sent to: " + toAddressesAsString);
        } catch (MessagingException | IllegalArgumentException mex) {
            this.logger.error(String.valueOf(mex.getCause()));
            this.logger.error(mex.getMessage());
            this.logger.error(Arrays.toString(mex.getStackTrace()));
            this.logger.info("To Email Address: " + this.toAddress);
            this.logger.info("To Email Address As String: " + toAddressesAsString);
            this.logger.info("From Address: " + this.fromAddress);
            this.logger.info("PORT: " + this.port);
            this.logger.info("HOST: " + this.host);
        }
    }

    String setMessageText(SubmissionModel submission) {
        String courseName = submission.getCourseName();
        String studentid = submission.getStudentId();
        return String.format(this.emailMessage, studentid, courseName);
    }

    String setToAddress() {
        String toAddressesAsString;
        if (this.environmentEmail != null && !this.environmentEmail.isEmpty()) {
            toAddressesAsString = this.environmentEmail;
            this.logger.info("Environment Email set as to email");
        } else {
            toAddressesAsString = this.toAddress;
            this.logger.info("To address set as To Email");
        }
        return toAddressesAsString;
    }

    public void sendTestEmail() {
        sendEmail(new SubmissionModel());
    }
}
