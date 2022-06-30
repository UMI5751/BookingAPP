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

