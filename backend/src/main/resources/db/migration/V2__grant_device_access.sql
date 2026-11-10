-- A conta de migração administra o schema; a conta operacional só consulta e altera os dispositivos.
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'fridge_app') THEN CREATE ROLE fridge_app LOGIN PASSWORD 'training-only'; END IF; END $$;
GRANT CONNECT ON DATABASE geladeira_lab TO fridge_app;
GRANT USAGE ON SCHEMA public TO fridge_app;
GRANT SELECT, UPDATE ON TABLE fridge_devices TO fridge_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO fridge_app;
