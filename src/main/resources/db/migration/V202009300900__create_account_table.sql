create table account
(
    account_name varchar(255) not null constraint account_table_pkey primary key,
    password varchar(255) not null,
    email varchar(255) not null ,
    account_type varchar(255) not null
);
alter table order_table add constraint fk_account_name foreign key (account_name) references account(account_name);
--alter table order_table add constraint fk_customer_email foreign key (customer_email) references account(email);

