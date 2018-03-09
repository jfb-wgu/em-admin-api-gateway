package edu.wgu.dmadmin.service;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jwt.SignedJWT;

import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.repo.CassandraRepo;
import net.minidev.json.JSONObject;

@Service
public class UserInfoService {
	PersonService personService;
	DirectoryService directoryService;
	CassandraRepo cassandraRepo;
	
	@Autowired
	public void setCassandraRepo(CassandraRepo repo) {
		this.cassandraRepo = repo;
	}
	
	@Autowired 
	public void setPersonService(PersonService pService) {
		this.personService = pService;
	}

	public Person getPersonFromRequest(HttpServletRequest request, String userId) throws ParseException {
		Person person = new Person();
		String auth = request.getHeader("authorization");
		
		if (auth != null) {
			String jwtToken = auth.substring(6, auth.length());
			JSONObject json = SignedJWT.parse(jwtToken).getPayload().toJSONObject();
			person.setPidm(Long.valueOf(json.get("wguPIDM").toString()));
			person.setUsername(json.get("username").toString());
			person.setFirstName(json.get("givenName").toString());
			person.setLastName(json.get("sn").toString());
			person.setStudentId(json.get("wguBannerID").toString());
	
			if ("Employee".equals(json.get("wguLevelOneRole").toString())) {
				person.setIsEmployee(Boolean.TRUE);
				person.setUserInfo(this.cassandraRepo.getUserModel(person.getUserId()).orElseThrow(() -> new UserNotFoundException(userId)));
			} else {
				person.setIsEmployee(Boolean.FALSE);
			}
			
			return person;
		}
		return getPersonByUserId(userId);
	}
	
	@SuppressWarnings("boxing")
	public Person getPersonByUserId(String userId) {
		Person person = this.personService.getPersonByBannerId(userId);
		
		if (person.getIsEmployee()) {
			person.setUserInfo(this.cassandraRepo.getUserModel(userId).orElseThrow(() -> new UserNotFoundException(userId)));
		}
		
		return person;
	}
}
