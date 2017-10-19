package edu.wgu.dmadmin.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)

public class EmailServiceTest {
    @InjectMocks
    private EmailService emailService;

    @Mock
    private Appender mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    private String emailTemplate = "Student: %s has submitted an evaluation for grading for course: %s";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        try {
            Field port = findField(emailService.getClass(), "port");
            Field host = findField(emailService.getClass(), "host");
            Field developerEmail = findField(emailService.getClass(), "developerEmails");
            Field environmentEmail = findField(emailService.getClass(), "environmentEmail");
            Field emailMessage = findField(emailService.getClass(), "emailMessage");
            Field toAddress = findField(emailService.getClass(), "toAddress");
            Field fromAddress = findField(emailService.getClass(), "fromAddress");

            makeAccessible(port);
            makeAccessible(host);
            makeAccessible(developerEmail);
            makeAccessible(environmentEmail);
            makeAccessible(emailMessage);
            makeAccessible(toAddress);
            makeAccessible(fromAddress);

            setField(port, emailService, "25");
            setField(host, emailService, "relay.wgu.edu");
            setField(developerEmail, emailService, "joshua.barnett@wgu.edu,jessica.pamdeth@wgu.edu");
            setField(environmentEmail, emailService, "environment@wgu.edu");
            setField(emailMessage, emailService, "Student: %s has submitted an evaluation for grading for course: %s");
            setField(toAddress, emailService, "EMAProduct@wgu.edu");
            setField(fromAddress, emailService, "noreply@wgu.edu");
        } catch (Exception e) {
            System.out.println("Exception");
        }

        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }

    @After
    public void tearDown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(mockAppender);
    }

    @Test
    public void testSendEmail() throws Exception {

        SubmissionModel sub = new SubmissionModel();
        sub.setStudentId("2345");
        sub.setCourseName("The Awesomest Course Ever!");

        emailService.sendEmail(sub);
        assertLogMessages("Email Sent to: environment@wgu.edu", "PORT: 25");
    }


    @Test
    public void testSetTooAddress() throws Exception {
        String toAddressString = "toAddress@wgu.egu";

        Field environmentEmail = findField(emailService.getClass(), "environmentEmail");
        Field toAddress = findField(emailService.getClass(), "toAddress");
        makeAccessible(environmentEmail);
        makeAccessible(toAddress);
        setField(environmentEmail, emailService, "");
        setField(toAddress, emailService, toAddressString);

        assertEquals(toAddressString, emailService.setToAddress());
    }

    @Test
    public void testSetTooAddress2() throws Exception {
        String toAddressString = "toAddress@wgu.egu";

        Field environmentEmail = findField(emailService.getClass(), "environmentEmail");
        Field toAddress = findField(emailService.getClass(), "toAddress");
        makeAccessible(environmentEmail);
        makeAccessible(toAddress);
        setField(environmentEmail, emailService, null);
        setField(toAddress, emailService, toAddressString);

        assertEquals(toAddressString, emailService.setToAddress());
    }

    @Test
    public void testSetTooAddress3() throws Exception {
        String envEmailAddress = "borg.dev@wgu.edu";

        Field environmentEmail = findField(emailService.getClass(), "environmentEmail");
        makeAccessible(environmentEmail);
        setField(environmentEmail, emailService, envEmailAddress);


        assertEquals(envEmailAddress, emailService.setToAddress());
    }

    //This is to test that different students and courses will have a different email message.
    @Test
    public void testSetMessageText() throws Exception {
        String courseName = "The Awesomest Course Ever!";
        String studentId = "2345";
        String expectedMessage = String.format(emailTemplate, studentId, courseName);

        SubmissionModel sub = new SubmissionModel();
        sub.setStudentId(studentId);
        sub.setCourseName(courseName);

        String message = emailService.setMessageText(sub);

        assertEquals(expectedMessage, message);

        String courseName2 = "The Worst Course Ever!";
        String studentId2 = "131313";
        String expectedMessage2 = String.format(emailTemplate, studentId2, courseName2);

        SubmissionModel sub2 = new SubmissionModel();
        sub2.setStudentId(studentId2);
        sub2.setCourseName(courseName2);

        String message2 = emailService.setMessageText(sub2);

        assertEquals(expectedMessage2, message2);
    }

    private void assertLogMessages(String... expectedMessages) {
        int messagesFound = 0;
        int i = 0;

        verify(mockAppender, atLeastOnce()).doAppend(captorLoggingEvent.capture());
        if (captorLoggingEvent.getAllValues().size() > 1) {
            final List<LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();

            if (loggingEvents.size() >= expectedMessages.length) {
                for (LoggingEvent event : loggingEvents) {
                    if (event.getLevel().equals(Level.INFO) || event.getLevel().equals(Level.ERROR)) {
                        List<String> mesageList = Arrays.asList(expectedMessages);
                        for (String message : mesageList) {
                            if (message.equals(event.getMessage())) {
                                messagesFound++;
                                break;
                            }
                        }
                    }
                }
            } else {
                fail("Log size does not match expected number of log messages");
            }
        } else {
            assertSingleLog(expectedMessages[0]);
        }
        assertTrue(messagesFound > 0);
    }

    private void assertSingleLog(String expectedLogMessage) {
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        assertTrue(loggingEvent.getLevel().equals(Level.INFO));
        assertThat(loggingEvent.getFormattedMessage(), is(expectedLogMessage));
    }

}