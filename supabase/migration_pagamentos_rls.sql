begin transaction;

alter table pagamentos enable row level security;

drop policy if exists "Cliente ve pagamentos dos seus pedidos" on pagamentos;
create policy "Cliente ve pagamentos dos seus pedidos"
on pagamentos
for select
to authenticated
using (
    exists (
        select 1
        from pedidos
        join clientes on clientes.id_cliente = pedidos.id_cliente
        where pedidos.id_pedido = pagamentos.id_pedido
          and clientes.auth_id = auth.uid()
    )
);

drop policy if exists "Cliente cria pagamentos dos seus pedidos" on pagamentos;
create policy "Cliente cria pagamentos dos seus pedidos"
on pagamentos
for insert
to authenticated
with check (
    exists (
        select 1
        from pedidos
        join clientes on clientes.id_cliente = pedidos.id_cliente
        where pedidos.id_pedido = pagamentos.id_pedido
          and clientes.auth_id = auth.uid()
    )
);

drop policy if exists "Admin gerencia pagamentos" on pagamentos;
create policy "Admin gerencia pagamentos"
on pagamentos
for all
to authenticated
using (public.current_user_is_admin())
with check (public.current_user_is_admin());

grant select, insert, update on pagamentos to authenticated;
grant usage, select on all sequences in schema public to authenticated;

commit;
