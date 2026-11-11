ALTER TABLE fridge_devices
  ADD COLUMN country VARCHAR(80) NOT NULL DEFAULT 'Brasil',
  ADD COLUMN city VARCHAR(120) NOT NULL DEFAULT 'São Paulo',
  ADD COLUMN latitude NUMERIC(9,6) NOT NULL DEFAULT -23.550520,
  ADD COLUMN longitude NUMERIC(9,6) NOT NULL DEFAULT -46.633308,
  ADD COLUMN firmware_code TEXT NOT NULL DEFAULT $$// FridgeLab firmware example
void setup() { pinMode(LED_BUILTIN, OUTPUT); }
void loop() { readTemperature(); maintainTarget(); delay(1000); }$$;

UPDATE fridge_devices SET country='Brasil', city='São Paulo', latitude=-23.550520, longitude=-46.633308 WHERE id=1;
UPDATE fridge_devices SET country='Brasil', city='Rio de Janeiro', latitude=-22.906847, longitude=-43.172896 WHERE id=2;
UPDATE fridge_devices SET country='Brasil', city='Curitiba', latitude=-25.428954, longitude=-49.267137, owner_id='student' WHERE id=3;

INSERT INTO fridge_devices(id,name,temperature_c,mode,note,owner_id,country,city,latitude,longitude)
VALUES
 (4,'Câmara Norte',-2,'ECO','Laticínios fictícios','teacher','Brasil','Belém',-1.455833,-48.503887),
 (5,'Câmara Sul',3,'NORMAL','Amostras de treinamento','teacher','Brasil','Porto Alegre',-30.034647,-51.217658),
 (6,'Freezer Campinas',-18,'TURBO','Produtos de teste','teacher','Brasil','Campinas',-22.909938,-47.062633),
 (7,'Hub Recife',4,'ECO','Monitoramento costeiro','teacher','Brasil','Recife',-8.057838,-34.882896),
 (8,'Hub Lisboa',2,'NORMAL','Rota internacional','teacher','Portugal','Lisboa',38.722252,-9.139337),
 (9,'Hub Santiago',-4,'ECO','Rota Andes','teacher','Chile','Santiago',-33.448890,-70.669265),
 (10,'Hub Bogotá',5,'NORMAL','Rota Andes norte','teacher','Colômbia','Bogotá',4.711000,-74.072100),
 (11,'Freezer Manaus',-16,'TURBO','Amostras amazônicas','teacher','Brasil','Manaus',-3.119028,-60.021731),
 (12,'Câmara Natal',3,'ECO','Lote didático','teacher','Brasil','Natal',-5.779256,-35.200916),
 (13,'Câmara Brasília',1,'NORMAL','Centro de distribuição','teacher','Brasil','Brasília',-15.793889,-47.882778),
 (14,'Freezer Salvador',-12,'TURBO','Produtos congelados','student','Brasil','Salvador',-12.977749,-38.501629),
 (15,'Câmara Fortaleza',4,'ECO','Lote fictício','student','Brasil','Fortaleza',-3.731862,-38.526669),
 (16,'Hub Montevidéu',2,'NORMAL','Rota Mercosul','student','Uruguai','Montevidéu',-34.901112,-56.164532),
 (17,'Hub Buenos Aires',0,'ECO','Rota Mercosul sul','student','Argentina','Buenos Aires',-34.603722,-58.381592),
 (18,'Hub Cidade do México',5,'NORMAL','Rota América','student','México','Cidade do México',19.432608,-99.133209),
 (19,'Freezer Vancouver',-15,'TURBO','Rota fria','student','Canadá','Vancouver',49.282729,-123.120738),
 (20,'Câmara Miami',3,'ECO','Rota internacional','student','Estados Unidos','Miami',25.761681,-80.191788);
