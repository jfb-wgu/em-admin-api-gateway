package edu.wgu.dmadmin.service;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("EVENT-PUBLISHER")
/*
//use this one for local testing/debugging
@FeignClient(value = "EVENT-PUBLISHER", url = "localhost:8081")
*/
public interface EventPublisher {
    @RequestMapping(
            value = {"/v1/api/publish/{topic}/{application}"},
            method = {RequestMethod.POST},
            consumes = {"application/json"}
    )
    @RequestLine("POST /v1/api/publish/{topic}/{application}")
    void publish(@RequestBody Object var1, @PathVariable("topic") @Param("topic") String var2, @PathVariable("application") @Param("application") String var3);
}