CREATE DATABASE order_db;
CREATE DATABASE restaurant_db;
CREATE DATABASE delivery_db;
CREATE DATABASE rider_db;
CREATE DATABASE analytics_db;

\c restaurant_db;
CREATE TABLE IF NOT EXISTS restaurant (id BIGSERIAL PRIMARY KEY, name VARCHAR(255));
INSERT INTO restaurant (name) VALUES ('Pizza Palace'), ('Burger Barn'), ('Sushi Spot');

\c rider_db;
CREATE TABLE IF NOT EXISTS rider (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), zone VARCHAR(255));
INSERT INTO rider (name, zone) VALUES ('Ali', 'North'), ('Sara', 'South'), ('Ravi', 'East');