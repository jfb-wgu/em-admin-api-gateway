package edu.wgu.dmadmin.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.dmadmin.domain.report.Competency;
import edu.wgu.dmadmin.domain.report.EmaEvaluationAspectRecord;
import edu.wgu.dmadmin.domain.report.EmaTaskRubricRecord;
import edu.wgu.dmadmin.repo.CassandraRepo;

@Service
public class ReportService {

	@Autowired
	private CassandraRepo cassandraRepo;

	public List<Competency> getTaskCompetencies(Date datePublished) {
	    return this.cassandraRepo.getCompetencies(datePublished);
	}
	
	public List<EmaTaskRubricRecord> getRubrics(Date datePublished) {
	    return this.cassandraRepo.getRubrics(datePublished);
	}
	
	public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date dateCompleted) {
	    return this.cassandraRepo.getEvaluationAspects(dateCompleted);
	}
}
