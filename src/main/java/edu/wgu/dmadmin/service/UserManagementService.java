package edu.wgu.dmadmin.service;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jwt.SignedJWT;

import edu.wgu.dmadmin.domain.security.Person;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dreammachine.model.publish.TaskModel;
import net.minidev.json.JSONObject;

@Service
public class UserManagementService {
	PersonService personService;
	DirectoryService directoryService;
	CassandraRepo cassandraRepo;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}
	
	@Autowired 
	public void setPersonService(PersonService pService) {
		this.personService = pService;
	}
	
	@Autowired 
	public void setDirectoryService(DirectoryService dService) {
		this.directoryService = dService;
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
	
	public Person getPersonFromRequest(HttpServletRequest request, String userId) throws ParseException {
		Person person = new Person();
		String auth = request.getHeader("authorization");
		
		if (auth != null) {
			String jwtToken = auth.substring(6, auth.length());
			JSONObject json = SignedJWT.parse(jwtToken).getPayload().toJSONObject();
			person.setPidm(Long.valueOf(json.get("wguPIDM").toString()));
			person.setUsername(json.get("username").toString());
			person.setFirstName(json.get("givenName").toString());
			person.setLastName(json.get("sn").toString());
			person.setStudentId(json.get("wguBannerID").toString());
	
			if ("Employee".equals(json.get("wguLevelOneRole").toString())) {
				person.setIsEmployee(Boolean.TRUE);
				person.setUserInfo(this.cassandraRepo.getUser(person.getUserId()).orElseThrow(() -> new UserNotFoundException(userId)));
			} else {
				person.setIsEmployee(Boolean.FALSE);
			}
			
			return person;
		}
		return getPersonByUserId(userId);
	}
	
	@SuppressWarnings("boxing")
	public Person getPersonByUserId(String userId) {
		Person person = this.personService.getPersonByBannerId(userId);
		
		if (person.getIsEmployee()) {
			person.setUserInfo(this.cassandraRepo.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId)));
		}
		
		return person;
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
	
	public Set<Person> getMissingUsers(String groupName) {
		Set<String> accountNames = this.directoryService.getMembersForGroup(groupName).stream().map(member -> member.getSAMAccountName()).collect(Collectors.toSet());
		Set<Person> missing = new HashSet<Person>();
		
		accountNames.forEach(account -> {
			try {
				logger.debug("Looking up user: " + account);
				Optional<Person> user = Optional.of(this.personService.getPersonByUsername(account));
				if (user.isPresent()) {
					Person person = user.get();
					if (!this.cassandraRepo.getUser(person.getUserId()).isPresent()) 
						missing.add(person);
				}
			} catch(Exception e) {
				logger.debug(e.getMessage());
			}
		});
		
		return missing;
	}
}
