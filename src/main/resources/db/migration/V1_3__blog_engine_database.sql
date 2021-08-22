insert into users(code, email, is_moderator, `name`, password, photo, reg_time)
values ('qwe', 'test1@mail.ru', 1, 'user_name1', '$2y$12$RrhSR3qnVbpk4yBuqAgXb.xtGerpj8Jo5RIqPM.OtbmT/R.YyrspK', null,
        now())
     , ('qwe', 'test2@mail.ru', 0, 'user_name2', '$2y$12$PB9KG0cthVErpLN1jX46/O7z3VhrldGp/mDT6dksygrR4Of4eehoe',
        'some link1', now())
     , ('qwe', 'test3@mail.ru', 0, 'user_name3', '$2y$12$lNZlcHGLfhNB/G1JBAGPye3NFLge.x7bSyRfcEDYPSMEBLa7ipYwy',
        'some link2', now());

insert into tags(name)
values ('tag1'),
       ('tag2');

insert into posts(is_active, moderation_status, moderator_id, user_id, `time`, title, text, view_count)
values (true, 'NEW', 2, 1, now(), 'title 1', 'post text 1', 1)
     , (true, 'ACCEPTED', 2, 1, '2021-02-01 12:00:00', 'title 2', 'post text 2', 10)
     , (true, 'ACCEPTED', 2, 2, '2021-01-01 12:00:00', 'title 3', 'post text 3', 100)
     , ( true, 'ACCEPTED', 1, 2, '2020-05-01 00:00:00', 'title 4'
       , 'post text 4aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'
       , 1000)
     , (true, 'NEW', 1, 3, now(), 'title 5', 'post text 5', 1000)
     , (true, 'NEW', 1, 3, now(), 'title 6', 'post text 6', 1000)
     , (true, 'DECLINED', 1, 3, now(), 'title 7', 'post text 7', 1000)
     , (true, 'DECLINED', 1, 1, now(), 'title 8', 'post text 8', 10000)
     , (true, 'DECLINED', 1, 1, now(), 'title 9', 'post text 9', 100000)
     , (true, 'DECLINED', 1, 1, now(), 'title 10', 'post text 10', 999)
     , (true, 'NEW', 1, 1, now(), 'title 11', 'post text 11', -1)
     , (true, 'NEW', 1, 1, now(), 'title 12', 'post text 12', 0)
     , (true, 'NEW', 1, 1, now(), 'title 13', 'post text 13', 2)
     , (true, 'DECLINED', 1, 1, now(), 'title 14', 'post text 14', 2)
;

insert into tag2post(post_id, tag_id)
values (1, 1)
     , (2, 1)
     , (3, 1)
     , (3, 2)
     , (4, 1)
     , (5, 2)
     , (6, 2);

insert into post_comments(text, time, parent_id, post_id, user_id)
values ('comment text 1', now(), null, 1, 1)
     , ('comment text 2', now(), 1, 1, 2)
     , ('comment text 3', now(), null, 2, 2);

insert into post_votes(`time`, `value`, user_id, post_id)
values (now(), 1, 1, 3)
     , (now(), -1, 2, 3)
     , (now(), 1, 3, 3);
