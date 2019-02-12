package edu.wgu.dm.admin;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {"edu.wgu.dm.admin", "edu.wgu.dm.config", "edu.wgu.dm.audit", "edu.wgu.dm.util",
        "edu.wgu.dm.health", "edu.wgu.dm.security.strategy", "edu.wgu.dm.repository", "edu.wgu.dm.security.service"})
@EnableFeignClients(basePackages = {"edu.wgu.dm.service.feign"})
@EnableAspectJAutoProxy
@EnableJpaAuditing
@EnableCaching
public class Application extends SpringBootServletInitializer {

    /**
     * Allows the app to run as a web app in an executable fat jar.
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplicationBuilder(Application.class).bannerMode(Mode.OFF)
                                                                               .build();
        app.run(args);
    }
}
