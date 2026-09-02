-- Một khoản thanh toán giờ có thể thuộc về đơn hàng HOẶC gói thuê tháng.
ALTER TABLE payments ALTER COLUMN order_id DROP NOT NULL;
ALTER TABLE payments ADD COLUMN customer_subscription_id BIGINT;

-- Đúng một trong hai, không được cả hai và không được rỗng cả hai: một khoản
-- tiền không thuộc về cái gì thì bộ đối soát không biết kích hoạt thứ gì.
ALTER TABLE payments ADD CONSTRAINT ck_payments_single_target
    CHECK ((order_id IS NULL) <> (customer_subscription_id IS NULL));

CREATE INDEX idx_payments_subscription ON payments (customer_subscription_id);
