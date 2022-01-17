package com.ramusthastudio.rss.dao.base;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AutoIdentityEntityBase extends AuditEntityBase {
    @Id
    @GeneratedValue(generator = "inquisitive-uuid")
    @GenericGenerator(name = "inquisitive-uuid", strategy = "com.ramusthastudio.rss.dao.base.InquisitiveUUIDGenerator")
    public String id;

}
