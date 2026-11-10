ALTER TABLE fridge_devices ADD COLUMN owner_id VARCHAR(100);
UPDATE fridge_devices SET owner_id = CASE WHEN id = 3 THEN 'student' ELSE 'teacher' END;
ALTER TABLE fridge_devices ALTER COLUMN owner_id SET NOT NULL;
