package edu.wgu.dm.admin.service;

import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.dm.admin.repository.UserRepo;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.service.feign.PersonService;

@Service
public class UserInfoService {

    @Autowired
    PersonService personService;

    @Autowired
    UserRepo repo;

    public Person getPersonFromRequest(HttpServletRequest request, String userId) throws ParseException {
        String auth = request.getHeader("authorization");

        if (auth != null) {
            Person person = new Person(auth);

            if (person.getIsEmployee()
                      .booleanValue()) {
                person.setUserInfo(this.repo.getUserById(person.getUserId()));
            }

            return person;
        }

        return getPersonByUserId(userId);
    }

    public Person getPersonByUserId(String userId) {
        Person person = this.personService.getPersonByBannerId(userId);
        if (person.getIsEmployee()
                  .booleanValue()) {
            person.setUserInfo(this.repo.getUserById(userId));
        }
        return person;
    }
}
