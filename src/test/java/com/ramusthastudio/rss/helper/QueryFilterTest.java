package com.ramusthastudio.rss.helper;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import org.gradle.internal.impldep.com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

class QueryFilterTest {

    @Test
    void filterEnumTypeTest() {
        Assertions.assertNull(QueryFilter.findPossibleType(
                "title", "title", EntityTest.class));
        Assertions.assertNull(QueryFilter.findPossibleType(
                "category", "PRIVATE", null));
        Assertions.assertNull(QueryFilter.findPossibleType(
                "category", "OTHER", EntityTest.class));
        Assertions.assertEquals(Category.PRIVATE, QueryFilter.findPossibleType(
                "category", "PRIVATE", EntityTest.class));
        Assertions.assertEquals(Lists.newArrayList(Category.PRIVATE, Category.PUBLIC), QueryFilter.findPossibleType(
                "category", Lists.newArrayList("PRIVATE", "PUBLIC"), EntityTest.class));
        Assertions.assertEquals(Lists.newArrayList(Category.PRIVATE, Category.PUBLIC), QueryFilter.findPossibleType(
                "category", Lists.newArrayList("PRIVATE", "PUBLIC"), EntityTest.class));
    }

    @Test
    void filterDateTypeTest() throws Exception {
        String dateFormat = "yyyy-MM-dd";

        String requestDate = "2022-01-31";
        Date expectedDate = new SimpleDateFormat(dateFormat).parse(requestDate);

        Object filterDate = QueryFilter.findPossibleType("dateOn", requestDate, EntityTest.class);
        Assertions.assertEquals(expectedDate.toString(), filterDate.toString());

        Object filterLocalDate = QueryFilter.findPossibleType("localDateOn", requestDate, EntityTest.class);
        Assertions.assertEquals(requestDate, filterLocalDate.toString());

        Object filterLocalDateTime = QueryFilter.findPossibleType("localDateTimeOn", requestDate, EntityTest.class);
        Assertions.assertEquals(requestDate + "T00:00", filterLocalDateTime.toString());

        Object filterInstant = QueryFilter.findPossibleType("instantOn", requestDate, EntityTest.class);
        Assertions.assertEquals(requestDate + "T00:00:00Z", filterInstant.toString());

        Object filterZonedDateTime = QueryFilter.findPossibleType("zonedDateTimeOn", requestDate, EntityTest.class);
        Assertions.assertEquals(requestDate + "T00:00Z", filterZonedDateTime.toString());
    }

    @Test
    void filterDateTImeTypeTest() throws Exception {
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";

        String requestDate = "2022-01-31T00:00:00";
        Date expectedDate = new SimpleDateFormat(dateFormat).parse(requestDate);

        Object filterDate = QueryFilter.findPossibleType("dateOn", requestDate, EntityTest.class);
        Assertions.assertEquals(expectedDate.toString(), filterDate.toString());

        Object filterLocalDate = QueryFilter.findPossibleType("localDateOn", requestDate, EntityTest.class);
        Assertions.assertEquals("2022-01-31", filterLocalDate.toString());

        Object filterLocalDateTime = QueryFilter.findPossibleType("localDateTimeOn", requestDate, EntityTest.class);
        Assertions.assertEquals("2022-01-31T00:00", filterLocalDateTime.toString());

        Object filterInstant = QueryFilter.findPossibleType("instantOn", requestDate, EntityTest.class);
        Assertions.assertEquals("2022-01-31T00:00:00Z", filterInstant.toString());

        Object filterZonedDateTime = QueryFilter.findPossibleType("zonedDateTimeOn", requestDate, EntityTest.class);
        Assertions.assertEquals("2022-01-31T00:00Z", filterZonedDateTime.toString());

        filterLocalDateTime = QueryFilter.findPossibleType("createdOn", requestDate, EntityTest.class);
        Assertions.assertEquals("2022-01-31T00:00", filterLocalDateTime.toString());
    }

    @Test
    void generateQueryTest() throws Exception {
        MultivaluedMap<String, String> queryParam = new MultivaluedHashMap<>();
        queryParam.put("title", List.of("test"));
        queryParam.put("category-lk", List.of("top", "news"));
        queryParam.put("sort", List.of("title"));
        queryParam.put("order", List.of("asc"));
        queryParam.put("createdBy-in", List.of("SYSTEM", "ROBOT"));

        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(new URI("http://localhost:8080"));
        Mockito.when(uriInfo.getQueryParameters()).thenReturn(queryParam);

        Map<String, Object> query = QueryFilter.generateQuery(uriInfo, EntityTest.class);
        Assertions.assertEquals("asc", query.get("order"));
        Assertions.assertEquals("title", ((Sort) query.get("sort")).getColumns().get(0).getName());
        Assertions.assertEquals(0, query.get("index"));
        Assertions.assertEquals(20, query.get("size"));

        String expectedQuery = "lower(category) LIKE :category0 AND lower(category) LIKE :category1 AND title = :title0 AND createdBy IN :createdBy";
        Assertions.assertEquals(expectedQuery, query.get("query"));

        @SuppressWarnings("unchecked")
        Map<String, Object> parameters = (Map<String, Object>) query.get("parameters");
        Assertions.assertEquals("%top%", parameters.get("category0"));
        Assertions.assertEquals("%news%", parameters.get("category1"));
        Assertions.assertEquals("test", parameters.get("title0"));
        Assertions.assertEquals(List.of("SYSTEM", "ROBOT"), parameters.get("createdBy"));

    }

    static class EntityTest extends AuditTest {
        public String title;
        public Category category;
        public Date dateOn;
        public LocalDate localDateOn;
        public LocalDateTime localDateTimeOn;
        public Instant instantOn;
        public ZonedDateTime zonedDateTimeOn;
    }

    static class AuditTest extends PanacheEntityBase {
        public String createdBy;
        public LocalDateTime createdOn;
    }

    enum Category {
        PRIVATE,
        PUBLIC
    }
}