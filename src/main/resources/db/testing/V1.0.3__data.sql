insert into channel
(id, link, category, title, language, created_by, created_on, updated_by, updated_on)
values ('1', 'https://www.suara.com/rss/bisnis', 'BISNIS,SUARA', 'Suara', 'ID', 'SYSTEM', now(), 'SYSTEM', now());

insert into item
(id, channel_id, title, description, guid, category, link, image_url, pub_date, created_by, created_on, updated_by, updated_on)
values ('1', '1', 'title', 'description', 'guid', 'category', 'link', 'image_url', now(), 'SYSTEM', now(), 'SYSTEM', now());

insert into news
(id, item_id, title, description, content, category, link, image_link, pub_date, channel_icon_link, created_by, created_on, updated_by, updated_on)
values ('1', '1', 'title', 'description', 'content', 'category', 'link', 'image_link', now(), 'channel_icon_link', 'SYSTEM', now(), 'SYSTEM', now());