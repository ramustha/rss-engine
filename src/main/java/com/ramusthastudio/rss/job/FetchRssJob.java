package com.ramusthastudio.rss.job;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.ramusthastudio.rss.dao.ChannelDao;
import com.ramusthastudio.rss.dao.DuplicateItemDao;
import com.ramusthastudio.rss.dao.ItemDao;
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

import static com.ramusthastudio.rss.helper.JsoupParser.cleanText;
import static com.ramusthastudio.rss.helper.JsoupParser.getImageLink;
import static com.ramusthastudio.rss.helper.ObjectProducer.readRss;

@ApplicationScoped
public class FetchRssJob {
    @Inject RssReader rssReader;

    @Scheduled(cron = "{fetch.cron.expr}")
    public void executeFetcherJob() {
        Log.infof("Starting fetch RSS data ⚡");

        Panache.withTransaction(() ->
                        ChannelDao.findAll()
                                .filter("deletedFilter", Parameters.with("isDeleted", false))
                                .stream()
                                .onItem().transformToUniAndMerge(channel -> persistAndUpdate((ChannelDao) channel))
                                .onFailure().invoke(Throwable::printStackTrace)
                                .onItem().ignoreAsUni()
                )
                .chain(Panache::flush)
                .await().indefinitely();
    }

    @Scheduled(cron = "{convert.cron.expr}")
    public void executeConverterJob() {
        Log.infof("Starting convert RSS data ⚡");

        Panache.withTransaction(() ->
                        ItemDao.findAll()
                                .filter("deletedFilter", Parameters.with("isDeleted", false))
                                .filter("statusFilter", Parameters.with("status", "PENDING"))
                                .stream().select().distinct()
                                .onItem().transformToUniAndMerge(item ->
                                        NewsDao.findDuplicate((ItemDao) item)
                                                .onItem().ifNotNull().transform(current -> {
                                                    ItemDao itemDao = (ItemDao) item;
                                                    itemDao.status = "COMPLETED";

                                                    NewsDao newsDao = (NewsDao) current;
                                                    persistAndUpdate(newsDao, itemDao);

                                                    Log.infof("update news existing = %s", newsDao);
                                                    return itemDao.persist().chain(newsDao::persist);
                                                }).onItem().ifNull().switchTo(() -> {
                                                    ItemDao itemDao = (ItemDao) item;
                                                    itemDao.status = "COMPLETED";

                                                    NewsDao newsDao = new NewsDao();
                                                    persistAndUpdate(newsDao, itemDao);

                                                    Log.infof("save news = %s", item);
                                                    return itemDao.persist().chain(newsDao::persist);
                                                }).onFailure().recoverWithNull())
                                .onFailure().invoke(Throwable::printStackTrace)
                                .onItem().ignoreAsUni()
                )
                .chain(Panache::flush)
                .await().indefinitely();
    }

    @Scheduled(every = "{duplicate.every.expr}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void executeDuplicateJob() {
        Log.infof("Starting remove duplicate RSS data ⚡");

        Panache.withTransaction(() ->
                        DuplicateItemDao.findAll()
                                .stream()
                                .onItem().transformToUniAndMerge(item -> {
                                    DuplicateItemDao duplicateItemDao = (DuplicateItemDao) item;
                                    if (duplicateItemDao.source.equals("item")) {
                                        return ItemDao.findDuplicate(duplicateItemDao)
                                                .onItem().ifNotNull().transform(i -> {
                                                    ItemDao itemDao = (ItemDao) i;
                                                    itemDao.deleted = true;

                                                    Log.infof("found duplicate item = %s", itemDao.title);
                                                    return itemDao.persist();
                                                })
                                                .onItem().ifNull().fail();
                                    }
                                    return NewsDao.findDuplicate(duplicateItemDao)
                                            .onItem().ifNotNull().transform(i -> {
                                                NewsDao newsDao = (NewsDao) i;
                                                newsDao.deleted = true;

                                                Log.infof("found duplicate news = %s", newsDao.title);
                                                return newsDao.persist();
                                            })
                                            .onItem().ifNull().fail();
                                })
                                .onFailure().invoke(Throwable::printStackTrace)
                                .onItem().ignoreAsUni())
                .chain(Panache::flush)
                .await().indefinitely();
    }

    private void persistAndUpdate(NewsDao newsDao, ItemDao itemDao) {
        newsDao.title = cleanText(itemDao.title);
        newsDao.description = cleanText(itemDao.description);
        newsDao.content = newsDao.description;
        newsDao.category = itemDao.category;
        newsDao.link = itemDao.link;
        newsDao.imageLink = getImageLink(itemDao.description);
        newsDao.pubDate = itemDao.pubDate;
        newsDao.channelIconLink = itemDao.channel.link;
        newsDao.item = itemDao;
    }

    public Uni<Void> persistAndUpdate(ChannelDao channelDao) {
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
            itemDao.imageUrl = JsoupParser.FALLBACK_IMG;
            itemDao.channel = channelDao;

            Log.infof("found news = %s link = %s", itemDao.title, itemDao.link);
            return itemDao;
        }).distinct();

        return Multi.createFrom().items(itemDaoList)
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

                                    Log.infof("update item existing = %s", itemDao);
                                    return itemDao.channel.persist().chain(itemDao::persist);
                                }).onItem().ifNull().switchTo(() -> {

                                    Log.infof("save item = %s", item);
                                    return item.persist();
                                }).onFailure().recoverWithNull())
                .onFailure().invoke(Throwable::printStackTrace)
                .onItem().ignoreAsUni();
    }
}
