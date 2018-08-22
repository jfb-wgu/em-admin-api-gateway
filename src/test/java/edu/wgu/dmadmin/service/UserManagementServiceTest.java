package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.admin.service.PersonService;
import edu.wgu.dm.admin.service.UserManagementService;
import edu.wgu.dm.common.exception.UserIdNotFoundException;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.entity.publish.TaskEntity;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.repo.ema.RoleRepository;
import edu.wgu.dmadmin.test.TestObjectFactory;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementServiceTest {

    @InjectMocks
    UserManagementService service;

    @Mock
    AdminRepository repo;

    @Mock
    RoleRepository roleRepo;

    @Mock
    PersonService pService;

    @Captor
    ArgumentCaptor<ArrayList<User>> captor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    RoleEntity role1 = TestObjectFactory.getRoleModel("role1");
    RoleEntity role2 = TestObjectFactory.getRoleModel("role2");

    TaskEntity task1 = TestObjectFactory.getTaskModel();
    TaskEntity task2 = TestObjectFactory.getTaskModel();
    UserEntity user1 = TestObjectFactory.getUserModel("test1", "testing1");
    UserEntity user2 = TestObjectFactory.getUserModel("test2", "testing2");
    Person person1 = new Person();
    Person person2 = new Person();;

    Random random = new Random();

    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);

        this.user1.getRoles().add(this.role1);
        this.user1.getTasks().add(this.task1);
        this.user2.getRoles().add(this.role2);
        this.user2.getTasks().add(this.task2);

        // when(this.repo.getRoles()).thenReturn(Arrays.asList(this.role1, this.role2));
        // when(this.repo.getTaskBasics()).thenReturn(Arrays.asList(this.task1, this.task2));
        // when(this.repo.getUsers()).thenReturn(Arrays.asList(this.user1, this.user2));
        // when(this.repo.getUserModel(this.user1.getUserId())).thenReturn(Optional.of(this.user1));

        this.person1.setFirstName("Bruce");
        this.person1.setLastName("Wayne");
        this.person1.setPidm(random.nextLong());
        this.person1.setIsEmployee(Boolean.TRUE);
        this.person1.setStudentId(this.user1.getUserId());

        this.person2.setFirstName("Bruce");
        this.person2.setLastName("Almighty");
        this.person2.setPidm(random.nextLong());
        this.person2.setIsEmployee(Boolean.TRUE);
        this.person2.setStudentId(this.user2.getUserId());

        when(this.pService.getPersonByBannerId(this.user1.getUserId())).thenReturn(this.person1);

        //
        // Map<UUID, RoleModel> roleMap = Arrays.asList(this.role1, this.role2).stream()
        // .collect(Collectors.toMap(r -> r.getRoleId(), r -> r));
        // when(this.repo.getRoleMap(Arrays.asList(this.user1, this.user2))).thenReturn(roleMap);
        //
        // Map<UUID, TaskModel> taskMap = Arrays.asList(this.task1, this.task2).stream()
        // .collect(Collectors.toMap(t -> t.getTaskId(), t -> t));
        // when(this.repo.getTaskMap()).thenReturn(taskMap);
    }

    @Test
    public void testGetUser() {
        when(this.repo.getUserById("123")).thenReturn(UserEntity.toUser(user1));
        this.service.getUser("123");
        verify(this.repo).getUserById("123");
    }

    @Test
    public void testGetUserFail() {
        when(this.repo.getUserById("none")).thenReturn(Optional.empty());
        this.thrown.expect(UserIdNotFoundException.class);
        this.service.getUser("none");
    }


    @Test
    public void testAddUser() {
        User evaluator = UserEntity.toUser(user1).get();
        this.service.addUsers(this.user1.getUserId(), Arrays.asList(evaluator));
        verify(this.repo).saveUsers(anyList());
    }

    @Test
    public void testAddUsers() {
        List<User> users = Arrays.asList(this.user1, this.user2).stream()
                .map(u -> UserEntity.toUser(u).get()).collect(Collectors.toList());

        when(this.pService.getPersonByUsername(user1.getUserId())).thenReturn(this.person1);
        when(this.pService.getPersonByUsername(user2.getUserId())).thenReturn(this.person2);
        service.addUsers(user1.getUserId(), users);


        // verify(this.service).checkIfSystemUser( anyList(), eq(this.user1.getUserId()));
        verify(repo).saveUsers(captor.capture());
        System.out.println(captor.getValue());
        assertEquals(user1.getUserId(), this.captor.getValue().get(0).getUserId());
        assertEquals(user2.getUserId(), this.captor.getValue().get(1).getUserId());
    }

    @Test
    public void testDeleteEvaluator() {
        this.service.deleteUser("123");
        verify(this.repo).deleteUser("123");
    }

    @Test
    public void testGetUsers() {
        when(service.getUsers())
                .thenReturn(UserEntity.toUsers(Arrays.asList(this.user1, this.user2)));
        List<User> result = service.getUsers();
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRoleNames().size());
    }

    @Test
    public void testGetUsersMissingData() {
        when(this.repo.getAllRoles()).thenReturn(RoleEntity.toRoles(Arrays.asList(this.role1)));
        when(service.getUsers())
                .thenReturn(UserEntity.toUsers(Arrays.asList(this.user1, this.user2)));
        List<User> result = this.service.getUsers();
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRoleNames().size());
        assertTrue(result.get(0).getRoleNames().contains(role1.getRole()));
        assertTrue(result.get(1).getTaskNames().contains(task2.getTaskName()));
    }


    @Test
    public void testGetUsersForTask() {
        when(repo.getAllUsers()).thenReturn(Arrays.asList(UserEntity.toUser(user2).get()));
        List<User> result = this.service.getUsersForTask(this.task2.getTaskId());
        assertEquals(result.get(0).getUserId(), this.user2.getUserId());
    }


    @Test
    public void testCreateUser() {
        when(this.repo.getUserById(anyString())).thenReturn(Optional.empty());
        when(this.pService.getPersonByUsername(user1.getUserId())).thenReturn(this.person1);
        when(repo.saveUser(any())).thenReturn(UserEntity.toUser(user1));
        this.service.createUser(user1.getUserId());

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(this.repo).saveUser(argument.capture());
        assertEquals("Bruce", argument.getValue().getFirstName());
        assertEquals("Wayne", argument.getValue().getLastName());
    }

}
