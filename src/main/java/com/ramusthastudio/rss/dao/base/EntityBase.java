package com.ramusthastudio.rss.dao.base;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class EntityBase extends PanacheEntityBase {
}
