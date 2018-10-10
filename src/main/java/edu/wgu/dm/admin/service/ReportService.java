package edu.wgu.dm.admin.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.dto.publish.Competency;
import edu.wgu.dm.dto.report.EmaEvaluationAspectRecord;
import edu.wgu.dm.dto.report.EmaTaskRubricRecord;
import edu.wgu.dm.util.DateUtil;
import lombok.NonNull;

@Service
public class ReportService {

    @Autowired
    private AdminRepository adminRepo;

    public List<Competency> getTaskCompetencies(@NonNull Date datePublished) {
        return this.adminRepo.getTaskCompetencies(DateUtil.startOfDay(datePublished));
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(@NonNull Date startDate, @NonNull Date endDate) {
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        return this.adminRepo.getEvaluationAspects(DateUtil.startOfDay(startDate), DateUtil.endOfDay(endDate));
    }

    public List<EmaTaskRubricRecord> getRubrics(@NonNull Date datePublished) {
        return this.adminRepo.getTaskRecords(datePublished);
    }
}
