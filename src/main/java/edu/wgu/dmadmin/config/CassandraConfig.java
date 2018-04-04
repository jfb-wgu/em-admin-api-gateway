package edu.wgu.dmadmin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;

@Configuration
public class CassandraConfig {

    @Autowired
    private Environment env;

    @Bean
    public Cluster cluster() {
        Cluster.Builder builder = Cluster.builder()
        		.addContactPoints(this.env.getProperty("spring.data.cassandra.contact-points").split(","))
        		.withPort(Integer.parseInt(this.env.getProperty("spring.data.cassandra.port")))
        		.withCredentials(this.env.getProperty("spring.data.cassandra.username"), this.env.getProperty("spring.data.cassandra.password"))
        		.withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM));
        return builder.build();
    }

    @Bean
    public Session session() throws Exception {
        return cluster().connect(this.env.getProperty("spring.data.cassandra.keyspace-name"));
    }
}
