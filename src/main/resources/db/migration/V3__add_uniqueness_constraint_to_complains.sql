ALTER TABLE complains
ADD CONSTRAINT uk_complain_client_product UNIQUE (client_id, product_id);