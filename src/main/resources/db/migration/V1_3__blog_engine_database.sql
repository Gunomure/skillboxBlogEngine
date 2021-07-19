insert into users(code, email, is_moderator, name, password, photo, reg_time)
values ('qwe', 'test1@mail.ru', 1, 'mikhail', 'qwe', null, now())
       ,('qwe', 'test2@mail.ru', 0, 'test_name', 'qwe', 'some link', now());

insert into tags(name) values ('tag1'), ('tag2');

insert into posts(is_active, moderation_status, moderator_id, user_id, `time`, title, text, view_count) values
(true, 'NEW', 2, 1, now(), 'title 1', 'post text 1', 1)
,(true, 'ACCEPTED', 2, 1, now(), 'title 2', 'post text 2', 10)
,(true, 'DECLINED', 2, 1, now(), 'title 3', 'post text 3', 100)
,(true, 'DECLINED', 1, 1, now(), 'title 4', 'post text 4', 1000)
,(true, 'DECLINED', 1, 1, now(), 'title 5', 'post text 5', 10000)
,(true, 'DECLINED', 1, 1, now(), 'title 6', 'post text 6', 100000)
,(true, 'DECLINED', 1, 1, now(), 'title 7', 'post text 7', 999)
,(true, 'NEW', 1, 1, now(), 'title 8', 'post text 8', -1)
,(true, 'NEW', 1, 1, now(), 'title 9', 'post text 9', 0)
,(true, 'NEW', 1, 1, now(), 'title 10', 'post text 10', 2)
,(true, 'DECLINED', 1, 1, now(), 'title 11', 'post text 11', 2)
;

insert into post_comments(text, time, parent_id, post_id, user_id) values
('comment text 1', now(), null, 1, 1)
,('comment text 2', now(), 1, 1, 2)
,('comment text 3', now(), null, 2, 2);