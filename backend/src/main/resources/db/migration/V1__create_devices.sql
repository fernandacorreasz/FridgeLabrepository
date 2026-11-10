CREATE TABLE fridge_devices (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  temperature_c INTEGER NOT NULL,
  mode VARCHAR(20) NOT NULL,
  note TEXT NOT NULL DEFAULT '',
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO fridge_devices(id,name,temperature_c,mode,note) VALUES
(1,'Geladeira da cozinha',4,'ECO','Refrigeração de rotina'),
(2,'Geladeira do laboratório',2,'NORMAL','Amostras fictícias'),
(3,'Freezer de testes',-15,'TURBO','Ambiente de simulação');
