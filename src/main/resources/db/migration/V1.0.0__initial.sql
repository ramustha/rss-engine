create table image
(
    id          varchar(64) primary key,
    title       varchar,
    link        varchar(100) not null,
    url         varchar(100),
    description varchar,
    height      integer,
    width       integer,
    created_on  timestamp,
    updated_on  timestamp,
    created_by  varchar(255),
    updated_by  varchar(255),
    deleted     boolean default false
);

create table channel
(
    id              varchar(64) primary key,
    image_id        varchar(64) references image (id),
    title           varchar      not null,
    description     varchar,
    category        varchar(64)  not null,
    language        varchar(64)  not null,
    link            varchar(100) not null,
    copyright       varchar(100),
    generator       varchar(100),
    ttl             varchar(100),
    last_build_date timestamp,
    managing_editor varchar(100),
    web_master      varchar(100),
    created_on      timestamp,
    updated_on      timestamp,
    created_by      varchar(255),
    updated_by      varchar(255),
    deleted         boolean default false
);

create table item
(
    id           varchar(64) primary key,
    channel_id   varchar(64) references channel (id),
    image_id     varchar(64) references image (id),
    title        varchar not null,
    description  varchar not null,
    link         varchar not null,
    author       varchar(100),
    category     varchar(64),
    guid         varchar not null,
    is_permalink bool             default false,
    pub_date     timestamp,
    status       varchar not null default 'PENDING',
    created_on   timestamp,
    updated_on   timestamp,
    created_by   varchar(255),
    updated_by   varchar(255),
    deleted      boolean          default false
);

create table management_channel
(
    id          varchar(64) primary key,
    image_id    varchar(64) references image (id),
    title       varchar      not null,
    description varchar,
    category    varchar(64)  not null,
    language    varchar(64)  not null,
    link        varchar(100) not null,
    created_on  timestamp,
    updated_on  timestamp,
    created_by  varchar(255),
    updated_by  varchar(255),
    deleted     boolean default false
);

create table news
(
    id                varchar(64) primary key,
    item_id           varchar(64) references item (id),
    title             varchar not null,
    description       varchar not null,
    content           varchar not null,
    category          varchar not null,
    link              varchar not null,
    image_link        varchar not null,
    pub_date          timestamp,
    channel_icon_link varchar not null,
    created_on        timestamp,
    updated_on        timestamp,
    created_by        varchar(255),
    updated_by        varchar(255),
    deleted           boolean default false
);
