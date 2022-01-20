package com.ramusthastudio.rss.dao;

import com.ramusthastudio.rss.dao.base.AutoIdentityEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

import static com.ramusthastudio.rss.helper.QueryFilter.generateQuery;

@Entity
@Table(name = "channel")
@SQLDelete(sql = "UPDATE channel SET deleted = true WHERE id = $1")
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
public class ChannelDao extends AutoIdentityEntityBase {
    @NotBlank
    public String title;
    @Column(name = "icon_url")
    public String iconUrl;
    public String description;
    @NotBlank
    public String category;
    @NotBlank
    public String language;
    @NotBlank
    public String link;

    @SuppressWarnings("unchecked")
    public static Uni<List<PanacheEntityBase>> find(@Context UriInfo request, Class<?> entity) {
        Map<String, Object> map = generateQuery(request, entity);
        return find(map.get("query").toString(), (Sort) map.get("sort"), (Map<String, Object>) map.get("parameters"))
                .filter("deletedFilter", Parameters.with("isDeleted", false))
                .page((int) map.get("index"), (int) map.get("size")).list()
                .onFailure().recoverWithUni(() -> Uni.createFrom().item(List.of()));
    }

    @Override
    public String toString() {
        return "ChannelDao{" +
                "title='" + title + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", language='" + language + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
