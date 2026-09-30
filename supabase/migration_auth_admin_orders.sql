begin transaction;

alter table clientes
    alter column id_usuario drop not null,
    add column if not exists auth_id uuid unique,
    add column if not exists is_admin boolean not null default false;

alter table clientes enable row level security;
alter table enderecos enable row level security;
alter table pedidos enable row level security;
alter table itens_pedido enable row level security;
alter table produtos enable row level security;

create or replace function public.current_user_is_admin()
returns boolean
language sql
security definer
set search_path = public
as $$
    select exists (
        select 1
        from clientes
        where clientes.auth_id = auth.uid()
          and clientes.is_admin = true
    );
$$;

drop policy if exists "Cliente ve o proprio perfil" on clientes;
create policy "Cliente ve o proprio perfil"
on clientes
for select
to authenticated
using (auth_id = auth.uid());

drop policy if exists "Cliente cria o proprio perfil" on clientes;
create policy "Cliente cria o proprio perfil"
on clientes
for insert
to authenticated
with check (auth_id = auth.uid());

drop policy if exists "Admin ve clientes" on clientes;
create policy "Admin ve clientes"
on clientes
for select
to authenticated
using (public.current_user_is_admin());

drop policy if exists "Cliente gerencia seus enderecos" on enderecos;
create policy "Cliente gerencia seus enderecos"
on enderecos
for all
to authenticated
using (
    exists (
        select 1 from clientes
        where clientes.id_cliente = enderecos.id_cliente
          and clientes.auth_id = auth.uid()
    )
)
with check (
    exists (
        select 1 from clientes
        where clientes.id_cliente = enderecos.id_cliente
          and clientes.auth_id = auth.uid()
    )
);

drop policy if exists "Cliente ve seus pedidos" on pedidos;
create policy "Cliente ve seus pedidos"
on pedidos
for select
to authenticated
using (
    exists (
        select 1 from clientes
        where clientes.id_cliente = pedidos.id_cliente
          and clientes.auth_id = auth.uid()
    )
);

drop policy if exists "Cliente cria seus pedidos" on pedidos;
create policy "Cliente cria seus pedidos"
on pedidos
for insert
to authenticated
with check (
    exists (
        select 1 from clientes
        where clientes.id_cliente = pedidos.id_cliente
          and clientes.auth_id = auth.uid()
    )
);

drop policy if exists "Cliente ve seus itens" on itens_pedido;
create policy "Cliente ve seus itens"
on itens_pedido
for select
to authenticated
using (
    exists (
        select 1
        from pedidos
        join clientes on clientes.id_cliente = pedidos.id_cliente
        where pedidos.id_pedido = itens_pedido.id_pedido
          and clientes.auth_id = auth.uid()
    )
);

drop policy if exists "Cliente cria itens dos seus pedidos" on itens_pedido;
create policy "Cliente cria itens dos seus pedidos"
on itens_pedido
for insert
to authenticated
with check (
    exists (
        select 1
        from pedidos
        join clientes on clientes.id_cliente = pedidos.id_cliente
        where pedidos.id_pedido = itens_pedido.id_pedido
          and clientes.auth_id = auth.uid()
    )
);

drop policy if exists "Admin gerencia produtos" on produtos;
create policy "Admin gerencia produtos"
on produtos
for all
to authenticated
using (
    public.current_user_is_admin()
)
with check (
    public.current_user_is_admin()
);

drop policy if exists "Admin gerencia pedidos" on pedidos;
create policy "Admin gerencia pedidos"
on pedidos
for all
to authenticated
using (
    public.current_user_is_admin()
)
with check (
    public.current_user_is_admin()
);

grant usage on schema public to authenticated;
grant execute on function public.current_user_is_admin() to authenticated;
grant select, insert, update on clientes, enderecos, pedidos, itens_pedido, produtos to authenticated;
grant usage, select on all sequences in schema public to authenticated;

commit;
