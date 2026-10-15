-- Migração manual PostgreSQL/Neon para bases criadas com USER/ADMIN.
-- Executar no SQL Editor do banco correto. Não promove contas.
BEGIN;
ALTER TABLE public.tb_usuario DROP CONSTRAINT IF EXISTS tb_usuario_role_check;
ALTER TABLE public.tb_usuario ADD CONSTRAINT tb_usuario_role_check
CHECK (role IN ('USER', 'ADMIN', 'ADMIN_GERAL', 'ADMIN_SETORIAL'));
COMMIT;
