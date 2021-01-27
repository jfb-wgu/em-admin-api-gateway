package edu.wgu.dm;

import edu.wgu.boot.core.config.WGUBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@WGUBootApplication
@EnableFeignClients(basePackages = {"edu.wgu.dm.service.feign"})
@EnableJpaAuditing
@EnableTransactionManagement
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
