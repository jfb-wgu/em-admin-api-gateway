package edu.wgu.dm.service.feign;

import edu.wgu.dm.dto.security.Person;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "${wgu.security.ping.personSvcUrl}", name = "PERSON-SERVICE-API")
public interface PersonService {

    @GetMapping(value = "/{username}")
    Person getPersonByUsername(@PathVariable("username") final String username);

    @GetMapping(value = "/bannerId/{bannerId}")
    Person getPersonByBannerId(@PathVariable("bannerId") final String bannerId);
}
