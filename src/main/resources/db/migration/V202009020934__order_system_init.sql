create table order_table
(
    order_id varchar(255) not null constraint order_table_pkey primary key,
    create_at timestamp not null,
    total_price numeric(10,2) not null,
    status varchar(255) not null,
    account_name varchar(255) not null,
    customer_email varchar(255) not null,
    none_physical_delete BOOLEAN not null
);

create table order_line
(
    uuid varchar(255) not null constraint order_line_pkey primary key,
    order_id varchar(255) not null constraint fk_orderline_order_id references order_table(order_id),--//名字一样的时候可以省略order_table(order_id)中的括号部分
    line_number integer not null,
    product_name varchar(255) not null,
    quantity integer not null,
    unit_price numeric(10,2) not null
);