package edu.wgu.dmadmin.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CassandraConfig {

    @Autowired
    private Environment env;

    @Bean
    public Cluster cluster() {
        Cluster.Builder builder = Cluster.builder()
        		.addContactPoints(env.getProperty("spring.data.cassandra.contact-points").split(","))
        		.withPort(Integer.parseInt(env.getProperty("spring.data.cassandra.port")))
        		.withCredentials(env.getProperty("spring.data.cassandra.username"), env.getProperty("spring.data.cassandra.password"))
        		.withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.QUORUM));
        return builder.build();
    }

    @Bean
    public Session session() throws Exception {
        return cluster().connect(env.getProperty("spring.data.cassandra.keyspace-name"));
    }
}
