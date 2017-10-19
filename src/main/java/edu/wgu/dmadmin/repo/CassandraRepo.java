package edu.wgu.dmadmin.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import static com.datastax.driver.mapping.Mapper.Option.saveNullFields;
import edu.wgu.dmadmin.model.assessment.EvaluationAccessor;
import edu.wgu.dmadmin.model.assessment.EvaluationByEvaluatorModel;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.audit.ActivityLogByUserModel;
import edu.wgu.dmadmin.model.audit.LogAccessor;
import edu.wgu.dmadmin.model.audit.StatusLogByAssessmentModel;
import edu.wgu.dmadmin.model.feedback.StudentFeedbackAccessor;
import edu.wgu.dmadmin.model.feedback.StudentFeedbackModel;
import edu.wgu.dmadmin.model.publish.MimeTypeAccessor;
import edu.wgu.dmadmin.model.publish.MimeTypeModel;
import edu.wgu.dmadmin.model.publish.TaskAccessor;
import edu.wgu.dmadmin.model.publish.TaskByAssessmentModel;
import edu.wgu.dmadmin.model.publish.TaskByCourseModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.SecurityAccessor;
import edu.wgu.dmadmin.model.security.UserByFirstNameModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.security.UserByLastNameModel;
import edu.wgu.dmadmin.model.submission.ReferralModel;
import edu.wgu.dmadmin.model.submission.SubmissionAccessor;
import edu.wgu.dmadmin.model.submission.SubmissionByEvaluatorAndTaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStatusAndTaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStatusGroupAndTaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionLockModel;
import edu.wgu.dmadmin.service.SubmissionUpdateService;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

@Repository("cassandra")
public class CassandraRepo {

    @Autowired
    Session session;
    
    @Autowired
    SubmissionUpdateService updateService;
    
    MappingManager mappingManager;
    SecurityAccessor securityAccessor;
    SubmissionAccessor submissionAccessor;
    EvaluationAccessor evaluationAccessor;
    TaskAccessor taskAccessor;
    MimeTypeAccessor mimeTypeAccessor;
    LogAccessor logAccessor;
    StudentFeedbackAccessor studentFeedbackAccessor;
    Mapper<UserByIdModel> userMapper;
    Mapper<TaskByCourseModel> taskMapper;
    Mapper<StatusLogByAssessmentModel> statusLogByAssessmentMapper;
    Mapper<SubmissionByIdModel> submissionByIdMapper;
    Mapper<ActivityLogByUserModel> activityLogByUserMapper;
    Mapper<MimeTypeModel> mimeTypeMapper;
    Mapper<StudentFeedbackModel> studentFeedbackMapper;
    Mapper<PermissionModel> permissionMapper;
    Mapper<RoleModel> roleMapper;
    Mapper<EvaluationByIdModel> evaluationMapper;
    Mapper<SubmissionLockModel> lockMapper;
    
    @Autowired
    public CassandraRepo(Session session) {
        mappingManager = new MappingManager(session);
        securityAccessor = mappingManager.createAccessor(SecurityAccessor.class);
        submissionAccessor = mappingManager.createAccessor(SubmissionAccessor.class);
        evaluationAccessor = mappingManager.createAccessor(EvaluationAccessor.class);
        taskAccessor = mappingManager.createAccessor(TaskAccessor.class);
        logAccessor = mappingManager.createAccessor(LogAccessor.class);
        mimeTypeAccessor = mappingManager.createAccessor(MimeTypeAccessor.class);
        studentFeedbackAccessor = mappingManager.createAccessor(StudentFeedbackAccessor.class);
        userMapper = mappingManager.mapper(UserByIdModel.class);
        taskMapper = mappingManager.mapper(TaskByCourseModel.class);
        statusLogByAssessmentMapper = mappingManager.mapper(StatusLogByAssessmentModel.class);
        submissionByIdMapper = mappingManager.mapper(SubmissionByIdModel.class);
        activityLogByUserMapper = mappingManager.mapper(ActivityLogByUserModel.class);
        mimeTypeMapper = mappingManager.mapper(MimeTypeModel.class);
        studentFeedbackMapper = mappingManager.mapper(StudentFeedbackModel.class);
        permissionMapper = mappingManager.mapper(PermissionModel.class);
        roleMapper = mappingManager.mapper(RoleModel.class);
        evaluationMapper = mappingManager.mapper(EvaluationByIdModel.class);
        lockMapper = mappingManager.mapper(SubmissionLockModel.class);
    }

    public Optional<UserByIdModel> getUser(String userId) {
    	return Optional.ofNullable(securityAccessor.getByUserId(userId));
    }
    
    public UserByIdModel getPermissionsForUser(String userId) {
    	securityAccessor.updateLastLogin(DateUtil.getZonedNow(), userId);
    	return securityAccessor.getPermissionsForUser(userId);
    }

    public Optional<UserByIdModel> getUserQualifications(String userId) {
    	return Optional.ofNullable(securityAccessor.getUserQualifications(userId));
    }

    public List<UserByIdModel> getUsers() {
    	return securityAccessor.getAll().all();
    }
    
    public List<UserByIdModel> getUsersById(List<String> userIds) {
    	return securityAccessor.getUsersById(userIds).all();
    }
    
    public List<UserByLastNameModel> getUsersByLastName(String name) {
    	return securityAccessor.getUsersByLastName(name).all();
    }
    
    public List<UserByFirstNameModel> getUsersByFirstName(String name) {
    	return securityAccessor.getUsersByFirstName(name).all();
    }

    public List<UserByIdModel> getUsersForRole(UUID roleId) {
    	return securityAccessor.getUsersForRole(roleId).all();
    }

    public List<UserByIdModel> getUsersForPermission(String permission) {
    	return securityAccessor.getUsersForPermission(permission).all();
    }

    /***
     * When saving the user, update the permissions and landing pages as well.
     *
     * @param userModel
     * @return the user with updated permission values.
     */
	public UserByIdModel saveUser(UserByIdModel userModel) {
    	if (!userModel.getRoles().isEmpty()) {
    		List<RoleModel> roles = this.getRoles(userModel.getRoles().stream().collect(Collectors.toList()));
    		List<PermissionModel> permissions = this.getPermissions(roles.stream().map(role -> role.getPermissions()).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll));

	    	userModel.setPermissions(permissions.stream().map(perm -> perm.getPermission()).collect(Collectors.toSet()));
	    	userModel.setLandings(permissions.stream().map(perm -> perm.getLanding()).collect(Collectors.toSet()));
    	} else {
    		userModel.setPermissions(Collections.emptySet());
    		userModel.setLandings(Collections.emptySet());
    	}

    	userMapper.save(userModel);
		return userModel;
	}

	public void saveUsers(List<UserByIdModel> userModels) {
		userModels.forEach(user -> {
			this.saveUser(user);
		});
	}

	public void deleteUser(String userId) {
		userMapper.delete(userId);
	}

    public List<SubmissionByStatusGroupAndTaskModel> getSubmissionsByStatusGroup(String statusGroup) {
    	return submissionAccessor.getSubmissionsByStatusGroup(statusGroup).all();
    }

    public List<SubmissionByStatusGroupAndTaskModel> getSubmissionsByStatusGroupAndTask(String statusGroup, UUID taskId) {
        return submissionAccessor.getSubmissionsByStatusGroupAndTask(statusGroup, taskId).all();
    }
    
    public List<SubmissionByStatusGroupAndTaskModel> getSubmissionsByStatusGroupAndTasks(String statusGroup, List<UUID> taskIds) {
        return submissionAccessor.getSubmissionsByStatusGroupAndTasks(statusGroup, taskIds).all();
    }

    public List<SubmissionByStatusAndTaskModel> getSubmissionsByStatusesAndTasks(List<String> statuses, List<UUID> taskIds) {
        return submissionAccessor.getSubmissionsByStatusesAndTasks(statuses, taskIds).all();
    }

    public List<SubmissionByStatusAndTaskModel> getSubmissionsByStatuses(List<String> statuses) {
        return submissionAccessor.getSubmissionsByStatuses(statuses).all();
    }

    public List<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluator(String evaluatorId) {
    	return submissionAccessor.getSubmissionsByEvaluator(evaluatorId).all();
    }

    public List<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluatorAndTask(String evaluatorId, UUID taskId) {
        return submissionAccessor.getSubmissionsByEvaluatorAndTask(evaluatorId, taskId).all();
    }
    
    public List<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluatorsAndTasks(List<String> evaluatorIds, List<UUID> taskIds) {
        return submissionAccessor.getSubmissionsByEvaluatorAndTasks(evaluatorIds, taskIds).all();
    }

    public List<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluators(List<String> evaluatorIds) {
        return submissionAccessor.getSubmissionsByEvaluators(evaluatorIds).all();
    }

    public List<SubmissionByStatusAndTaskModel> getSubmissionsByStatus(String status) {
    	return submissionAccessor.getSubmissionsByStatus(status).all();
    }

    public List<SubmissionByStatusAndTaskModel> getSubmissionsByStatusAndTask(String status, UUID taskId) {
        return submissionAccessor.getSubmissionsByStatusAndTask(status, taskId).all();
    }

    public List<EvaluationByEvaluatorModel> getSubmissionsByStatusAndEvaluator(String evaluatorId, String status) {
    	return evaluationAccessor.getSubmissionsByEvaluatorIdAndStatus(status, evaluatorId).all();
    }

    public List<SubmissionByStudentAndTaskModel> getSubmissionByStudentByTask(String studentId, UUID taskId) {
        return submissionAccessor.getSubmissionsByStudentAndTask(studentId, taskId).all();
    }
    
    public List<SubmissionByStudentAndTaskModel> getSubmissionByStudentByTasks(String studentId, List<UUID> taskIds) {
        return submissionAccessor.getSubmissionsByStudentAndTasks(studentId, taskIds).all();
    }

    public SubmissionByStudentAndTaskModel getLastSubmissionByStudentAndTaskId(String studentId, UUID taskId) {
        return submissionAccessor.getLastSubmissionByStudentAndTask(studentId, taskId);
    }
    
    public List<SubmissionByStudentAndTaskModel> getSubmissionHistoryByStudentAndTask(String studentId, UUID taskId) {
        return submissionAccessor.getSubmissionHistoryByStudentAndTask(studentId, taskId).all();
    }

    public List<EvaluationBySubmissionModel> getEvaluation(String evaluatorId, UUID submissionId) {
        return evaluationAccessor.getEvaluationBySubmission(evaluatorId, submissionId).all();
    }
    
    public Optional<EvaluationByIdModel> getEvaluationById(UUID evaluationId) {
    	if (evaluationId == null) return Optional.empty();
    	return Optional.ofNullable(evaluationAccessor.getEvaluationById(evaluationId));
    }

    public List<EvaluationBySubmissionModel> getScoreReport(String evaluatorId, UUID submissionId) {
    	return evaluationAccessor.getScoreReport(evaluatorId, submissionId).all();
    }

    public List<EvaluationByEvaluatorModel> getEvaluationByEvaluaatorAndSubmission(String evaluatorId, String status, UUID submissionId) {
        return evaluationAccessor.getEvaluationByEvaluaatorAndSubmission(evaluatorId, status, submissionId).all();
    }
    
    public void saveScoreReport(EvaluationModel evaluation) {
    	evaluationAccessor.setScoreReport(evaluation.getScoreReport(), evaluation.getEvaluationId(), evaluation.getEvaluatorId(), 
    			evaluation.getSubmissionId(), evaluation.getStudentId(), evaluation.getTaskId(), evaluation.getAttempt());
    }

    public List<EvaluationBySubmissionModel> getEvaluationsBySubmission(UUID submissionId) {
    	return evaluationAccessor.getEvaluationsBySubmission(submissionId).all();
    }
    
    public void deleteEvaluation(EvaluationByIdModel evaluation) {
    	evaluationMapper.delete(evaluation);
    }
    
    public void saveEvaluation(EvaluationByIdModel model) {
    	model.setDateUpdated(DateUtil.getZonedNow());
        evaluationMapper.save(model);
    }
    
    public List<SubmissionByStudentAndTaskModel> getSubmissionsByStudentId(String studentId) {
        return submissionAccessor.getSubmissionsByStudent(studentId).all();
    }
    
    public Optional<SubmissionByIdModel> getSubmissionByStudentById(String studentId, UUID submissionId) {
    	return Optional.ofNullable(submissionAccessor.getSubmissionByIdAndStudent(submissionId, studentId));
    }
    
    public Optional<SubmissionByIdModel> getSubmissionById(UUID submissionId) {
    	return Optional.ofNullable(submissionAccessor.getSubmissionById(submissionId));
    }

    public List<SubmissionByIdModel> getSubmissionsById(List<UUID> submissionIds) {
    	return submissionAccessor.getSubmissionsById(submissionIds).all();
    }

    public Optional<SubmissionByIdModel> getConfirmationSubmission(UUID submissionId) {
        return Optional.ofNullable(submissionAccessor.getConfirmationSubmission(submissionId));
    }

    public Optional<SubmissionByIdModel> getSubmissionStatus(UUID submissionId) {
        return Optional.ofNullable(submissionAccessor.getSubmissionStatus(submissionId));
    }

    public int getCountSubmissionsByStudentAndAssessment(String studentId, UUID assessmentId) {
    	return submissionAccessor.getCountSubmissionsByStudentAndAssessment(studentId, assessmentId).all().size();
    }
    
    public List<ReferralModel> getSubmissionReferrals(UUID submissionId) {
    	return submissionAccessor.getReferrals(submissionId).getReferralsNS();
    }
	
    public List<TaskByCourseModel> getTasks() {
    	return taskAccessor.getAll().all();
    }
    
    public List<TaskByCourseModel> getTaskBasics() {
    	return taskAccessor.getAllBasics().all();
    }
    
	public List<TaskByCourseModel> getTasksByCourseId(Long courseId) {
		return taskAccessor.getTasksByCourseId(courseId).all();
	}
    
    public List<TaskByAssessmentModel> getTasksByAssessment(UUID assessmentId) {
    	return taskAccessor.getTasksByAssessment(assessmentId).all();
    }
        
    public List<TaskByAssessmentModel> getBasicTasksByAssessment(UUID assessmentId) {
        return taskAccessor.getBasicTasksByAssessment(assessmentId).all();
    }

    public Optional<TaskByIdModel> getTaskById(UUID taskId) {
    	return Optional.ofNullable(taskAccessor.getTaskById(taskId));
    }
    
    public Optional<TaskByIdModel> getTaskKeys(UUID taskId) {
    	return Optional.ofNullable(taskAccessor.getTaskKeys(taskId));
    }
    
    public Optional<TaskByIdModel> getTaskRubric(UUID taskId) {
    	return Optional.ofNullable(taskAccessor.getTaskRubric(taskId));
    }
    
    public Optional<TaskByIdModel> getTaskBasics(UUID taskId) {
    	return Optional.ofNullable(taskAccessor.getTaskBasics(taskId));
    }
    
    public Optional<TaskByIdModel> getTaskForSubmission(UUID taskId) {
    	return Optional.ofNullable(taskAccessor.getTaskForSubmission(taskId));
    }
    
    public Optional<TaskByIdModel> getSupportingDocuments(UUID taskId) {
    	return Optional.ofNullable(taskAccessor.getSupportingDocuments(taskId));
    }
    
    public void saveSupportingDocuments(TaskModel task) {
    	taskAccessor.saveSupportingDocuments(task.getSupportingDocuments(), DateUtil.getZonedNow(), task.getCourseId(), task.getAssessmentId(), task.getTaskId());
    }

    public void deleteTask(UUID taskId) {
    	Optional<TaskByIdModel> task = this.getTaskKeys(taskId);
    	if (task.isPresent()) {
    		taskMapper.delete(new TaskByCourseModel(task.get()));
    	}
    }
    
    public void saveTask(TaskByCourseModel model) {
    	model.setDateUpdated(DateUtil.getZonedNow());
    	model.setAspectCount(model.getRubric().getAspects().size());
    	taskMapper.save(model);
    }
    
    public void saveSubmissionLock(SubmissionLockModel lock) {
    	submissionAccessor.insertSubmissionLock(lock.getSubmissionId(), lock.getUserId(), lock.getLockId());
    }
    
    public void deleteSubmissionLock(SubmissionLockModel lock) {
    	lockMapper.delete(lock);
    }
    
    public List<SubmissionLockModel> getSubmissionLocks(UUID submissionId) {
    	return submissionAccessor.getSubmissionLocks(submissionId).all();
    }
    
    /**
     * Legacy method for null evaluation and default saveNullFields behavior.
     *
     * @param byId
     * @param userId
     * @param oldStatus
     */
    public void saveSubmission(SubmissionByIdModel byId, String userId, String oldStatus) {
    	this.saveSubmission(byId, null, userId, oldStatus, true);
    }

    /**
     * Use this method to save submission records.  If the status has changed for this
     * submission then insert a record into the status log and call out to the update
     * service for notifications.
     * 
     * @param SubmissionByIdModel submission
     * @param EvaluationByIdModel evaluation or null
     * @param String userId
     * @param String oldStatus
     * @param boolean saveNullFields
     */
	public void saveSubmission(SubmissionByIdModel byId, EvaluationByIdModel evaluation, String userId, String oldStatus, boolean saveNulls) {
    	BatchStatement batch = new BatchStatement();

    	byId.setDateUpdated(DateUtil.getZonedNow());
    	byId.setStatusGroup(StatusUtil.getStatusGroup(byId.getStatus()));
    	batch.add(submissionByIdMapper.saveQuery(byId, saveNullFields(saveNulls)));

    	if (evaluation != null) {
    		evaluation.setDateUpdated(DateUtil.getZonedNow());
        	batch.add(evaluationMapper.saveQuery(evaluation));
    	}

    	if (!byId.getStatus().equals(oldStatus)) {
    		batch.add(statusLogByAssessmentMapper.saveQuery(new StatusLogByAssessmentModel(oldStatus, byId, userId)));

        	updateService.notify(
        			byId.getAssessmentCode(), byId.getAssessmentId(), byId.getTaskId(), byId.getTaskName(),
        			byId.getStudentId(), byId.getEvaluatorId(), byId.getDateUpdated(), oldStatus,
        			byId.getStatus(), byId.getSubmissionId());
    	}

    	session.execute(batch);
    }

    public void deleteSubmission(SubmissionByIdModel byId) {
    	submissionByIdMapper.delete(byId);
    }

    public List<StatusLogByAssessmentModel> getStatusLogByAssessment(UUID assessmentId) {
    	return logAccessor.getByAssessmentId(assessmentId).all();
    }
    
    public String getLastStatusForSubmission(String studentId, UUID submissionId) {
    	return logAccessor.getLastStatusForSubmission(studentId, submissionId).getNewStatus();
    }
    
    public void saveActivityLogEntry(ActivityLogByUserModel model) {
    	activityLogByUserMapper.save(model);
    }
    
    public void saveMimeType(MimeTypeModel model) {
    	mimeTypeMapper.save(model);
    }
    
    public List<MimeTypeModel> getMimeTypes() {
		return mimeTypeAccessor.getAll().all();
    }

    public void saveStudentFeedback(StudentFeedbackModel model) {
    	studentFeedbackMapper.save(model);
    }
    
    public List<StudentFeedbackModel> getFeedbackFromStudent(String studentId) {
    	return studentFeedbackAccessor.getAllFeedbackForStudent(studentId).all();
    }
    
    public void savePermission(PermissionModel model) {
    	model.setDateUpdated(DateUtil.getZonedNow());
    	permissionMapper.save(model);
    }
    
    public List<PermissionModel> getPermissions() {
    	return securityAccessor.getPermissions().all();
    }
    
    public List<PermissionModel> getPermissions(List<UUID> permissionIds) {
    	return securityAccessor.getPermissions(permissionIds).all();
    }

    public Optional<PermissionModel> getPermission(UUID permissionId) {
    	return Optional.ofNullable(securityAccessor.getPermission(permissionId));
    }

    public void saveRole(RoleModel model) {
    	model.setDateUpdated(DateUtil.getZonedNow());
    	roleMapper.save(model);
    }
    
    public void deleteRole(UUID roleId) {
    	securityAccessor.deleteRole(roleId);
    }
    
    public void deletePermission(UUID permissionId, String permission) {
    	securityAccessor.deletePermission(permissionId, permission);
    }

    public Optional<RoleModel> getRole(UUID roleId) {
    	return Optional.ofNullable(securityAccessor.getRole(roleId));
    }
    
    public List<RoleModel> getRoles() {
    	return securityAccessor.getRoles().all();
    }

    public List<RoleModel> getRoles(List<UUID> roleIds) {
    	return securityAccessor.getRoles(roleIds).all();
    }

    public Optional<SubmissionByIdModel> getInternalComments(UUID submissionId) {
        return Optional.ofNullable(submissionAccessor.getSubmissionInternalComments(submissionId));
    }
}
