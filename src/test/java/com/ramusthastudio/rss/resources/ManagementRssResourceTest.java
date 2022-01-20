package com.ramusthastudio.rss.resources;

import com.ramusthastudio.rss.PostgresResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
public class ManagementRssResourceTest {

    @Test
    void getRssBy() {
        given()
                .get("/api/management/rss/1")
                .then()
                .statusCode(200);
    }

    @Test
    void getRss() {
        given()
                .get("/api/management/rss?id=1")
                .then()
                .statusCode(200)
                .body("id[0]", equalTo("1"));
    }

    @Test
    void getDuplicateItem() {
        given()
                .get("/api/management/duplicate-item")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }
}