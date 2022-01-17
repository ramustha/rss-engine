create table channel
(
    id          varchar(64) primary key,
    icon_url    varchar(64),
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

create table item
(
    id           varchar(64) primary key,
    channel_id   varchar(64) references channel (id),
    title        varchar not null,
    description  varchar not null,
    link         varchar not null,
    author       varchar(100),
    category     varchar(64),
    guid         varchar not null,
    is_permalink bool             default false,
    pub_date     timestamp,
    image_url    varchar(64),
    status       varchar not null default 'PENDING',
    created_on   timestamp,
    updated_on   timestamp,
    created_by   varchar(255),
    updated_by   varchar(255),
    deleted      boolean          default false
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
