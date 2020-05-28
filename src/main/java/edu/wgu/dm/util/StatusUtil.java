package edu.wgu.dm.util;


import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatusUtil {


    public static final String AUTHOR_WORK_SUBMITTED = "2";
    public static final String AUTHOR_WORK_RESUBMITTED = "4";
    public static final String AUTHOR_WORK_EVALUATED = "8"; // submission Locked for student
    public static final String EVALUATION_CANCELLED = "33";
    public static final String LEAD_HOLD = "128";
    public static final String ORIGINALITY_HOLD = "256";
    public static final String ARTICULATION_HOLD = "512";
    public static final String OPEN_HOLD = "1024";


    public static List<String> getStatusesForQueues(List<String> queues) {
        List<String> statuses = new ArrayList<>();

        for (String queue : queues) {
            switch (queue) {
                case Permissions.TASK_QUEUE:
                    statuses.add(AUTHOR_WORK_SUBMITTED);
                    statuses.add(AUTHOR_WORK_RESUBMITTED);
                    statuses.add(EVALUATION_CANCELLED);
                    break;

                case Permissions.ORIGINALITY_QUEUE:
                    statuses.add(ORIGINALITY_HOLD);
                    break;

                case Permissions.ARTICULATION_QUEUE:
                    statuses.add(ARTICULATION_HOLD);
                    break;

                case Permissions.OPEN_QUEUE:
                    statuses.add(OPEN_HOLD);
                    break;

                case Permissions.LEAD_QUEUE:
                    statuses.add(LEAD_HOLD);
                    break;
            }
        }

        return statuses;
    }

}
