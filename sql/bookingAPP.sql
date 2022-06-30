DROP TABLE IF EXISTS booking;
CREATE table booking (
    id serial,
    title varchar(100) not null,
    description varchar(255) not null ,
    price float not null,
    data timestamp not null,
    primary key(id)
)