package edu.wgu.dmadmin.controller;

import edu.wgu.common.domain.Role;
import edu.wgu.dmadmin.audit.Audit;
import edu.wgu.dmadmin.domain.publish.MimeType;
import edu.wgu.dmadmin.domain.publish.PAMSCourse;
import edu.wgu.dmadmin.domain.publish.Task;
import edu.wgu.dmadmin.domain.publish.TaskDashboard;
import edu.wgu.dmadmin.domain.publish.TaskListResponse;
import edu.wgu.dmadmin.domain.publish.TaskResponse;
import edu.wgu.dmadmin.domain.publish.TaskTree;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.service.PublicationService;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.IgnoreAuthorization;
import edu.wgu.security.authz.annotation.Secured;
import edu.wgu.security.authz.strategy.SecureByRolesStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * @author Jessica Pamdeth
 */
@RestController
@RequestMapping("v1/publication")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public ResponseEntity<TaskListResponse> getAllTasks() {
        TaskListResponse result = new TaskListResponse(this.publicationService.getAllTasks());
        return new ResponseEntity<TaskListResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/tasks/course/{courseId}", method = RequestMethod.GET)
    public ResponseEntity<TaskListResponse> getTasksForCourse(@PathVariable final Long courseId) {
        TaskListResponse result = new TaskListResponse(this.publicationService.getTasksForCourse(courseId));
        return new ResponseEntity<TaskListResponse>(result, HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/tasks/assessment/{assessmentId}", method = RequestMethod.GET)
    public ResponseEntity<TaskListResponse> getTasksForAssessment(@PathVariable final UUID assessmentId) {
        TaskListResponse result = new TaskListResponse(this.publicationService.getTasksForAssessment(assessmentId));
        return new ResponseEntity<TaskListResponse>(result, HttpStatus.OK);
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/tasks/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<TaskResponse> getTask(@PathVariable final UUID taskId) {
        TaskResponse result = new TaskResponse(this.publicationService.getTask(taskId));
        return new ResponseEntity<TaskResponse>(result, HttpStatus.OK);
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/tasks/tree", method = RequestMethod.GET)
    public ResponseEntity<TaskTree> getTaskTree() {
        return new ResponseEntity<TaskTree>(this.publicationService.getTaskTree(), HttpStatus.OK);
    }

    @Audit
    @IgnoreAuthorization
    @RequestMapping(value = "/tasks/dashboard", method = RequestMethod.GET)
    public ResponseEntity<TaskDashboard> getTaskDashbaord() {
        return new ResponseEntity<TaskDashboard>(this.publicationService.getTaskDashboard(), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void addTask(@RequestBody Task assessmentTask) {
        this.publicationService.addTask(assessmentTask);
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/tasks/{taskId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable final UUID taskId) {
        this.publicationService.deleteTask(taskId);
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/courses/{courseCode}/pams", method = RequestMethod.GET)
    public ResponseEntity<PAMSCourse> getCourseVersion(@PathVariable final String courseCode) {
        return new ResponseEntity<PAMSCourse>(this.publicationService.getCourseVersion(courseCode), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/mimetypes", method = RequestMethod.GET)
    public ResponseEntity<List<MimeType>> getMimeTypes() {
        return new ResponseEntity<List<MimeType>>(this.publicationService.getMimeTypes(), HttpStatus.OK);
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = "/mimetypes", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void addMimeType(@RequestBody final MimeType format) {
        this.publicationService.addMimeType(format);
    }

    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.SERVICE)
    @RequestMapping(value = "/task/{taskId}/supportingdocument", method = RequestMethod.POST)
    public List<Attachment> addSupportingDoc(@PathVariable final UUID taskId, @RequestBody AttachmentModel supportingDocument) {
        return this.publicationService.addSupportingDocument(taskId, supportingDocument);
    }

    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.SERVICE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/task/{taskId}/supportingdocument/{title}", method = RequestMethod.DELETE)
    public void deleteSupportingDoc(@PathVariable final UUID taskId, @PathVariable final String title) {
        this.publicationService.deleteSupportingDocument(taskId, title);
    }
}
