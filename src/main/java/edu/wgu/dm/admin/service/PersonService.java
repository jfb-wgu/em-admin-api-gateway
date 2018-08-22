package edu.wgu.dm.admin.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.wgu.dm.dto.security.Person;

@FeignClient("PERSON-SERVICE-API")
public interface PersonService {

	@RequestMapping(value = "/v1/person/{username}", method = RequestMethod.GET)
	Person getPersonByUsername(@PathVariable("username") final String username);

	@RequestMapping(value = "/v1/person/bannerId/{bannerId}", method = RequestMethod.GET)
	Person getPersonByBannerId(@PathVariable("bannerId") final String bannerId);

	@RequestMapping(value = "/v1/person/pidm/{pidm}", method = RequestMethod.GET)
	Person getPersonByPIDM(@PathVariable("pidm") final String pidm);
}
