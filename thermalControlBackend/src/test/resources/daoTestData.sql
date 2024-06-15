truncate table sensor_interaction;

MERGE INTO sensor AS target
USING (SELECT 1 AS id, 156000 AS serial, 'Kostroma' AS name) AS source
ON (target.id = source.id)
WHEN NOT MATCHED THEN
    INSERT (id, serial, name) VALUES (source.id, source.serial, source.name);

MERGE INTO sensor AS target
USING (SELECT 2 AS id, 150000 AS serial, 'Yaroslavl' AS name) AS source
ON (target.id = source.id)
WHEN NOT MATCHED THEN
    INSERT (id, serial, name) VALUES (source.id, source.serial, source.name);

MERGE INTO sensor AS target
USING (SELECT 3 AS id, 153000 AS serial, 'Ivanovo' AS name) AS source
ON (target.id = source.id)
WHEN NOT MATCHED THEN
    INSERT (id, serial, name) VALUES (source.id, source.serial, source.name);

insert into sensor_interaction(serial)
values (156000);
insert into sensor_interaction(serial)
values (150000);