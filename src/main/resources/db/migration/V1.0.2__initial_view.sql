create view duplicate_item as
SELECT title, link, 'news' as source FROM news GROUP BY title, link HAVING COUNT(id) > 1
UNION
SELECT title, link, 'item' as source FROM item GROUP BY title, link HAVING COUNT(id) > 1;