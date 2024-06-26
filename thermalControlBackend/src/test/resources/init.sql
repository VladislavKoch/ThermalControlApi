create table if not exists sensor
(
    id     int generated by default as identity,
    serial int not null unique,
    name   varchar(100)
);

create table if not exists measurement
(
    id          int generated by default as identity,
    temperature numeric(4, 1)                                    not null,
    humidity    numeric(4, 1)                                    not null,
    sensor      int references sensor (serial) on DELETE cascade not null,
    time        timestamp                                        not null
);

create table if not exists sensor_interaction
(
    serial int references sensor (serial) on DELETE cascade primary key
);

create table if not exists telegram_interaction
(
    id          int generated by default as identity,
    username    varchar(100),
    telegram_id varchar(20) not null unique,
    time        timestamp   not null
);

create table if not exists my_user
(
    id       int generated by default as identity,
    login    varchar(100) not null unique,
    password varchar      not null,
    telegram varchar(20) unique nulls distinct,
    roles    varchar      not null
);

create table if not exists sensor_settings
(
    id                  int generated by default as identity,
    sensor              int references sensor (serial) on DELETE cascade unique NULLS NOT DISTINCT,
    optimal_temperature decimal not null,
    minimal_temperature decimal not null
);

create table if not exists sensor_period
(
    id         int generated by default as identity,
    is_default bool not null,
    weekday    int  not null,
    start_at   time not null,
    end_at     time not null,
    constraint weekday_is_default_unique unique NULLS NOT DISTINCT (weekday, is_default)
);