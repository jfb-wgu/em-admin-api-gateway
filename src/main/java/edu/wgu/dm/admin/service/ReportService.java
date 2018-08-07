package edu.wgu.dm.admin.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.dm.admin.domain.report.EmaEvaluationAspectRecord;
import edu.wgu.dm.admin.domain.report.EmaTaskRubricRecord;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.dto.publish.Competency;
import edu.wgu.dm.util.utils.DmUtil;

@Service
public class ReportService {

    @Autowired
    private AdminRepository adminRepo;

    public List<Competency> getTaskCompetencies(Date datePublished) {
        return adminRepo.getTaskCompetencies(DmUtil.startOfDay(datePublished));
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        return adminRepo.getEvaluationAspects(DmUtil.startOfDay(startDate),
                DmUtil.endOfDay(endDate));
    }

    public List<EmaTaskRubricRecord> getRubrics(Date datePublished) {
        return adminRepo.getTaskRecords(datePublished);
    }
}
