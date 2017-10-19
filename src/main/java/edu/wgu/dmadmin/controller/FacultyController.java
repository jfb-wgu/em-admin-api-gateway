package edu.wgu.dmadmin.controller;

import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.assessment.CourseResponse;
import edu.wgu.dmadmin.domain.assessment.Task;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.SecureByPermissionStrategy;
import edu.wgu.dmadmin.service.StudentAssessmentService;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;
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
@RequestMapping("v1")
public class FacultyController {

    @Autowired
    private StudentAssessmentService studentAssessmentService;

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.SPOOF_STUDENT)
    @RequestMapping(value = "/students/{studentId}/courses/{courseId}", method = RequestMethod.GET)
    public ResponseEntity<CourseResponse> getCourse(@PathVariable final String studentId, @PathVariable final Long courseId) {
        CourseResponse result = new CourseResponse(this.studentAssessmentService.getAssessmentsForCourse(studentId, courseId));
        return new ResponseEntity<CourseResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.SPOOF_STUDENT)
    @RequestMapping(value = "/students/{studentId}/scorereports/{submissionId}", method = RequestMethod.GET)
    public ResponseEntity<Task> getScoreReport(@PathVariable final String studentId, @PathVariable final UUID submissionId) {
        Task result = this.studentAssessmentService.getScoreReport(studentId, submissionId);
        return new ResponseEntity<Task>(result, HttpStatus.OK);
    }
}
