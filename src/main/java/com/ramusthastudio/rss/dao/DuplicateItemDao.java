package com.ramusthastudio.rss.dao;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

import static com.ramusthastudio.rss.helper.QueryFilter.generateQuery;

@Entity
@Table(name = "duplicate_item")
public class DuplicateItemDao extends PanacheEntityBase {
    @Id
    public String title;
    public String link;
    public String source;

    @SuppressWarnings("unchecked")
    public static Uni<List<PanacheEntityBase>> getFilter(@Context UriInfo request, Class<?> entity) {
        Map<String, Object> map = generateQuery(request, entity);
        return DuplicateItemDao
                .find(map.get("query").toString(), (Sort) map.get("sort"), (Map<String, Object>) map.get("parameters"))
                .filter("deletedFilter", Parameters.with("isDeleted", false))
                .page((int) map.get("index"), (int) map.get("size")).list()
                .onFailure().recoverWithUni(() -> Uni.createFrom().item(List.of()));
    }

    @Override
    public String toString() {
        return "DuplicateItemDao{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
