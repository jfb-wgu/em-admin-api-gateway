package edu.wgu.dmadmin.domain.publish;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PAMSCourseTest {
	PAMSCourse courseV1;
	PAMSCourse courseV2;
	PAMSCourse courseV3;
	
	List<PAMSCourse> courses = new ArrayList<PAMSCourse>();
	
	@Before
	public void initialize() {
		courseV2 = new PAMSCourse();
		courseV2.setId(new Long(5320001));
		courseV2.setCode("C375");
		courseV2.setReviewStatus(5);
		courseV2.setCourseStudytable("B");
		courseV2.setCourseStudyId(new Long(50020));
		courseV2.setMajorVersion(2);
		courseV2.setPublishDate(new Date(1436204815151L));
		courseV2.setTitle("Survey of World History");
		courseV2.setLaunchDate(new Date(1444888800000L));
		
		courseV1 = new PAMSCourse();
		courseV1.setId(new Long(1100002));
		courseV1.setCode("C375");
		courseV1.setReviewStatus(5);
		courseV1.setCourseStudytable("A");
		courseV1.setCourseStudyId(new Long(34688));
		courseV1.setMajorVersion(1);
		courseV1.setPublishDate(new Date(1416414111089L));
		courseV1.setTitle("Survey of World History");
		courseV1.setLaunchDate(new Date(1414821600000L));

		courseV3 = new PAMSCourse();
		courseV3.setId(new Long(1100002));
		courseV3.setCode("C375");
		courseV3.setReviewStatus(5);
		courseV3.setCourseStudytable("A");
		courseV3.setCourseStudyId(new Long(34688));
		courseV3.setMajorVersion(3);
		courseV3.setPublishDate(new Date(1416454111089L));
		courseV3.setTitle("Survey of World History");
		courseV3.setLaunchDate(new Date(1414821609000L));
		
		courses.add(courseV2);
		courses.add(courseV1);
		courses.add(courseV3);
	}
	
	@Test
	public void testGetCurrentCourse() {
		PAMSCourseVersions versions = new PAMSCourseVersions();
		versions.setVersions(courses);
		
		PAMSCourse course = versions.getCurrent();
		
		assertEquals(3, course.getMajorVersion());
	}
}
