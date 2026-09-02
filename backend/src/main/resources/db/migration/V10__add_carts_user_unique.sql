-- Chống race condition: 2 request đồng thời tạo cart cho cùng user
-- (AddCartItemService.createCartSafely bắt DataIntegrityViolationException dựa vào constraint này).
-- Đã kiểm tra dữ liệu thật trên Postgres :5439 (2026-07-26): không có user_id trùng trong bảng carts.
DROP INDEX idx_carts_user_id;
CREATE UNIQUE INDEX uk_carts_user_id ON carts (user_id);
