package edu.wgu.dmadmin.service;

import java.util.Calendar;
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
        return this.cassandraRepo.getCompetencies(startOfDay(datePublished));
    }

    public List<EmaTaskRubricRecord> getRubrics(Date datePublished) {
        return this.cassandraRepo.getRubrics(startOfDay(datePublished));
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date dateCompleted) {
        return this.cassandraRepo.getEvaluationAspects(startOfDay(dateCompleted));
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        
        return this.cassandraRepo.getEvaluationAspects(startOfDay(startDate), endOfDay(endDate));
    }
    
    private static Date startOfDay(Date input) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(input);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTime();
    }
    
    private static Date endOfDay(Date input) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(input);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 9999);
        
        return cal.getTime();
    }
}
