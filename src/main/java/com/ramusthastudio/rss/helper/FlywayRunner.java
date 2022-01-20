package com.ramusthastudio.rss.helper;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.List;

@ApplicationScoped
public class FlywayRunner {

    @ConfigProperty(name = "quarkus.flyway.migrate-at-start") boolean runMigration;
    @ConfigProperty(name = "quarkus.datasource.reactive.url") String datasourceUrl;
    @ConfigProperty(name = "quarkus.datasource.username") String datasourceUsername;
    @ConfigProperty(name = "quarkus.datasource.password") String datasourcePassword;
    @ConfigProperty(name = "quarkus.flyway.locations") List<String> migrationLocations;

    public void runFlywayMigration(@Observes StartupEvent event) {
        Log.infof("running migration = %s", runMigration);

        if (runMigration) {
            datasourceUrl = datasourceUrl.replace("vertx-reactive:", "jdbc:");

            Log.infof("url = %s", datasourceUrl);
            Log.infof("username = %s", datasourceUsername);
            Log.infof("password = %s", datasourcePassword);
            Log.infof("locations = %s", migrationLocations);

            Flyway flyway = Flyway.configure()
                    .dataSource(datasourceUrl, datasourceUsername, datasourcePassword)
                    .locations(migrationLocations.toArray(new String[0]))
                    .load();
            flyway.migrate();
        }
    }
}
