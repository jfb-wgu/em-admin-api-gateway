package edu.wgu.dmadmin.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.domain.user.User;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.user.UserByIdModel;
import edu.wgu.dmadmin.model.user.UserModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dreammachine.model.publish.TaskModel;

@Service
public class UserManagementService {
	PersonService personService;
	CassandraRepo cassandraRepo;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}

	@Autowired 
	public void setPersonService(PersonService pService) {
		this.personService = pService;
	}
	
	private static Logger logger = LoggerFactory.getLogger(UserManagementService.class);
	
	public User getUser(String userId) {
		UserModel evaluator = this.cassandraRepo.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));
		return new User(evaluator);
	}

	public void addUsers(List<User> users) {
		for (User user : users) {
			this.cassandraRepo.saveUser(new UserByIdModel(user));
		}
	}

	public void deleteUser(String userId) {
		this.cassandraRepo.deleteUser(userId);
	}
		
	public List<User> getUsers() {
		List<User> users = null;
		
		Map<UUID, RoleModel> roles = this.cassandraRepo.getRoles().stream().collect(Collectors.toMap(r -> r.getRoleId(), r -> r));
		Map<UUID, TaskModel> tasks = this.cassandraRepo.getTaskBasics().stream().collect(Collectors.toMap(t -> t.getTaskId(), t -> t));
		List<UserByIdModel> result = this.cassandraRepo.getUsers();

		users = result.stream().map(evaluator -> new User(evaluator)).collect(Collectors.toList());
		users.forEach(user -> {
			user.getRoles().forEach(role -> {
				try {
					user.getRoleNames().add(roles.get(role).getRole());
				} catch (NullPointerException e) {
					logger.error("Role [" + role + "] was not found.", e.getMessage());
				}
			});

			user.getTasks().forEach(task -> {
				try {
					TaskModel model = tasks.get(task);
					user.getTaskNames().add(model.getAssessmentCode() + "-" + model.getTaskName());
				} catch (NullPointerException e) {
					logger.error("Task [" + task + "] was not found.", e.getMessage());
				}
			});
		});

		return users;
	}
	
	public List<User> getUsersForTask(UUID taskId) {
		return this.cassandraRepo.getUsers().stream()
				.filter(u -> u.getTasks().contains(taskId))
				.map(u -> new User(u))
				.sorted()
				.collect(Collectors.toList());
	}

	public UserModel createUser(String username) {
		Person person = this.personService.getPersonByUsername(username);
		UserByIdModel model = new UserByIdModel();
		model.setUserId(person.getUserId());
		model.setFirstName(person.getFirstName());
		model.setLastName(person.getLastName());
		model.setEmployeeId(person.getUsername());
		this.cassandraRepo.saveUser(model);
		
		return this.cassandraRepo.getUser(model.getUserId()).get();
	}
}
