package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.assessment.CourseResponse;
import edu.wgu.dmadmin.domain.assessment.Task;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.service.StudentAssessmentService;
import edu.wgu.dmadmin.util.IdentityUtil;
import edu.wgu.security.authz.annotation.IgnoreAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Jessica Pamdeth
 */
@Component
@RestController
@RequestMapping("v1/student")
public class AssessmentController {

    @Autowired
    private StudentAssessmentService studentAssessmentService;

    @Autowired
    private IdentityUtil iUtil;

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/courses/{courseId}", method = RequestMethod.GET)
    public ResponseEntity<CourseResponse> getCourse(@PathVariable final Long courseId)
            throws UserIdNotFoundException {
        String studentId = this.iUtil.getUserId();
        CourseResponse result = new CourseResponse(this.studentAssessmentService.getAssessmentsForCourse(studentId, courseId));
        return new ResponseEntity<CourseResponse>(result, HttpStatus.OK);
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/scorereport/{submissionId}", method = RequestMethod.GET)
    public ResponseEntity<Task> getScoreReport(@PathVariable final UUID submissionId)
            throws UserIdNotFoundException {
        String studentId = this.iUtil.getUserId();
        Task result = this.studentAssessmentService.getScoreReport(studentId, submissionId);
        return new ResponseEntity<Task>(result, HttpStatus.OK);
    }

    void setStudentAssessmentService(StudentAssessmentService service) {
        this.studentAssessmentService = service;
    }

    void setIdentityUtil(IdentityUtil utility) {
        this.iUtil = utility;
    }
}
