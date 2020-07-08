package edu.wgu.dm.service;

import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.exception.UserNotFoundException;
import edu.wgu.dm.repository.SecurityRepo;
import edu.wgu.dm.service.feign.PersonService;
import java.text.ParseException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoService {

    private final PersonService personService;
    private final SecurityRepo userInfoRepo;

    public Person getPersonFromRequest(HttpServletRequest request, String userId) throws ParseException {

        String auth = request.getHeader("authorization");
        if (auth != null) {
            Person person = new Person(auth);
            if (Boolean.TRUE.equals(person.getIsEmployee())) {
                person.setUserInfo(this.userInfoRepo.getUserById(person.getUserId()));
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
        }

        return person;
    }

    public Set<String> getUserPermissions(String userId) {
        return this.userInfoRepo.getPermissionsForUser(userId);
    }
}
