package com.ramusthastudio.rss.helper;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.Optional;

public class JsoupParser {
    public static final String FALLBACK_IMG = "fallback_img_url";

    private JsoupParser() {
    }

    public static String cleanText(String html) {
        return Optional.of(Jsoup.parse(html).body().text()).orElse(html);
    }

    public static String getImageLink(String html) {
        Elements imgTag = Jsoup.parse(html).getElementsByTag("img");
        return imgTag.isEmpty() ? FALLBACK_IMG : imgTag.attr("src");
    }
}
