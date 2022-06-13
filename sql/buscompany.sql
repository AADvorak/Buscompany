DROP DATABASE IF EXISTS buscompany;
CREATE DATABASE buscompany;
USE buscompany;

create table `user`
(
    id         int auto_increment
        primary key,
    first_name varchar(100) not null,
    last_name  varchar(100) not null,
    patronymic varchar(100) null,
    login      varchar(100) not null collate utf8_unicode_ci,
    password   varchar(100) not null,
    constraint unique (login)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table admin
(
    user_id  int primary key,
    position varchar(100) not null,
    foreign key (user_id) references user (id) on delete cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table client
(
    user_id int primary key,
    phone   varchar(10)  not null,
    email   varchar(100) not null,
    foreign key (user_id) references user (id) on delete cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table user_session
(
    user_id          int primary key,
    session_id       varchar(36) not null,
    last_action_time datetime    not null default current_timestamp(),
    foreign key (user_id) references user (id) on delete cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table bus
(
    id          int auto_increment
        primary key,
    bus_name    varchar(100) not null,
    place_count smallint     not null
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table trip
(
    id           int auto_increment
        primary key,
    bus_id       int,
    foreign key (bus_id) references bus (id) on delete cascade,
    from_station varchar(100)   not null,
    to_station   varchar(100)   not null,
    `start`      time           not null,
    price        numeric(11, 2) not null,
    duration     int            not null,
    approved     bit            not null
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table trip_schedule
(
    trip_id   int primary key,
    foreign key (trip_id) references trip (id) on delete cascade,
    from_date date         not null,
    to_date   date         not null,
    `period`  varchar(100) not null,
    constraint unique (trip_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table trip_date
(
    id               int auto_increment
        primary key,
    trip_id          int      not null,
    foreign key (trip_id) references trip (id) on delete cascade,
    `date`           date     not null,
    constraint unique (trip_id, `date`),
    free_place_count smallint not null
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table `order`
(
    id           int auto_increment
        primary key,
    trip_date_id int not null,
    foreign key (trip_date_id) references trip_date (id) on delete cascade,
    client_id    int not null,
    foreign key (client_id) references client (user_id) on delete cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table passenger
(
    id         int auto_increment
        primary key,
    order_id   int          not null,
    foreign key (order_id) references `order` (id) on delete cascade,
    first_name varchar(100) not null,
    last_name  varchar(100) not null,
    passport   varchar(100) not null
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

create table place
(
    id           int auto_increment
        primary key,
    trip_date_id int      not null,
    foreign key (trip_date_id) references trip_date (id) on delete cascade,
    number       smallint not null,
    passenger_id int      null,
    foreign key (passenger_id) references passenger (id) on delete set null
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

insert into bus (bus_name, place_count)
values ('Mercedes', 80),
       ('Man', 90);