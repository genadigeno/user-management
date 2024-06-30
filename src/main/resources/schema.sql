-- Create DB first --
CREATE DATABASE user_management
    WITH
    OWNER = postgres
--     TEMPLATE = template0
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

COMMENT ON DATABASE user_management
    IS 'gvg group task';

-- Create own schema --
CREATE SCHEMA gvggroup
    AUTHORIZATION postgres;

COMMENT ON SCHEMA gvggroup
    IS 'test assessment ';

create table gvggroup.users (
    id integer not null,
    created date,
    email varchar(255) not null,
    enabled boolean,
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)        );

alter table if exists gvggroup.users drop constraint if exists user_email_must_be_unique;
alter table if exists gvggroup.users add constraint user_email_must_be_unique unique (email);

alter table if exists gvggroup.users drop constraint if exists user_username_must_be_unique;
alter table if exists gvggroup.users add constraint user_username_must_be_unique unique (username);

create sequence users_seq start with 1 increment by 50;