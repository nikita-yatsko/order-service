--liquibase formatted sql

--changeset order_service:4
INSERT INTO orders (user_id, status, total_price) VALUES (1, 'CREATED', 200);

INSERT INTO items (name, price) VALUES ('Iphone 11', 1000),
                                       ('Test Item', 100);

INSERT INTO order_items(order_id, item_id, quantity) VALUES (1, 1, 50);

