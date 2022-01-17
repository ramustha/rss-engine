package com.ramusthastudio.rss.helper;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.util.stream.Stream;

@ApplicationScoped
public class ObjectProducer {

    @Produces
    public RssReader createRssReader() {
        return new RssReader();
    }

    public static Stream<Item> readRss(RssReader reader, String url) {
        try {
            return reader.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
