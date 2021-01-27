package edu.wgu.dm.controller;

import edu.wgu.boot.auth.Role;
import edu.wgu.boot.auth.authz.annotation.HasAnyRole;
import edu.wgu.boot.auth.authz.annotation.Secured;
import edu.wgu.boot.auth.authz.strategy.SecureByRolesStrategy;
import edu.wgu.dm.dto.response.Tag;
import edu.wgu.dm.dto.security.RoleInfo;
import edu.wgu.dm.service.SecureByPermissionStrategy;
import edu.wgu.dm.service.TagService;
import edu.wgu.dm.util.Permissions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api("Tag management services. Deactivating an existing tag will stop users from creating new referral with that tag. For e.g. On the evaluation page, evaluator wont be able to see deactivated tag in the dropdown to add on the submission")
@RequestMapping("v1/admin")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.USER_CREATE)
    @PostMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("User with ADMIN role can Create or Update a tag.")
    public ResponseEntity<Tag> createUpdateTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(this.tagService.upsertTag(tag));
    }

    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @GetMapping(value = "/tags/{tagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Employee can get the details for specified Tag.")
    public ResponseEntity<Tag> getTag(@PathVariable final Long tagId) {
        return ResponseEntity.ok(this.tagService.getTag(tagId));
    }

    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @GetMapping(value = "/tags/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Employee can list the roles that can be assigned to a Tag.")
    public ResponseEntity<List<RoleInfo>> getApplicableRoles() {
        return ResponseEntity.ok(this.tagService.getRolesThatCanBeAssignedToTag());
    }

    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @GetMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Employee can view the list of Tags.")
    public ResponseEntity<List<Tag>> getTagList() {
        return ResponseEntity.ok(this.tagService.getTags());
    }

    @Secured(strategies = {SecureByPermissionStrategy.class})
    @HasAnyRole(Permissions.SYSTEM)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/tags/{tagId}")
    @ApiOperation("User with SYSTEM role can delete the specified tag.")
    public void deleteTag(@PathVariable final Long tagId) {
        this.tagService.deleteTag(tagId);
    }


}
