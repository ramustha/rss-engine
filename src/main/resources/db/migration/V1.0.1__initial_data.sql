insert into channel
    (id, link, category, title, language, created_by, created_on, updated_by, updated_on)
values (replace(gen_random_uuid()::text, '-', ''), 'https://www.antaranews.com/rss/top-news.xml', 'PUBLIC', 'Antara News', 'ID', 'SYSTEM', now(), 'SYSTEM', now());
insert into channel
(id, link, category, title, language, created_by, created_on, updated_by, updated_on)
values (replace(gen_random_uuid()::text, '-', ''), 'https://coconuts.co/jakarta/feed/', 'PUBLIC', 'Coconuts – Jakarta', 'ID', 'SYSTEM', now(), 'SYSTEM', now());
