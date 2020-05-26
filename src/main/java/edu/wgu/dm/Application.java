package edu.wgu.dm;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import edu.wgu.boot.core.config.WGUBootApplication;

@WGUBootApplication
@ComponentScan(basePackages = {"edu.wgu.dm.admin", "edu.wgu.dm.config", "edu.wgu.dm.audit", "edu.wgu.dm.util",
        "edu.wgu.dm.health", "edu.wgu.dm.security.strategy", "edu.wgu.dm.repository", "edu.wgu.dm.security.service"})
@EnableFeignClients(basePackages = {"edu.wgu.dm.service.feign"})
@EnableAspectJAutoProxy
@EnableJpaAuditing
@EnableCaching
@EntityScan({"edu.wgu.dm.entity", "edu.wgu.dm.view"})
@EnableJpaRepositories(basePackages = {"edu.wgu.dm.repo", "edu.wgu.dm.view"})
@EnableTransactionManagement
public class Application {

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
