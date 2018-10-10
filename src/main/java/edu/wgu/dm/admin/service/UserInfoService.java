package edu.wgu.dm.admin.service;

import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nimbusds.jwt.SignedJWT;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.service.feign.PersonService;
import net.minidev.json.JSONObject;

@Service
public class UserInfoService {

    @Autowired
    PersonService personService;

    @Autowired
    AdminRepository repo;

    public Person getPersonFromRequest(HttpServletRequest request, String userId) throws ParseException {
        Person person = new Person();
        String auth = request.getHeader("authorization");

        if (auth != null) {
            String jwtToken = auth.substring(6, auth.length());
            JSONObject json = SignedJWT.parse(jwtToken)
                                       .getPayload()
                                       .toJSONObject();
            person.setPidm(Long.valueOf(json.get("wguPIDM")
                                            .toString()));
            person.setUsername(json.get("username")
                                   .toString());
            person.setFirstName(json.get("givenName")
                                    .toString());
            person.setLastName(json.get("sn")
                                   .toString());
            person.setStudentId(json.get("wguBannerID")
                                    .toString());

            if ("Employee".equals(json.get("wguLevelOneRole")
                                      .toString())) {
                person.setIsEmployee(Boolean.TRUE);
                person.setUserInfo(this.repo.getUserById(person.getUserId()));
            } else {
                person.setIsEmployee(Boolean.FALSE);
            }

            return person;
        }
        return getPersonByUserId(userId);
    }

    public Person getPersonByUserId(String userId) {
        Person person = this.personService.getPersonByBannerId(userId);
        if (person.getIsEmployee()) {
            person.setUserInfo(this.repo.getUserById(userId));
        }
        return person;
    }
}
