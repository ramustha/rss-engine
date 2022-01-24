drop view duplicate_item;

create view duplicate_item as
SELECT title, link, 'news' as source FROM news n where n.deleted = false GROUP BY title, link HAVING COUNT(id) > 1
UNION
SELECT title, link, 'item' as source FROM item i where i.deleted = false GROUP BY title, link HAVING COUNT(id) > 1;