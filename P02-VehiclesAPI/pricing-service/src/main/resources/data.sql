-- By default JPA uses snake case strategy for the DB, which means that even if you are specifying the column name as
-- `vehicleId` in the class entity, the JPA will internally save it as `vehicle_id` in the DB. So you should use the
-- same column name in your data.sql file.
INSERT INTO price (vehicle_id, currency, price) VALUES (1, 'USD', 2001.0);
INSERT INTO price (vehicle_id, currency, price) VALUES (2, 'CAD', 3001.0);
INSERT INTO price (vehicle_id, currency, price) VALUES (3, 'CNY', 4001.0);
INSERT INTO price (vehicle_id, currency, price) VALUES (4, 'EUR', 5001.0);
INSERT INTO price (vehicle_id, currency, price) VALUES (5, 'YEN', 6001.0);

