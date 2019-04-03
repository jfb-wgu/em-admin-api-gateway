package edu.wgu.dmadmin.test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.util.DateUtil;

@SuppressWarnings("boxing")
public class TestObjectFactory {
    static Random random = new Random();

    public static UserSummary getUserSummary(String firstName, String lastName) {
        UserSummary user = new UserSummary();

        user.setUserId(firstName + lastName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmployeeId(firstName + "." + lastName);
        user.setLastLogin(DateUtil.getZonedNow());

        return user;
    }

    public static User getUser(String firstName, String lastName) {
        User user = new User();

        user.setUserId(firstName + lastName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmployeeId(firstName + "." + lastName);
        user.setLastLogin(DateUtil.getZonedNow());
        user.setRoles(new ArrayList<>());
        user.setTasks(new ArrayList<>());
        user.setTeams(new ArrayList<>());

        return user;
    }

    public static Role getRole(String roleName) {
        Role model = new Role();

        model.setRole(roleName);
        model.setPermissions(new ArrayList<>());
        model.setRoleDescription("role description");
        model.setRoleId(random.nextLong());
        model.setDateCreated(DateUtil.getZonedNow());

        return model;
    }

    public static Permission getPermission(String permissionName) {
        Permission perm = new Permission();

        perm.setPermission(permissionName);
        perm.setLanding("landing");
        perm.setPermissionDescription("permission description");
        perm.setPermissionId(random.nextLong());
        perm.setPermissionType("type");
        perm.setDateCreated(DateUtil.getZonedNow());

        return perm;
    }

    public static Person getPerson(String firstName, String lastName) {
        Person person = new Person();

        person.setIsEmployee(Boolean.FALSE);
        person.setStudentId(firstName + lastName);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPidm(Long.valueOf(1234566));
        person.setUsername(firstName + "." + lastName);
        person.setPersonType("Student");
        person.setPrimaryPhone("123-555-5555");
        person.setPreferredEmail(firstName + "." + lastName + "@wgu.edu");
        person.setUserInfo(Optional.of(TestObjectFactory.getUser(firstName, lastName)));

        return person;
    }
}
