package edu.wgu.dmadmin.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.feedback.StudentFeedback;
import edu.wgu.dmadmin.model.feedback.StudentFeedbackModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;

@Service
public class FeedbackService {
    @SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    @Autowired
    private CassandraRepo cassandraRepo;

    public void saveStudentFeedback(String studentId, StudentFeedback feedback) {
		feedback.setStudentId(studentId);
		feedback.setStudentRatingId(UUID.randomUUID());
		feedback.setDateRated(DateUtil.getZonedNow());
    	cassandraRepo.saveStudentFeedback(new StudentFeedbackModel(feedback));
    }
    
    public List<StudentFeedback> getStudentFeedback(String studentId) {
    	return cassandraRepo.getFeedbackFromStudent(studentId).stream().map(feedback -> new StudentFeedback(feedback)).collect(Collectors.toList());
    }
    
    public boolean hasStudentFeedback(String studentId) {
    	return cassandraRepo.getFeedbackFromStudent(studentId).size() > 0;
    }
    
    public void setCassandraRepo(CassandraRepo repo) {
    	this.cassandraRepo = repo;
    }
}
