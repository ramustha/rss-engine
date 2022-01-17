package com.ramusthastudio.rss.dao;

import com.ramusthastudio.rss.dao.base.AutoIdentityEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

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
