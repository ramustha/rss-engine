package com.ramusthastudio.rss.resources;

import com.ramusthastudio.rss.PostgresResource;
import com.ramusthastudio.rss.dao.ChannelDao;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
public class ManagementChannelResourceTest {

    @Test
    void addChannel() {
        ChannelDao channelDao = new ChannelDao();
        channelDao.title = "title";
        channelDao.link = "link";
        channelDao.category = "category";
        channelDao.language = "language";

        given()
                .contentType(ContentType.JSON).body(channelDao)
                .post("/api/management/channel")
                .then()
                .statusCode(200)
                .body("title", equalTo("title"));
    }

    @Test
    void getChannelBy() {
        given()
                .get("/api/management/channel/1")
                .then()
                .statusCode(200)
                .body("id", equalTo("1"));
    }

    @Test
    void deleteChannelByNotFound() {
        given()
                .delete("/api/management/channel/notfound")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteChannelByFound() {
        given()
                .delete("/api/management/channel/1")
                .then()
                .statusCode(204);
    }

    @Test
    void updateChannelNotFound() {
        ChannelDao channelDao = new ChannelDao();
        channelDao.id = "notFound";
        channelDao.title = "title";
        channelDao.link = "link";
        channelDao.category = "category";
        channelDao.language = "language";

        given()
                .contentType(ContentType.JSON).body(channelDao)
                .put("/api/management/channel/" + channelDao.id)
                .then()
                .statusCode(204);
    }

    @Test
    void updateChannelFound() {
        ChannelDao channelDao = new ChannelDao();
        channelDao.id = "1";
        channelDao.title = "update title";
        channelDao.link = "link";
        channelDao.category = "category";
        channelDao.language = "language";

        given()
                .contentType(ContentType.JSON).body(channelDao)
                .put("/api/management/channel/" + channelDao.id)
                .then()
                .statusCode(200)
                .body("title", equalTo("update title"));
    }

    @Test
    void getChannel() {
        given()
                .get("/api/management/channel")
                .then()
                .statusCode(200)
                .body("size()", equalTo(4));
    }
}