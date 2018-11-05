package edu.wgu.dm.admin.repository;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import edu.wgu.dm.dto.publish.Competency;
import edu.wgu.dm.dto.report.EmaEvaluationAspectRecord;
import edu.wgu.dm.dto.report.EmaTaskRubricRecord;
import edu.wgu.dm.entity.publish.CompetencyEntity;
import edu.wgu.dm.repo.publish.CompetencyRepository;
import edu.wgu.dm.repo.report.EvaluationAspectReportRepository;
import edu.wgu.dm.repo.report.TaskRubricReportRepository;
import edu.wgu.dm.view.report.EvaluationAspectReportEntity;
import edu.wgu.dm.view.report.TaskRubricReportEntity;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportRepo {

    @Autowired
    CompetencyRepository competencyRepo;

    @Autowired
    TaskRubricReportRepository rubricReportRepo;

    @Autowired
    EvaluationAspectReportRepository aspectReportRepo;

    public List<Competency> getTaskCompetencies(Date datePublished) {
        return CompetencyEntity.toCompetencies(this.competencyRepo.getCompetencies(datePublished));
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date startOfDay, Date endOfDay) {
        return EvaluationAspectReportEntity.toRecords(
                this.aspectReportRepo.findByDateCompletedGreaterThanEqualAndDateCompletedLessThanEqual(startOfDay,
                        endOfDay));
    }

    public List<EmaTaskRubricRecord> getTaskRecords(Date datePublished) {
        return TaskRubricReportEntity.toTasks(this.rubricReportRepo.findByDatePublishedGreaterThanEqual(datePublished));
    }
}
