package com.ramusthastudio.rss.job;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.ramusthastudio.rss.dao.ChannelDao;
import com.ramusthastudio.rss.dao.ItemDao;
import com.ramusthastudio.rss.dao.ManagementChannelDao;
import com.ramusthastudio.rss.dao.NewsDao;
import com.ramusthastudio.rss.helper.JsoupParser;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Parameters;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static com.ramusthastudio.rss.helper.JsoupParser.*;
import static com.ramusthastudio.rss.helper.ObjectProducer.readRss;

@ApplicationScoped
public class FetchRssJob {
    @Inject RssReader rssReader;

    @Scheduled(every = "60s")
    public void executeFetcherJob() {
        Log.infof("Starting fetch RSS data ⚡");

        Panache.withTransaction(() ->
                        ManagementChannelDao.findAll()
                                .filter("deletedFilter", Parameters.with("isDeleted", false))
                                .stream()
                                .onItem().transformToUniAndMerge(channel -> persistAndUpdate((ManagementChannelDao) channel))
                                .onFailure().invoke(Throwable::printStackTrace)
                                .onItem().ignoreAsUni()
                )
                .await().indefinitely();

    }

    @Scheduled(every = "30s")
    public void executeConverterJon() {
        Log.infof("Starting convert RSS data ⚡");

        Panache.withTransaction(() ->
                        ItemDao.findAll()
                                .filter("deletedFilter", Parameters.with("isDeleted", false))
                                .filter("statusFilter", Parameters.with("status", "PENDING"))
                                .stream()
                                .onItem().transformToUniAndMerge(item -> {
                                    Log.infof("convert item = %s", item);

                                    ItemDao itemDao = (ItemDao) item;
                                    itemDao.status = "COMPLETED";

                                    NewsDao newsDao = new NewsDao();
                                    newsDao.title = cleanText(itemDao.title);
                                    newsDao.description = cleanText(itemDao.description);
                                    newsDao.content = newsDao.description;
                                    newsDao.category = itemDao.category;
                                    newsDao.link = itemDao.link;
                                    newsDao.imageLink = getImageLink(itemDao.description);
                                    newsDao.pubDate = itemDao.pubDate;
                                    newsDao.channelIconLink = itemDao.channel.link;

                                    Log.infof("save news = %s", newsDao);

                                    return newsDao.persist().chain(itemDao::persist);
                                })
                                .onFailure().invoke(Throwable::printStackTrace)
                                .onItem().ignoreAsUni()
                )
                .await().indefinitely();
    }

    public Uni<Void> persistAndUpdate(ManagementChannelDao channelDao) {
        Log.infof("channel = %s", channelDao.link);

        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        Stream<Item> rssFeed = readRss(rssReader, channelDao.link);
        Stream<ItemDao> itemDaoList = rssFeed.map(r -> {
            ItemDao itemDao = new ItemDao();
            itemDao.title = r.getTitle().orElse(null);
            itemDao.description = r.getDescription().orElse(null);
            itemDao.link = r.getLink().orElse(null);
            itemDao.author = r.getAuthor().orElse(null);
            itemDao.category = r.getCategory().orElse(channelDao.category);
            itemDao.guid = r.getGuid().orElse(null);
            itemDao.isPermaLink = r.getIsPermaLink().orElse(null);
            itemDao.pubDate = ZonedDateTime.from(formatter.parse(r.getPubDate().orElse(formatter.format(ZonedDateTime.now()))));

            ChannelDao channel = new ChannelDao();
            channel.title = channelDao.title;
            channel.description = channelDao.description;
            channel.category = channelDao.category;
            channel.language = channelDao.language;
            channel.link = channelDao.link;

            itemDao.channel = channel;

            Log.infof("found news = %s link = %s", itemDao.title, itemDao.link);
            return itemDao;
        });

        return Multi.createFrom().items(itemDaoList)
                .onFailure().invoke(Throwable::printStackTrace)
                .onFailure().retry().atMost(3)
                .onItem().transformToUniAndMerge(item ->
                        ItemDao.findDuplicate(item)
                                .onItem().ifNotNull().transform(current -> {
                                    ItemDao itemDao = (ItemDao) current;
                                    itemDao.title = item.title;
                                    itemDao.description = item.description;
                                    itemDao.link = item.link;
                                    itemDao.author = item.author;
                                    itemDao.category = item.category;
                                    itemDao.guid = item.guid;
                                    itemDao.isPermaLink = item.isPermaLink;
                                    itemDao.pubDate = item.pubDate;
                                    itemDao.channel = item.channel;

                                    Log.infof("update existing = %s", itemDao);
                                    return itemDao.channel.persist().chain(itemDao::persist);
                                }).onItem().ifNull().switchTo(() -> {

                                    Log.infof("save = %s", item);
                                    return item.channel.persist().chain(item::persist);
                                }))
                .onItem().ignoreAsUni();
    }
}
