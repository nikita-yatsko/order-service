--liquibase formatted sql

--changeset order_service:4
INSERT INTO orders (user_id, status, total_price) VALUES (1, 'CREATED', 200);
INSERT INTO orders (user_id, status, total_price) VALUES (2, 'IN_PROCESS', 50);
INSERT INTO orders (user_id, status, total_price) VALUES (3, 'SHIPPED', 10);

INSERT INTO items (name, price) VALUES ('First item', 1000),
                                       ('Second Item', 100),
                                       ('Third item', 100);

INSERT INTO order_items(order_id, item_id, quantity) VALUES (1, 1, 50),
                                                            (1, 2, 10),
                                                            (2, 3, 5),
                                                            (3, 1, 20);


