package com.ramusthastudio.rss.dao;

import com.ramusthastudio.rss.dao.base.AutoIdentityEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "management_channel")
@SQLDelete(sql = "UPDATE management_channel SET deleted = true WHERE id = $1")
@FilterDef(name = "deletedFilter", defaultCondition = "false", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
public class ManagementChannelDao extends AutoIdentityEntityBase {
    @NotBlank
    public String title;
    public String description;
    @NotBlank
    public String category;
    @NotBlank
    public String language;
    @NotBlank
    public String link;

    @Override
    public String toString() {
        return "ManagementChannelDao{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", language='" + language + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
