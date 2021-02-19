create table customer (
id bigserial,
firstName varchar 255,
lastName    varchar 255,
email   varchar 255
);
create table webshop_order (
id identity,
customer_id number,
status varchar 255,
total_price_hrk numeric,
total_price_eur numeric
);

create table order_item (
id identity,
order_id number,
product_id number,
quantity number
);

create table product (
id identity,
code char 10,
name varchar 255,
priceHrk number ,
description text,
is_available boolean
);