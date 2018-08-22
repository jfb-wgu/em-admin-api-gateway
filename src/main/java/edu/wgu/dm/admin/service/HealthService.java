package edu.wgu.dm.admin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HealthService {
 
    @Autowired
    private Environment env;

    public Map<String, String> getEnvironment() {
        List<String> propertyNames = new ArrayList<>();
        for (Iterator<?> it = ((AbstractEnvironment) this.env).getPropertySources().iterator(); it
                .hasNext();) {
            PropertySource<?> propertySource = (PropertySource<?>) it.next();
            if (propertySource instanceof EnumerablePropertySource) {
                String[] names = ((EnumerablePropertySource<?>) propertySource).getPropertyNames();
                propertyNames.addAll(Arrays.asList(names));
                log.debug(
                        "Properties for " + propertySource.getName() + ": " + Arrays.asList(names));
            }
        }

         Map<String, String> properties =
                propertyNames.stream().filter(key -> !key.contains("password")).collect(
                        Collectors.toMap(Function.identity(), key -> env.getProperty(key)));
        return properties;
    }
}
