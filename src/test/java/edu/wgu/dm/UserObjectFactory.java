package edu.wgu.dm;

import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserObjectFactory {

    public static User getUser(List<Long> taskIds, List<String> permissions) {
        User user = new User();

        user.setEmployeeId("test");
        user.setFirstName("Bruce");
        user.setLastName("Wayne");
        user.setTasks(taskIds);
        user.setUserId("user");

        Role role = getRole("test");
        List<Permission> perms = new ArrayList<>();

        for (String permission : permissions) {
            perms.add(getPermission(permission));
        }

        role.setPermissions(perms);
        user.getRoles()
            .add(role);
        return user;
    }


    public static User getUser() {
        User user = new User();

        user.setEmployeeId("test");
        user.setFirstName("Bruce");
        user.setLastName("Wayne");
        user.setTasks(Arrays.asList(1L, 34L, 3454L));
        user.setUserId("user");
        user.setRoles(Arrays.asList(getRole("test")));

        return user;
    }

    public static Role getRole(String inRole) {
        Role role = new Role();

        role.setRoleId(RandomFactory.randomLong());
        role.setRole(inRole);
        role.setRoleDescription("testing");
        role.setPermissions(Arrays.asList(getPermission("test")));

        return role;
    }

    public static Permission getPermission(String inPermission) {
        Permission permission = new Permission();

        permission.setPermissionId(RandomFactory.randomLong());
        permission.setPermission(inPermission);
        permission.setLanding("landing");
        permission.setPermissionDescription("description");
        permission.setPermissionType("type");

        return permission;
    }

    public static Person getPerson(String firstName, String lastName) {
        Person person = new Person();

        person.setIsEmployee(Boolean.FALSE);
        person.setStudentId(firstName + lastName);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPidm(new Long(1234566));
        person.setUsername(firstName + "." + lastName);
        person.setPersonType("Student");
        person.setPrimaryPhone("123-555-5555");
        person.setPreferredEmail(firstName + "." + lastName + "@wgu.edu");
        person.setUserInfo(Optional.of(getUser()));

        return person;
    }
}
