package com.ramusthastudio.rss.dao.base;

import com.ramusthastudio.rss.helper.LoggedUserGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public class AuditEntityBase extends EntityBase {
    @CreationTimestamp
    @Column(name = "created_on")
    public LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on")
    public LocalDateTime updatedOn;

    @GeneratorType(type = LoggedUserGenerator.class, when = GenerationTime.INSERT)
    @Column(name = "created_by")
    public String createdBy;

    @GeneratorType(type = LoggedUserGenerator.class, when = GenerationTime.ALWAYS)
    @Column(name = "updated_by")
    public String updatedBy;

    public boolean deleted;
}
