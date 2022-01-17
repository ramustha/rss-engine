package com.ramusthastudio.rss.dao.base;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class IdentityEntityBase extends AuditEntityBase {
    @Id
    public String id;

}
