package edu.wgu.dm.admin;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import edu.wgu.autoconfigure.WguSoaApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@WguSoaApplication
@EnableSwagger2
@ComponentScan(basePackages = {"edu.wgu.dm.admin", "edu.wgu.dm.config", "edu.wgu.dm.audit",
        "edu.wgu.security.service", "edu.wgu.dm.util"})
@EntityScan("edu.wgu.dm.entity")
@EnableJpaRepositories(basePackages = {"edu.wgu.dm.repo.ema"})
@EnableFeignClients(basePackages = {"edu.wgu.dm.service.feign"})
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class Application extends SpringBootServletInitializer {

    /**
     * Allows the app to run as a web app in an executable fat jar.
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication app =
                new SpringApplicationBuilder(Application.class).bannerMode(Mode.OFF).build();
        app.run(args);
    }

    /**
     * Allows the app to run as a web application in a war file.
     */
    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        application.sources(Application.class);
        application.bannerMode(Mode.OFF);
        return super.configure(application);
    }
}
