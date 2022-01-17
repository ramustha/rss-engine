package com.ramusthastudio.rss.helper;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class FlywayRunner {
    @Inject Logger log;

    @ConfigProperty(name = "quarkus.flyway.migrate-at-start") boolean runMigration;
    @ConfigProperty(name = "quarkus.datasource.reactive.url") String datasourceUrl;
    @ConfigProperty(name = "quarkus.datasource.username") String datasourceUsername;
    @ConfigProperty(name = "quarkus.datasource.password") String datasourcePassword;

    public void runFlywayMigration(@Observes StartupEvent event) {
        log.infof("running migration = %s", runMigration);

        if (runMigration) {
            datasourceUrl = datasourceUrl.replace("vertx-reactive:", "jdbc:");
            log.infof("url = %s", datasourceUrl);
            log.infof("username = %s", datasourceUsername);
            log.infof("password = %s", datasourcePassword);

            Flyway flyway = Flyway.configure().dataSource(datasourceUrl, datasourceUsername, datasourcePassword).load();
            flyway.migrate();
        }
    }
}
