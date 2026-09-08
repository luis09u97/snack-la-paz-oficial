begin transaction;

drop table if exists fretes cascade;
drop table if exists rasas cascade;

alter table pedidos
add column if not exists valor_frete decimal(10, 2) not null default 0;

commit;
