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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static com.ramusthastudio.rss.helper.QueryFilter.generateQuery;

@Entity
@Table(name = "item")
@SQLDelete(sql = "UPDATE item SET deleted = true WHERE id = $1")
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
@FilterDef(name = "statusFilter", parameters = @ParamDef(name = "status", type = "string"))
@Filter(name = "statusFilter", condition = "status = :status")
public class ItemDao extends AutoIdentityEntityBase {
    @NotBlank
    public String title;
    @NotBlank
    public String description;
    @NotBlank
    public String link;
    public String author;
    public String category;
    @NotBlank
    public String guid;
    @Column(name = "is_permalink")
    public Boolean isPermaLink;
    @Column(name = "pub_date")
    public ZonedDateTime pubDate;
    public String status = "PENDING";
    @NotBlank
    @Column(name = "image_url")
    public String imageUrl;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    public ChannelDao channel;

    public static Uni<PanacheEntityBase> findDuplicate(ItemDao item) {
        return find("title = ?1 and link = ?2", item.title, item.link)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }

    @SuppressWarnings("unchecked")
    public static Uni<List<PanacheEntityBase>> find(@Context UriInfo request, Class<?> entity) {
        Map<String, Object> map = generateQuery(request, entity);
        return find(map.get("query").toString(), (Sort) map.get("sort"), (Map<String, Object>) map.get("parameters"))
                .filter("deletedFilter", Parameters.with("isDeleted", false))
                .page((int) map.get("index"), (int) map.get("size")).list()
                .onFailure().recoverWithUni(() -> Uni.createFrom().item(List.of()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemDao itemDao = (ItemDao) o;

        if (!title.equals(itemDao.title)) return false;
        return link.equals(itemDao.link);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + link.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ItemDao{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", guid='" + guid + '\'' +
                ", isPermaLink=" + isPermaLink +
                ", pubDate=" + pubDate +
                ", status='" + status + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", channel=" + channel +
                '}';
    }
}
