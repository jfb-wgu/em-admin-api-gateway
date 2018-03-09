package edu.wgu.dmadmin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wgu.dmadmin.domain.security.BulkCreateResponse;
import edu.wgu.dmadmin.domain.security.BulkUsers;
import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dreammachine.domain.security.User;
import edu.wgu.dreammachine.model.publish.TaskModel;
import edu.wgu.dreammachine.model.security.RoleModel;
import edu.wgu.dreammachine.model.security.UserByIdModel;
import edu.wgu.dreammachine.model.security.UserModel;

@Service
public class UserManagementService {
	PersonService personService;
	CassandraRepo cassandraRepo;

	private static Logger logger = LoggerFactory.getLogger(UserManagementService.class);

	public User getUser(String userId) {
		UserModel evaluator = this.cassandraRepo.getUserModel(userId)
				.orElseThrow(() -> new UserNotFoundException(userId));
		return new User(evaluator);
	}

	public void addUsers(String userId, List<User> users) {
		this.cassandraRepo.saveUsers(userId, users, true);
	}

	public void deleteUser(String userId) {
		this.cassandraRepo.deleteUser(userId);
	}

	public List<User> getUsers() {
		List<User> users = null;
		
		List<UserByIdModel> models = this.cassandraRepo.getUsers();
		Map<UUID, RoleModel> roles = this.cassandraRepo.getRoleMap(models);
		Map<UUID, TaskModel> tasks = this.cassandraRepo.getTaskMap();
		

		users = models.stream().map(evaluator -> new User(evaluator)).collect(Collectors.toList());
		users.forEach(user -> {
			user.getRoles().forEach(role -> {
				try {
					String roleName = roles.get(role).getRole();
					user.getRoleNames().add(roleName);
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
		return this.cassandraRepo.getUsers().stream().filter(u -> u.getTasks().contains(taskId)).map(u -> new User(u))
				.sorted().collect(Collectors.toList());
	}

	public UserModel createUser(String username) {
		Person person = this.personService.getPersonByUsername(username);

		UserByIdModel user = this.cassandraRepo.getUserModel(person.getUserId()).orElseGet(() -> {
			UserByIdModel model = new UserByIdModel();
			model.setUserId(person.getUserId());
			model.setFirstName(person.getFirstName());
			model.setLastName(person.getLastName());
			model.setEmployeeId(person.getUsername());
			return this.cassandraRepo.saveUser(model);
		});

		return user;
	}

	public BulkCreateResponse createUsers(String userId, BulkUsers users) {
		List<UserByIdModel> toCreate = new ArrayList<>();
		List<String> failed = new ArrayList<>();

		users.getUsernames().forEach(name -> {
			try {
				Person person = this.personService.getPersonByUsername(name);
				UserByIdModel user = this.cassandraRepo.getUserModel(person.getUserId()).orElseGet(() -> {
					UserByIdModel model = new UserByIdModel();
					model.setUserId(person.getUserId());
					model.setFirstName(person.getFirstName());
					model.setLastName(person.getLastName());
					model.setEmployeeId(person.getUsername());
					return model;
				});

				user.getRoles().addAll(users.getRoles());
				user.getTasks().addAll(users.getTasks());
				toCreate.add(user);
			} catch (Exception e) {
				logger.error(Arrays.toString(e.getStackTrace()));
				failed.add(name);
			}
		});
		
		List<User> created = this.cassandraRepo.saveUsers(toCreate, userId, true);
		return new BulkCreateResponse(created, failed);
	}

	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}

	@Autowired
	public void setPersonService(PersonService pService) {
		this.personService = pService;
	}
}
