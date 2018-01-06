package edu.wgu.dmadmin.domain.submission;

import java.util.List;

import edu.wgu.dreammachine.domain.assessment.Evaluation;
import edu.wgu.dreammachine.domain.submission.Submission;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubmissionData {
	Submission submission;
	List<Evaluation> evaluations;
}
