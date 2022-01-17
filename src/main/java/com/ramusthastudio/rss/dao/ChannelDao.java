package com.ramusthastudio.rss.dao;

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

@Entity
@Cacheable
@Table(name = "channel")
@SQLDelete(sql = "UPDATE channel SET deleted = true WHERE id = $1")
@FilterDef(name = "deletedFilter", defaultCondition = "false", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
public class ChannelDao extends AutoIdentityEntityBase {
    public String title;
    public String description;
    public String category;
    public String language;
    public String link;
    public String copyright;
    public String generator;
    public String ttl;
    @Column(name = "last_build_date")
    public String lastBuildDate;
    @Column(name = "managing_editor")
    public String managingEditor;
    @Column(name = "web_master")
    public String webMaster;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    public ImageDao image;

    @Override
    public String toString() {
        return "ChannelDao{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", language='" + language + '\'' +
                ", link='" + link + '\'' +
                ", copyright='" + copyright + '\'' +
                ", generator='" + generator + '\'' +
                ", ttl='" + ttl + '\'' +
                ", lastBuildDate='" + lastBuildDate + '\'' +
                ", managingEditor='" + managingEditor + '\'' +
                ", webMaster='" + webMaster + '\'' +
                ", image=" + image +
                '}';
    }
}
