package edu.wgu.dm.service;

import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.exception.UserNotFoundException;
import edu.wgu.dm.repository.SecurityRepo;
import edu.wgu.dm.service.feign.PersonService;
import java.text.ParseException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    private static final Logger log = LoggerFactory.getLogger(UserInfoService.class);
    private final PersonService personService;
    private final SecurityRepo userInfoRepo;
    private final TagService tagService;

    public UserInfoService(PersonService personService, SecurityRepo userInfoRepo, TagService tagService) {
        this.personService = personService;
        this.userInfoRepo = userInfoRepo;
        this.tagService = tagService;
    }

    public Person getPersonFromRequest(HttpServletRequest request, String userId) throws ParseException {

        String auth = request.getHeader("authorization");
        if (auth != null) {
            Person person = new Person(auth);
            if (Boolean.TRUE.equals(person.getIsEmployee())) {
                person.setUserInfo(this.userInfoRepo.getUserById(person.getUserId()));
                person.setTags(tagService.getAllowedTagsForUser(userId));
            }
            return person;
        }
        return getPersonByUserId(userId);
    }

    public Person getPersonByUserId(String userId) {
        Person person = this.personService.getPersonByBannerId(userId);
        if (person == null || person.getUserId() == null) {
            log.error("Person service returned UserId[" + Encode.forJava(userId) + "] as null.");
            throw new UserNotFoundException(userId);
        }

        if (Boolean.TRUE.equals(person.getIsEmployee())) {
            person.setUserInfo(this.userInfoRepo.getUserById(userId));
            person.setTags(tagService.getAllowedTagsForUser(userId));
        }

        return person;
    }

    public Set<String> getUserPermissions(String userId) {
        return this.userInfoRepo.getPermissionsForUser(userId);
    }
}
