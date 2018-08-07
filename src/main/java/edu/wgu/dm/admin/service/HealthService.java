package edu.wgu.dm.admin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

	private static Logger logger = LoggerFactory.getLogger(HealthService.class);

	@Autowired
	private Environment env;

	public Map<String, String> getEnvironment() {
		List<String> propertyNames = new ArrayList<>();
		for (Iterator<?> it = ((AbstractEnvironment) this.env).getPropertySources().iterator(); it.hasNext();) {
			PropertySource<?> propertySource = (PropertySource<?>) it.next();
			if (propertySource instanceof EnumerablePropertySource) {
				String[] names = ((EnumerablePropertySource<?>) propertySource).getPropertyNames();
				propertyNames.addAll(Arrays.asList(names));
				logger.debug("Properties for " + propertySource.getName() + ": " + Arrays.asList(names));
			}
		}

		// this ensures property precedence is respected.
		Map<String, String> properties = new HashMap<>();
		propertyNames.forEach(key -> {
			if (!key.contains("password"))
				properties.put(key, this.env.getProperty(key));
		});

		return properties;
	}
}
