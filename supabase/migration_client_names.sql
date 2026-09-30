alter table public.clientes
    add column if not exists nome text,
    add column if not exists email text;

update public.clientes cliente
set
    nome = coalesce(
        nullif(cliente.nome, ''),
        nullif(auth_user.raw_user_meta_data ->> 'full_name', ''),
        nullif(auth_user.raw_user_meta_data ->> 'name', ''),
        split_part(auth_user.email, '@', 1)
    ),
    email = coalesce(nullif(cliente.email, ''), auth_user.email)
from auth.users auth_user
where cliente.auth_id = auth_user.id
  and (
      cliente.nome is null
      or cliente.nome = ''
      or cliente.email is null
      or cliente.email = ''
  );
