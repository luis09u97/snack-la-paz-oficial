begin transaction;

alter table categorias enable row level security;
alter table itens_pedido enable row level security;

drop policy if exists "Admin gerencia categorias" on categorias;
create policy "Admin gerencia categorias"
on categorias
for all
to authenticated
using (public.current_user_is_admin())
with check (public.current_user_is_admin());

drop policy if exists "Admin ve itens dos pedidos" on itens_pedido;
create policy "Admin ve itens dos pedidos"
on itens_pedido
for select
to authenticated
using (public.current_user_is_admin());

grant select, insert, update on categorias to authenticated;
grant select on itens_pedido to authenticated;
grant usage, select on all sequences in schema public to authenticated;

commit;
