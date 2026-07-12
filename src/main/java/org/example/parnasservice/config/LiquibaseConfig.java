package org.example.parnasservice.config;

import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiquibaseConfig {

    private static final Logger log = LoggerFactory.getLogger(LiquibaseConfig.class);

    @Bean
    public SpringLiquibase liquibase(
            DataSource dataSource,
            @Value("${spring.liquibase.change-log:classpath:liquibase/changelog-master.yaml}") String changeLog,
            @Value("${spring.liquibase.enabled:true}") boolean enabled) {
        log.info("Configuring Liquibase: enabled={} changeLog={}", enabled, changeLog);

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }
}
