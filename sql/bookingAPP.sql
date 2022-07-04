DROP TABLE IF EXISTS booking;
drop table if exists tb_user;

create table tb_user (
    id serial,
    email varchar(100) not null,
    password varchar(100) not null,
    primary key (id)
);


CREATE table tb_event (
    id serial,
    title varchar(100) not null,
    description varchar(255) not null ,
    price float not null,
    data timestamp not null,
    primary key (id),
    creator_id integer not null,
    constraint fk_created_id foreign key (creator_id) references tb_user(id)
);

create table tb_booking (
    id serial,
    user_id integer not null,
    event_id integer not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (id),
    constraint fk_user_id foreign key (user_id) references tb_user(id),
    constraint fk_event_id foreign key (event_id) references tb_event(id)
);

-- create index on user_id
create index idx_user_id on tb_booking(user_id);



