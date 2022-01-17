package com.ramusthastudio.rss.services;

import com.ramusthastudio.rss.dao.ManagementChannelDao;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RssIntegrationService {

    public Uni<PanacheEntityBase> addChannel(ManagementChannelDao managementChannelDao) {
        Log.debugf("add channel, %s", managementChannelDao);
        return Panache.withTransaction(managementChannelDao::persist).log();
    }


}
