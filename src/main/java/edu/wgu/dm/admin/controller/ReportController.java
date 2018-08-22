package edu.wgu.dm.admin.controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import edu.wgu.common.domain.Role;
import edu.wgu.dm.admin.service.ReportService;
import edu.wgu.dm.audit.Audit;
import edu.wgu.dm.dto.admin.report.EmaEvaluationAspectRecord;
import edu.wgu.dm.dto.admin.report.EmaTaskRubricRecord;
import edu.wgu.dm.dto.publish.Competency;
import edu.wgu.security.authz.annotation.HasAnyRole;
import edu.wgu.security.authz.annotation.Secured;
import edu.wgu.security.authz.strategy.SecureByRolesStrategy;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Jessica Pamdeth
 *
 */
@RestController
@RequestMapping("v1/reports")
public class ReportController {

    @Autowired
    private ReportService service;

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = {"/competencies/{datePublished}"}, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("List competencies for configured assessment tasks.")
    @ApiImplicitParam(name = "Authorization", value = "employee", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<List<Competency>> getCompetencies(
            @PathVariable @DateTimeFormat(iso=ISO.DATE) final Date datePublished) {
        return ResponseEntity.ok(this.service.getTaskCompetencies(datePublished));
    }

    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = {"/rubrics/{datePublished}"}, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("List rubric aspects and anchors for configured assessment tasks.")
    @ApiImplicitParam(name = "Authorization", value = "employee", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<List<EmaTaskRubricRecord>> getRubrics(
            @PathVariable @DateTimeFormat(iso=ISO.DATE) final Date datePublished) {
        return ResponseEntity.ok(this.service.getRubrics(datePublished));
    }
 
    @Audit
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @RequestMapping(value = {"/evaluation/aspects/{startDate}/{endDate}"}, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("List evaluation aspects for completed evaluations.")
    @ApiImplicitParam(name = "Authorization", value = "employee", dataType = "string",
            paramType = "header", required = true)
    public ResponseEntity<List<EmaEvaluationAspectRecord>> getEvaluationAspects(
            @PathVariable @DateTimeFormat(iso=ISO.DATE) final Date startDate,
            @PathVariable @DateTimeFormat(iso=ISO.DATE) final Date endDate) {
        return ResponseEntity.ok(this.service.getEvaluationAspects(startDate, endDate));
    }
}
