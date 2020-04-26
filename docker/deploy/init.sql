create table clients(
cnp text primary key,
first_name text not null,
last_name text not null,
document_id text not null,
email text,
phone_number text not null
);

insert into clients values ('1821229170017', 'Viorel', 'Chelaru', 'ZL056200', 'chelaru.viorel@gmail.com', '0743401019');
insert into clients values ('1770301008877', 'Diana', 'Popescu', 'B112233', 'diana.popescu@gmail.com', '0754223311');

create sequence account_request_seq;
create table account_request(
id bigint primary key default nextval('account_request_seq'),
client_cnp text not null,
account_type integer not null, 
initial_deposit numeric(12,2) default 0,
agent_username text not null,
request_timestamp timestamp not null,
status integer not null,
processing_start_time timestamp,
processing_end_time timestamp,
worker_name text
);

create table account(
iban text not null,
account_type integer not null,
client_cnp text not null,
current_amount numeric(12,2) not null
);

create table account_worker_heartbeat(
worker_name text primary key,
heartbeat_timestamp timestamp
);

