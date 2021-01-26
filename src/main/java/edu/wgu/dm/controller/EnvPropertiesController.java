package edu.wgu.dm.controller;

import edu.wgu.boot.auth.Role;
import edu.wgu.boot.auth.authz.annotation.HasAnyRole;
import edu.wgu.boot.auth.authz.annotation.Secured;
import edu.wgu.boot.auth.authz.strategy.SecureByRolesStrategy;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class EnvPropertiesController {

    private static final Logger log = LoggerFactory.getLogger(EnvPropertiesController.class);
    private final Environment env;

    public EnvPropertiesController(Environment env) {
        this.env = env;
    }

    /**
     * Gets environment.
     *
     * @return the environment
     */
    @Secured(strategies = {SecureByRolesStrategy.class})
    @HasAnyRole(Role.EMPLOYEE)
    @GetMapping(value = {"/env"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("List environment variables for this server.")
    public ResponseEntity<Map<String, String>> getEnvironment() {

        List<String> propertyNames = new ArrayList<>();
        for (Iterator<?> it = ((AbstractEnvironment) this.env).getPropertySources()
                                                              .iterator(); it.hasNext(); ) {
            PropertySource<?> propertySource = (PropertySource<?>) it.next();
            if (propertySource instanceof EnumerablePropertySource) {
                String[] names = ((EnumerablePropertySource<?>) propertySource).getPropertyNames();
                propertyNames.addAll(Arrays.asList(names));
            }
        }
        Map<String, String> properties = propertyNames.stream()
                                                      .filter(key -> !key.toLowerCase()
                                                                         .contains("password"))
                                                      .filter(key -> !key.toLowerCase()
                                                                         .contains("secret"))
                                                      .collect(Collectors.toMap(Function.identity(),
                                                                                this.env::getProperty,
                                                                                (val1, val2) -> val1));

        return ResponseEntity.ok(properties);
    }

}
