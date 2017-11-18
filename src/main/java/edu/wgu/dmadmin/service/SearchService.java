package edu.wgu.dmadmin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.search.DateRange;
import edu.wgu.dmadmin.domain.search.SearchCriteria;
import edu.wgu.dmadmin.model.user.UserModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dreammachine.domain.submission.DashboardSubmission;
import edu.wgu.dreammachine.model.submission.SubmissionByIdModel;
import edu.wgu.dreammachine.model.submission.SubmissionModel;
import edu.wgu.dreammachine.util.DateUtil;
import edu.wgu.dreammachine.util.StatusUtil;

@Service
public class SearchService {
	CassandraRepo cassandraRepo;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}
	
	private static Logger logger = LoggerFactory.getLogger(SearchService.class);
	
	public List<DashboardSubmission> search(SearchCriteria criteria) {

		if (StringUtils.isNotBlank(criteria.getSubmissionId())) {
			logger.debug("Searching for submission ID: " + criteria.getSubmissionId());
			
			try {
				UUID submissionId = UUID.fromString(criteria.getSubmissionId());
				Optional<SubmissionByIdModel> result = this.cassandraRepo.getSubmissionById(submissionId);
				if (result.isPresent()) {
					return Arrays.asList(new DashboardSubmission(result.get()));
				} 
				return Collections.emptyList();
			} catch(IllegalArgumentException e) {
				logger.error(Arrays.toString(e.getStackTrace()));
				return Collections.emptyList();
			}
		}
		
		List<SubmissionModel> result = new ArrayList<SubmissionModel>();
			
		if (StringUtils.isNotBlank(criteria.getStudentId())) {
			logger.debug("Searching for student ID: " + criteria.getStudentId());
			result.addAll(searchByStudent(criteria));
		} else if (StatusUtil.PENDING.equals(criteria.getStatus()) 
				|| StatusUtil.WORKING.equals(criteria.getStatus())
				|| StatusUtil.HOLD.equals(criteria.getStatus())) { 
			logger.debug("Searching for status group: " + criteria.getStatus());
			result.addAll(searchByStatus(criteria));
		} else if (StringUtils.isNotBlank(criteria.getEvaluatorFirstName()) || StringUtils.isNotBlank(criteria.getEvaluatorLastName())) {
			logger.debug("Searching for evaluator name: first [" + criteria.getEvaluatorFirstName() + "] last [" + criteria.getEvaluatorLastName() + "]");
			result.addAll(searchByEvaluator(criteria));
		} else {
			logger.debug("Search for status group: " + criteria.getStatus());
			result.addAll(searchByStatus(criteria));
		}
		
		// apply date range if specified
		if (StringUtils.isNotBlank(criteria.getDateRange()) && !criteria.getDateRange().equals("timeframeAny")) {
			logger.debug("Applying date filter");
			int days = DateRange.getDaysForDateRange(criteria.getDateRange());
			Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
			searchCalendar.add(Calendar.DATE, days);
			Date searchRange = searchCalendar.getTime();
			result = result.stream().filter(s -> s.getDateUpdated().after(searchRange)).collect(Collectors.toList());
		}
		
		// apply status if specified
		if (StringUtils.isNotBlank(criteria.getStatus())) {
			logger.debug("Applying status filter");
			result = result.stream().filter(s -> s.getStatusGroup().equals(criteria.getStatus())).collect(Collectors.toList());
		}
		
		// apply evaluator first name if specified
		if (StringUtils.isNotBlank(criteria.getEvaluatorFirstName())) {
			logger.debug("Applying first name filter");
			result = result.stream().filter(s -> criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName())).collect(Collectors.toList());
		}
		
		// apply evaluator last name if specified
		if (StringUtils.isNotBlank(criteria.getEvaluatorLastName())) {
			logger.debug("Applying last name filter");
			result = result.stream().filter(s -> criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName())).collect(Collectors.toList());
		}	
		
		return result.stream().map(s -> new DashboardSubmission(s)).collect(Collectors.toList());
	}
	
	public List<? extends SubmissionModel> searchByStudent(SearchCriteria criteria) {
		
		if (CollectionUtils.isNotEmpty(criteria.getTasks())) {
			return this.cassandraRepo.getSubmissionByStudentByTasks(criteria.getStudentId(), criteria.getTasks());
		}
		return this.cassandraRepo.getSubmissionsByStudentId(criteria.getStudentId());
	}
	
	public List<? extends SubmissionModel> searchByStatus(SearchCriteria criteria) {
		if (CollectionUtils.isNotEmpty(criteria.getTasks())) {
			return this.cassandraRepo.getSubmissionsByStatusGroupAndTasks(criteria.getStatus(), criteria.getTasks());
		}
		return this.cassandraRepo.getSubmissionsByStatusGroup(criteria.getStatus());
	}
	
	public List<? extends SubmissionModel> searchByEvaluator(SearchCriteria criteria) {
		List<UserModel> evaluators = new ArrayList<UserModel>();
		
		if (StringUtils.isNotBlank(criteria.getEvaluatorFirstName())) {
			evaluators.addAll(this.cassandraRepo.getUsersByFirstName(criteria.getEvaluatorFirstName()));
		}
		
		if (StringUtils.isNotBlank(criteria.getEvaluatorLastName())) {
			evaluators.addAll(this.cassandraRepo.getUsersByLastName(criteria.getEvaluatorLastName()));
		}
		
		if (StringUtils.isNoneBlank(criteria.getEvaluatorFirstName(), criteria.getEvaluatorLastName())) {
			evaluators = evaluators.stream()
				.filter(u -> u.getFirstName().equals(criteria.getEvaluatorFirstName()))
				.filter(u -> u.getLastName().equals(criteria.getEvaluatorLastName()))
				.distinct()
				.collect(Collectors.toList());
		}
		
		if (CollectionUtils.isEmpty(evaluators)) return Collections.emptyList();
		
		List<String> userIds = evaluators.stream().map(e -> e.getUserId()).collect(Collectors.toList());
		
		if (CollectionUtils.isNotEmpty(criteria.getTasks())) {
			return this.cassandraRepo.getSubmissionsByEvaluatorsAndTasks(userIds, criteria.getTasks());
		}
		return this.cassandraRepo.getSubmissionsByEvaluators(userIds);
	}
}
