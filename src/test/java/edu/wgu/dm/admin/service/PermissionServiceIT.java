package edu.wgu.dm.admin.service;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.dm.dto.security.Permission;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class PermissionServiceIT {

    private static Random random = new Random();

    
    @Autowired
    PermissionService permissionService;

    @Test
    public void saveNewPermissionTest() {
        // arrange
        Permission permissionDto = new Permission();
        permissionDto.setLanding("test landing");
        permissionDto.setPermission("test permission:"+random.nextInt());
        permissionDto.setPermissionType("ADMIN");
        permissionDto.setPermissionDescription("test description");
        Permission[] permissions = new Permission[1];
        permissions[0] = permissionDto;

        // act
        permissionService.savePermissions(permissions);

        // assert
        List<Permission> lst = permissionService.getPermissions()
                         .stream()
                         .filter(p -> permissionDto.getPermission()
                                                   .equalsIgnoreCase(p.getPermission()))
                         .collect(Collectors.toList());
        assertEquals(1, lst.size());
    }
}