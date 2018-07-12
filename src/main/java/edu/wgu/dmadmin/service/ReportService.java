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
        Calendar cal = Calendar.getInstance();
        cal.setTime(datePublished);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return this.cassandraRepo.getCompetencies(cal.getTime());
    }

    public List<EmaTaskRubricRecord> getRubrics(Date datePublished) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datePublished);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return this.cassandraRepo.getRubrics(cal.getTime());
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date dateCompleted) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateCompleted);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return this.cassandraRepo.getEvaluationAspects(cal.getTime());
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 9999);
        return this.cassandraRepo.getEvaluationAspects(startCal.getTime(), endCal.getTime());
    }
}
