package edu.wgu.dmadmin.domain.submission;

import java.util.List;

import edu.wgu.dmadmin.domain.assessment.Evaluation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubmissionData {
	Submission submission;
	List<Evaluation> evaluations;
}
