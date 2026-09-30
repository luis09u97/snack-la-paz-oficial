begin transaction;

insert into storage.buckets (id, name, public)
values ('product-images', 'product-images', true)
on conflict (id) do update set public = excluded.public;

drop policy if exists "Leitura publica de imagens de produtos" on storage.objects;
create policy "Leitura publica de imagens de produtos"
on storage.objects
for select
to anon, authenticated
using (bucket_id = 'product-images');

drop policy if exists "Admin envia imagens de produtos" on storage.objects;
create policy "Admin envia imagens de produtos"
on storage.objects
for insert
to authenticated
with check (
    bucket_id = 'product-images'
    and public.current_user_is_admin()
);

drop policy if exists "Admin atualiza imagens de produtos" on storage.objects;
create policy "Admin atualiza imagens de produtos"
on storage.objects
for update
to authenticated
using (
    bucket_id = 'product-images'
    and public.current_user_is_admin()
)
with check (
    bucket_id = 'product-images'
    and public.current_user_is_admin()
);

commit;
