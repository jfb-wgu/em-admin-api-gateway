package edu.wgu.dm.admin.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import edu.wgu.dm.admin.domain.report.EmaEvaluationAspectRecord;
import edu.wgu.dm.admin.domain.report.EmaTaskRubricRecord;
import edu.wgu.dm.converter.entitydto.GenericConverter;
import edu.wgu.dm.dto.publish.Competency;
import edu.wgu.dm.ema.repo.CompetencyRepository;
import edu.wgu.dm.ema.repo.EvaluationRepository;
import edu.wgu.dm.ema.repo.PermissionRepository;
import edu.wgu.dm.ema.repo.TaskRepository;
import edu.wgu.dm.entity.evaluation.EvaluationModel;
import edu.wgu.dm.entity.publish.TaskModel;
import edu.wgu.dm.util.enums.EvaluationStatus;

public class AdminRepository {

    @Autowired
    private CompetencyRepository competencyRepo;

    @Autowired
    private EvaluationRepository evaluationRepo;
    
    @Autowired
    private TaskRepository taskRepo;
    

    @Autowired
    GenericConverter converter;


    public List<Competency> getTaskCompetencies(Date datePublished) {
        return competencyRepo.getCompetencies(datePublished).stream()
                .map(entity -> converter.toCompetency(entity)).collect(Collectors.toList());
    }

    public List<EmaEvaluationAspectRecord> getEvaluationAspects(Date startOfDay, Date endOfDay) {
        List<EmaEvaluationAspectRecord> aspectRecords = new ArrayList<>();
        List<EvaluationModel> list = evaluationRepo
                .findByDateCompletedGreaterThanEqualAndDateCompletedLessThanEqualAndStatus(
                        startOfDay, endOfDay, EvaluationStatus.COMPLETED);
        list.forEach(eval -> {
            eval.getEvaluationAspects().forEach(aspect -> {
                aspectRecords.add(converter.toEmaEvaluationAspectRecord(aspect, eval));
            });
        });
        return aspectRecords;
    }

    public List<EmaTaskRubricRecord> getTaskRecords(Date datePublished) {
        List<EmaTaskRubricRecord> taskRecords = new ArrayList<>();
        List<TaskModel> list =
                taskRepo.findByAssessmentDatePublishedGreaterThanEqual(datePublished);

        list.stream().forEach(task -> {
            task.getAspects().forEach(aspect -> {
                aspect.getAnchors().forEach(anchor -> {
                    taskRecords.add(converter.toEmaTaskRubricRecord(anchor, aspect, task));
                });
            });
        });
        return taskRecords;
    }

}
