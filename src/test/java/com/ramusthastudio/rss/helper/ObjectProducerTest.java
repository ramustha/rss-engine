package com.ramusthastudio.rss.helper;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ObjectProducerTest {

    @Test
    void readRss() {
        Stream<Item> itemStream = ObjectProducer.readRss(new RssReader(), "https://www.suara.com/rss/bisnis");
        Assertions.assertNotNull(itemStream);

        List<Item> collect = itemStream.collect(Collectors.toList());
        assertFalse(collect.isEmpty());
    }

    @Test
    void readRssException() {
        Assertions.assertThrows(RuntimeException.class,
                () -> ObjectProducer.readRss(new RssReader(), "https://localhost:8080"));
    }
}