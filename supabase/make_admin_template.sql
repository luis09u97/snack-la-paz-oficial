begin transaction;

-- Troque pelo e-mail da conta que deve acessar o painel administrativo.
update public.clientes cliente
set is_admin = true
from auth.users usuario
where cliente.auth_id = usuario.id
  and lower(usuario.email) = lower('SEU_EMAIL_AQUI');

commit;
