package edu.wgu.dmadmin.domain.report;

import java.util.UUID;
import edu.wgu.dmadmin.model.publish.AnchorModel;
import edu.wgu.dmadmin.model.publish.AspectModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import lombok.Data;

@Data
public class EmaTaskRubricRecord {
    private UUID taskId;
    private String rubricName;
    private String rubricDesc;
    private String aspectName;
    private String aspectDesc;
    private int aspectPassingScore;
    private int aspectOrder;
    private String aspectLrUrl;
    private String aspectAnchorName;
    private String aspectAnchorDesc;
    private int aspectAnchorScore;
    
    public EmaTaskRubricRecord(AnchorModel anchor, AspectModel aspect, TaskModel task) {
        this.setTaskId(task.getTaskId());
        this.setRubricName(task.getRubric().getName());
        this.setRubricDesc(task.getRubric().getDescription());
        this.setAspectName(aspect.getName());
        this.setAspectDesc(aspect.getDescription());
        this.setAspectPassingScore(aspect.getPassingScore());
        this.setAspectOrder(aspect.getOrder());
        this.setAspectLrUrl(aspect.getLrURL());
        this.setAspectAnchorName(anchor.getName());
        this.setAspectAnchorDesc(anchor.getDescription());
        this.setAspectAnchorScore(anchor.getScore());
    }
}
