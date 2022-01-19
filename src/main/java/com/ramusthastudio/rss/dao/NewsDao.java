package com.ramusthastudio.rss.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ramusthastudio.rss.dao.base.AutoIdentityEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static com.ramusthastudio.rss.helper.QueryFilter.generateQuery;

@Entity
@Table(name = "news")
@SQLDelete(sql = "UPDATE item SET deleted = true WHERE id = $1")
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
public class NewsDao extends AutoIdentityEntityBase {
    @NotBlank
    public String title;
    @NotBlank
    public String description;
    @NotBlank
    public String content;
    @NotBlank
    public String category;
    @NotBlank
    public String link;
    @NotBlank
    @Column(name = "image_link")
    public String imageLink;
    @NotNull
    @Column(name = "pub_date")
    public ZonedDateTime pubDate;
    @NotBlank
    @Column(name = "channel_icon_link")
    public String channelIconLink;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    public ItemDao item;

    public static Uni<PanacheEntityBase> findDuplicate(NewsDao item) {
        return find("title = ?1 and link = ?2", item.title, item.link)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }

    public static Uni<PanacheEntityBase> findDuplicate(ItemDao item) {
        return find("title = ?1 and link = ?2", item.title, item.link)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }

    @SuppressWarnings("unchecked")
    public static Uni<List<PanacheEntityBase>> getFilter(@Context UriInfo request, Class<?> entity) {
        Map<String, Object> map = generateQuery(request, entity);
        return NewsDao
                .find(map.get("query").toString(), (Sort) map.get("sort"), (Map<String, Object>) map.get("parameters"))
                .filter("deletedFilter", Parameters.with("isDeleted", false))
                .page((int) map.get("index"), (int) map.get("size")).list()
                .onFailure().recoverWithUni(() -> Uni.createFrom().item(List.of()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsDao newsDao = (NewsDao) o;

        if (!title.equals(newsDao.title)) return false;
        return link.equals(newsDao.link);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + link.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NewsDao{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", link='" + link + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", pubDate=" + pubDate +
                ", channelIconLink='" + channelIconLink + '\'' +
                '}';
    }
}
