--liquibase formatted sql

--changeset order_service:4
INSERT INTO orders (user_id, status, total_price) VALUES (1, 'CREATED', 200);
INSERT INTO orders (user_id, status, total_price) VALUES (2, 'IN_PROCESS', 50);

INSERT INTO items (name, price) VALUES ('First item', 1000),
                                       ('Second Item', 100),
                                       ('Third item', 100);

INSERT INTO order_items(order_id, item_id, quantity) VALUES (1, 1, 50),
                                                            (1, 2, 10),
                                                            (2, 3, 5);


--changeset order_service:5
ALTER TABLE order_service.order_items
DROP CONSTRAINT order_items_item_id_fkey,
ADD CONSTRAINT order_items_item_id_fkey
FOREIGN KEY (item_id)
REFERENCES order_service.items(id)
ON DELETE SET NULL;

--changeset order_service:6 
ALTER TABLE order_service.order_items 
ALTER COLUMN item_id DROP NOT NULL;
