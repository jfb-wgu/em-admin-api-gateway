package edu.wgu.dmadmin.service;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wgu.dmadmin.domain.publish.PAMSCourse;

@FeignClient("STUDYPLAN-SERVICE")
public interface StudyPlanService {

    @RequestMapping(value = "/api/courses/{courseCode}", method = RequestMethod.GET)
    public List<PAMSCourse> getCourseVersionsByCode(@PathVariable("courseCode") String courseCode);
}
