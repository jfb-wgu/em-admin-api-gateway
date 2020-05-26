package edu.wgu.dm.service.feign;

import edu.wgu.dm.dto.security.Person;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(url = "${wgu.security.ping.personSvcUrl}", name = "PERSON-SERVICE-API")
public interface PersonService {

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    Person getPersonByUsername(@PathVariable("username") final String username);

    @RequestMapping(value = "/bannerId/{bannerId}", method = RequestMethod.GET)
    Person getPersonByBannerId(@PathVariable("bannerId") final String bannerId);

    @RequestMapping(value = "/pidm/{pidm}", method = RequestMethod.GET)
    Person getPersonByPIDM(@PathVariable("pidm") final String pidm);
}
