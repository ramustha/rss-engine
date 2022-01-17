package com.ramusthastudio.rss.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ramusthastudio.rss.dao.base.AutoIdentityEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Cacheable
@Table(name = "news")
@SQLDelete(sql = "UPDATE item SET deleted = true WHERE id = $1")
@FilterDef(name = "deletedFilter", defaultCondition = "false", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
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

    @Transient
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    public ImageDao item;

    @Override
    public String toString() {
        return "News{" +
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
