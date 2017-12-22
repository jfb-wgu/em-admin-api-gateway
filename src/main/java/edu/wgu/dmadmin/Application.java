package edu.wgu.dmadmin;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import edu.wgu.autoconfigure.WguSoaApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Generic starter project for WGU SOA applications.  Most base functionality is given via the parent POM,
 * wgu-parent-pom, and the WGU SOA Core package, wgu-soa-core.  You can see what is included in your effective POM by
 * running mvn dependency:tree from the command line and you can find the parent POM and Core repositories in Stash.
 * See the README file for more details, replace this message with your application specific Javadoc comments and happy
 * coding!
 */
@EnableFeignClients(basePackages = {"edu.wgu.dmadmin.service", "edu.wgu.dreamcatcher.client"})
@WguSoaApplication
@EnableSwagger2
public class Application extends SpringBootServletInitializer {

   /**
    * Allows the app to run as a web app in an executable fat jar.
    *
    * @param args
    */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplicationBuilder(Application.class).bannerMode(Mode.OFF).build();
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