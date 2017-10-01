-- -- To clear data base:
-- drop table user_mtm_conversation;
-- drop table message;
-- drop table conversation;
-- drop table users;
-----------------------------------------------------
create table users (
  id           serial primary key,
  login        varchar(25),
  passwordHash integer,
  firstName    varchar(50),
  lastName     varchar(50),
  constraint LOGIN_UNIQUE unique (login)
);

create table conversation (
  id    serial primary key,
  title text
);

create table user_mtm_conversation (
  user_id         integer,
  conversation_id integer,
  primary key (user_id, conversation_id),
  constraint FK_USER foreign key (user_id) references users (id),
  constraint FK_CONV foreign key (conversation_id) references conversation (id)
);

create table message (
  id                 serial primary key,
  author_id          integer,
  receiver_id        integer,
  conversation_id    integer,
  messageEncodedText bytea,
  constraint FK_RECEIVER foreign key (receiver_id) references users (id),
  constraint FK_AUTHOR foreign key (author_id) references users (id),
  constraint FK_COONVERSATION foreign key (conversation_id) references conversation (id)
);
