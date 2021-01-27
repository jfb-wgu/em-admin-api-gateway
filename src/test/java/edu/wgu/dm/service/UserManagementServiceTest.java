package edu.wgu.dm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import edu.wgu.dm.repository.RoleRepo;
import edu.wgu.dm.repository.UserRepo;
import edu.wgu.dm.exception.UserNotFoundException;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.service.feign.PersonService;
import edu.wgu.dm.TestObjectFactory;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementServiceTest {

    @InjectMocks
    UserManagementService service;

    @Mock
    UserRepo repo;

    @Mock
    RoleRepo roleRepo;

    @Mock
    PersonService pService;

    @Captor
    ArgumentCaptor<List<User>> captor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    User user1 = TestObjectFactory.getUser("Bruce", "Wayne");
    User user2 = TestObjectFactory.getUser("Bruce", "Almighty");

    UserSummary summary1 = TestObjectFactory.getUserSummary("Bruce", "Wayne");
    UserSummary summary2 = TestObjectFactory.getUserSummary("Bruce", "Almighty");

    Person person1 = TestObjectFactory.getPerson("Bruce", "Wayne");
    Person person2 = TestObjectFactory.getPerson("Bruce", "Almighty");

    Random random = new Random();

    Role role1 = TestObjectFactory.getRole("role1");
    Role role2 = TestObjectFactory.getRole("role2");

    Long taskId1 = this.random.nextLong();
    Long taskId2 = this.random.nextLong();

    @Before
    public void initialize() {


        this.user1.getRoles()
                  .add(new Role(this.role1.getRoleId()));
        this.user1.getTasks()
                  .add(this.taskId1);
        this.user2.getRoles()
                  .add(new Role(this.role2.getRoleId()));
        this.user2.getTasks()
                  .add(this.taskId2);

        this.person1.setPidm(this.random.nextLong());
        this.person1.setIsEmployee(Boolean.TRUE);
        this.person1.setStudentId(this.user1.getUserId());

        this.person2.setPidm(this.random.nextLong());
        this.person2.setIsEmployee(Boolean.TRUE);
        this.person2.setStudentId(this.user2.getUserId());
    }

    @Test
    public void testGetUser() {
        when(this.repo.getUserById(this.user1.getUserId())).thenReturn(Optional.ofNullable(this.user1));
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        this.service.getUser(this.user1.getUserId());

        verify(this.repo).getUserById(argument.capture());
        assertEquals(this.user1.getUserId(), argument.getValue());
    }

    @Test
    public void testGetUserFail() {
        when(this.repo.getUserById("none")).thenReturn(Optional.empty());
        this.thrown.expect(UserNotFoundException.class);
        this.service.getUser("none");
    }


    @Test
    public void testAddUser() {
        User user = this.user1;
        this.service.saveUser("admin", user);
        verify(this.repo).saveUser(user);
    }

    @Test
    public void testDeleteEvaluator() {
        this.service.deleteUser("123");
        verify(this.repo).deleteUser("123");
    }

    @Test
    public void testGetUsers() {
        List<UserSummary> users = Arrays.asList(this.summary1, this.summary2);
        when(this.service.getUsers()).thenReturn(users);

        List<UserSummary> result = this.service.getUsers();

        assertEquals(2, result.size());
        assertEquals(this.summary2.getLastName(), result.get(1)
                                                        .getLastName());
    }

    @Test
    public void testGetUsersForTask() {
        List<UserSummary> usersForTask = Arrays.asList(this.summary1);
        when(this.repo.getUsersByTask(this.taskId1)).thenReturn(usersForTask);

        List<UserSummary> result = this.service.getUsersForTask(this.taskId1);

        assertEquals(result.get(0)
                           .getUserId(),
                this.summary1.getUserId());
    }


    @Test
    public void testCreateUser() {
        when(this.repo.getUserById(anyString())).thenReturn(Optional.empty());
        when(this.pService.getPersonByUsername(this.person1.getUserId())).thenReturn(this.person1);
        when(this.repo.saveUser(any())).thenReturn(Optional.of(this.user1));

        this.service.createUser(this.user1.getUserId());

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(this.repo).saveUser(argument.capture());
        assertEquals(this.user1.getFirstName(), argument.getValue()
                                                        .getFirstName());
        assertEquals(this.user1.getLastName(), argument.getValue()
                                                       .getLastName());
    }
}
