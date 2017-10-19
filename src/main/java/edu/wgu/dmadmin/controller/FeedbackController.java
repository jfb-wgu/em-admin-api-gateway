package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.feedback.StudentFeedback;
import edu.wgu.dmadmin.domain.feedback.StudentFeedbackListResponse;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.service.FeedbackService;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.security.authz.annotation.IgnoreAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/feedback")
public class FeedbackController {

    @Autowired
    FeedbackService feedbackService;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/student", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void saveStudentFeedback(@RequestBody StudentFeedback feedback) throws UserIdNotFoundException {
        String studentId = this.iUtil.getUserId();
        this.feedbackService.saveStudentFeedback(studentId, feedback);
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/student/list", method = RequestMethod.GET)
    public ResponseEntity<StudentFeedbackListResponse> getStudentFeedback() throws UserIdNotFoundException {
        String studentId = this.iUtil.getUserId();
        StudentFeedbackListResponse result = new StudentFeedbackListResponse(this.feedbackService.getStudentFeedback(studentId));
        return new ResponseEntity<StudentFeedbackListResponse>(result, HttpStatus.OK);
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public boolean hasStudentFeedback() throws UserIdNotFoundException {
        String studentId = this.iUtil.getUserId();
        return this.feedbackService.hasStudentFeedback(studentId);
    }
}
