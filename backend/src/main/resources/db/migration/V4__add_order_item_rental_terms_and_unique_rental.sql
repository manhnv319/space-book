ALTER TABLE order_items
    ADD COLUMN IF NOT EXISTS rental_term_value INT,
    ADD COLUMN IF NOT EXISTS rental_term_unit VARCHAR(255);

CREATE UNIQUE INDEX IF NOT EXISTS uq_rentals_order_item_id
    ON rentals (order_item_id)
    WHERE order_item_id IS NOT NULL;
