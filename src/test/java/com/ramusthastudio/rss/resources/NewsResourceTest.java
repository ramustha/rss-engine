package com.ramusthastudio.rss.resources;

import com.ramusthastudio.rss.PostgresResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
public class NewsResourceTest {

    @Test
    void getNewsBy() {
        given()
                .get("/api/news/1")
                .then()
                .statusCode(200);
    }

    @Test
    void getNews() {
        given()
                .get("/api/news/search")
                .then()
                .statusCode(200)
                .body("id[0]", equalTo("1"));
    }
}