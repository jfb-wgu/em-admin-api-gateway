package edu.wgu.dmadmin.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.publish.MimeType;
import edu.wgu.dmadmin.domain.publish.PAMSCourse;
import edu.wgu.dmadmin.domain.publish.PAMSCourseVersions;
import edu.wgu.dmadmin.domain.publish.Task;
import edu.wgu.dmadmin.domain.publish.TaskDashboard;
import edu.wgu.dmadmin.domain.publish.TaskTree;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.exception.TaskNotFoundException;
import edu.wgu.dmadmin.model.publish.MimeTypeModel;
import edu.wgu.dmadmin.model.publish.TaskByAssessmentModel;
import edu.wgu.dmadmin.model.publish.TaskByCourseModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;

@Service
public class PublicationService {
	 
	private CassandraRepo cassandraRepo;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}
	
	@Autowired
	private StudyPlanService studyPlanService;
	
	public List<Task> getAllTasks() {
		List<TaskByCourseModel> result = this.cassandraRepo.getTasks();
		
		List<Task> tasks = result.stream().map(task -> new Task(task)).collect(Collectors.toList());
		Collections.sort(tasks);
		return tasks;
	}
	
	public TaskTree getTaskTree() {
		return new TaskTree(this.cassandraRepo.getTasks());
	}
	
	public TaskDashboard getTaskDashboard() {
		return new TaskDashboard(this.cassandraRepo.getTasks());
	}
	
	public List<Task> getTasksForCourse(Long courseId) {
		List<TaskByCourseModel> result = this.cassandraRepo.getTasksByCourseId(courseId);
		
		List<Task> tasks = result.stream().map(task -> new Task(task)).collect(Collectors.toList());
		Collections.sort(tasks);
		return tasks;
	}

	public List<Task> getTasksForAssessment(UUID assessmentId) {
		List<TaskByAssessmentModel> result = this.cassandraRepo.getTasksByAssessment(assessmentId);
		
		List<Task> tasks = result.stream().map(task -> new Task(task)).collect(Collectors.toList());
		Collections.sort(tasks);
		return tasks;
	}
	
	public Task getTask(UUID taskId) {
		TaskByIdModel task = this.cassandraRepo.getTaskById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
		return new Task(task);
	}

	public void addTask(Task task) {
		if (task.getTaskId() == null) {
			task.setTaskId(UUID.randomUUID());
		}
		
		if (task.getDateCreated() == null) {
			task.setDateCreated(DateUtil.getZonedNow());
		}

		this.cassandraRepo.saveTask(new TaskByCourseModel(task));
	}
	
	public void deleteTask(UUID taskId) {
		this.cassandraRepo.deleteTask(taskId);
	}

	public List<Attachment> addSupportingDocument(UUID taskId, AttachmentModel supportingDocument) {
		
        TaskByIdModel task = this.cassandraRepo.getSupportingDocuments(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        task.getSupportingDocuments().put(supportingDocument.getTitle(), supportingDocument);

		this.cassandraRepo.saveSupportingDocuments(task);
        return task.getSupportingDocuments().values().stream().map(a -> new Attachment(a, task.getCourseCode(), task.getAssessmentCode(), task.getTaskId())).collect(Collectors.toList());
    }

    public void deleteSupportingDocument(UUID taskId, String title) {
    	
        TaskByIdModel task = this.cassandraRepo.getSupportingDocuments(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        if (task.getSupportingDocuments().remove(title) != null) {
			this.cassandraRepo.saveSupportingDocuments(task);
        }
    }
	
	public PAMSCourse getCourseVersion(String courseCode) {
		List<PAMSCourse> result = this.studyPlanService.getCourseVersionsByCode(courseCode);
	    PAMSCourseVersions versions = new PAMSCourseVersions(result);
	    return versions.getCurrent();
	}
    
    public List<MimeType> getMimeTypes() {
    	return this.cassandraRepo.getMimeTypes().stream().map(mt -> new MimeType(mt)).collect(Collectors.toList());
    }
	
	public void addMimeType(MimeType type) {
		if (type.getTypeId() == null) {
			type.setTypeId(UUID.randomUUID());
		}

		this.cassandraRepo.saveMimeType(new MimeTypeModel(type));
	}
}
