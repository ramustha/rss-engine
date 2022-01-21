package com.ramusthastudio.rss.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsoupParserTest {

    @Test
    void cleanText() {
        String text1 = JsoupParser.cleanText("<html><body>Clean HTML tag</body></html>");
        Assertions.assertEquals("Clean HTML tag", text1);

        String text2 = JsoupParser.cleanText("<img src=\"https://img.antaranews.com/cache/800x533/2022/01/20/MJR_5031.jpg\" align=\"left\" border=\"0\" />" +
                "Presiden Joko Widodo mengungkapkan sejumlah strategi pemerintah yang diyakini dapat mewujudkan ekonomi hijau");

        Assertions.assertEquals("Presiden Joko Widodo mengungkapkan sejumlah strategi pemerintah yang diyakini dapat mewujudkan ekonomi hijau", text2);
    }

    @Test
    void getImageLink() {
        String link = JsoupParser.getImageLink("<img src=\"https://img.antaranews.com/cache/800x533/2022/01/20/MJR_5031.jpg\" align=\"left\" border=\"0\" />" +
                "Presiden Joko Widodo mengungkapkan sejumlah strategi pemerintah yang diyakini dapat mewujudkan ekonomi hijau");

        Assertions.assertEquals("https://img.antaranews.com/cache/800x533/2022/01/20/MJR_5031.jpg", link);
    }
}