package com.ramusthastudio.rss.dao;

import com.ramusthastudio.rss.dao.base.AutoIdentityEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Cacheable
@Table(name = "image")
@SQLDelete(sql = "UPDATE image SET deleted = true WHERE id = $1")
@FilterDef(name = "deletedFilter", defaultCondition = "false", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
public class ImageDao extends AutoIdentityEntityBase {
    public String title;
    public String link;
    public String url;
    public String description;
    public Integer height;
    public Integer width;

    @Override
    public String toString() {
        return "ImageDao{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
