CREATE DATABASE order_db;
CREATE DATABASE restaurant_db;
CREATE DATABASE delivery_db;
CREATE DATABASE rider_db;
CREATE DATABASE analytics_db;

\c restaurant_db;

CREATE TABLE restaurant (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255)
);

INSERT INTO restaurant (name)
VALUES ('Pizza Palace'), ('Burger Barn'), ('Sushi Spot');

\c rider_db;

CREATE TABLE rider (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255),
                       zone VARCHAR(255),
                       available BOOLEAN DEFAULT true
);

INSERT INTO rider (name, zone, available)
VALUES
    ('Ali', 'North', true),
    ('Sara', 'South', true),
    ('Ravi', 'East', true);

\c order_db;

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        restaurant_id BIGINT,
                        status VARCHAR(50)
);

\c delivery_db;

CREATE TABLE delivery (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT,
                          rider_id BIGINT,
                          status VARCHAR(50)
);

\c analytics_db;

CREATE TABLE analytics (
                           id BIGSERIAL PRIMARY KEY,
                           metric_type VARCHAR(255),
                           value BIGINT
);