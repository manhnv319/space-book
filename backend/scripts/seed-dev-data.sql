-- Manual development seed. Run after Flyway has applied V1, V2, and V3.
-- Safe to re-run: books, categories, and vouchers use schema unique keys;
-- copies and subscriptions are guarded by their business identifiers.

BEGIN;

INSERT INTO books (
  isbn, title, description, image_url, format, list_price,
  rental_price_day, rental_price_week, rental_price_month, deposit_amount,
  publish_year, publisher, language, page_count, is_active
) VALUES
  ('9786040000001', 'Dế Mèn Phiêu Lưu Ký', 'Hành trình trưởng thành của Dế Mèn.', NULL, 'PAPERBACK', 85000, 5000, 25000, 70000, 60000, 2020, 'Kim Đồng', 'vi', 192, TRUE),
  ('9786040000002', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Tuổi thơ ở một làng quê miền Trung.', NULL, 'PAPERBACK', 125000, 7000, 35000, 100000, 90000, 2021, 'Trẻ', 'vi', 384, TRUE),
  ('9786040000003', 'Mắt Biếc', 'Câu chuyện thanh xuân đầy hoài niệm.', NULL, 'PAPERBACK', 110000, 6000, 30000, 90000, 80000, 2022, 'Trẻ', 'vi', 296, TRUE),
  ('9786040000004', 'Nhà Giả Kim', 'Hành trình theo đuổi ước mơ và định mệnh.', NULL, 'PAPERBACK', 99000, 6000, 30000, 80000, 70000, 2020, 'Nhã Nam', 'vi', 228, TRUE),
  ('9786040000005', 'Muôn Kiếp Nhân Sinh', 'Những chiêm nghiệm về đời sống và nhân quả.', NULL, 'HARDCOVER', 168000, 9000, 45000, 140000, 120000, 2023, 'First News', 'vi', 432, TRUE),
  ('9786040000006', 'Sapiens: Lược Sử Loài Người', 'Lịch sử ngắn gọn của nhân loại.', NULL, 'PAPERBACK', 189000, 10000, 50000, 150000, 130000, 2022, 'Tri Thức', 'vi', 560, TRUE),
  ('9786040000007', 'Atomic Habits', 'Xây dựng thói quen tốt từng bước nhỏ.', NULL, 'PAPERBACK', 158000, 9000, 45000, 130000, 110000, 2023, 'Nhã Nam', 'vi', 320, TRUE),
  ('9786040000008', 'Đắc Nhân Tâm', 'Nghệ thuật giao tiếp và tạo ảnh hưởng.', NULL, 'PAPERBACK', 108000, 6000, 30000, 90000, 75000, 2021, 'First News', 'vi', 320, TRUE),
  ('9786040000009', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 'Gợi mở cho người trẻ về học tập và trải nghiệm.', NULL, 'PAPERBACK', 98000, 6000, 30000, 80000, 70000, 2022, 'Nhã Nam', 'vi', 280, TRUE),
  ('9786040000010', 'Clean Code', 'Cẩm nang viết mã nguồn sạch và dễ bảo trì.', NULL, 'PAPERBACK', 245000, 13000, 65000, 200000, 180000, 2021, 'O''Reilly', 'en', 464, TRUE),
  ('9786040000011', 'Design Patterns', 'Các mẫu thiết kế phần mềm nền tảng.', NULL, 'HARDCOVER', 295000, 15000, 75000, 240000, 220000, 2020, 'Addison-Wesley', 'en', 416, TRUE),
  ('9786040000012', 'Tư Duy Nhanh Và Chậm', 'Khám phá hai hệ thống tư duy của con người.', NULL, 'PAPERBACK', 175000, 10000, 50000, 140000, 120000, 2022, 'Thế Giới', 'vi', 608, TRUE),
  ('9786040000013', 'Đi Tìm Lẽ Sống', 'Hồi ký về ý nghĩa cuộc đời trong nghịch cảnh.', NULL, 'PAPERBACK', 118000, 7000, 35000, 95000, 85000, 2021, 'Văn Học', 'vi', 224, TRUE),
  ('9786040000014', 'Bố Già', 'Tiểu thuyết về gia đình Corleone.', NULL, 'PAPERBACK', 145000, 8000, 40000, 120000, 105000, 2023, 'Đông A', 'vi', 592, TRUE),
  ('9786040000015', 'Sherlock Holmes Toàn Tập', 'Những vụ án kinh điển của thám tử Sherlock Holmes.', NULL, 'HARDCOVER', 220000, 12000, 60000, 180000, 160000, 2022, 'Đông A', 'vi', 720, TRUE)
ON CONFLICT (isbn) DO NOTHING;

INSERT INTO categories (name, slug) VALUES
  ('Văn học', 'van-hoc'),
  ('Kỹ năng sống', 'ky-nang-song'),
  ('Khoa học', 'khoa-hoc'),
  ('Công nghệ', 'cong-nghe')
ON CONFLICT DO NOTHING;

WITH seed_book_categories (isbn, category_slug) AS (
  VALUES
    ('9786040000001', 'van-hoc'), ('9786040000002', 'van-hoc'),
    ('9786040000003', 'van-hoc'), ('9786040000004', 'van-hoc'),
    ('9786040000005', 'ky-nang-song'), ('9786040000006', 'khoa-hoc'),
    ('9786040000007', 'ky-nang-song'), ('9786040000008', 'ky-nang-song'),
    ('9786040000009', 'ky-nang-song'), ('9786040000010', 'cong-nghe'),
    ('9786040000011', 'cong-nghe'), ('9786040000012', 'khoa-hoc'),
    ('9786040000013', 'ky-nang-song'), ('9786040000014', 'van-hoc'),
    ('9786040000015', 'van-hoc')
)
INSERT INTO book_categories (book_id, category_id)
SELECT b.id, c.id
FROM seed_book_categories sbc
JOIN books b ON b.isbn = sbc.isbn
JOIN categories c ON c.slug = sbc.category_slug
ON CONFLICT DO NOTHING;

WITH seed_books (isbn) AS (
  VALUES
    ('9786040000001'), ('9786040000002'), ('9786040000003'),
    ('9786040000004'), ('9786040000005'), ('9786040000006'),
    ('9786040000007'), ('9786040000008'), ('9786040000009'),
    ('9786040000010'), ('9786040000011'), ('9786040000012'),
    ('9786040000013'), ('9786040000014'), ('9786040000015')
), seed_copies (copy_number) AS (VALUES (1), (2))
INSERT INTO book_copies (book_id, status, condition_status, notes)
SELECT b.id, 'AVAILABLE', 'GOOD', 'Development seed copy ' || sc.copy_number
FROM seed_books sb
JOIN books b ON b.isbn = sb.isbn
CROSS JOIN seed_copies sc
WHERE NOT EXISTS (
  SELECT 1
  FROM book_copies bc
  WHERE bc.book_id = b.id
    AND bc.notes = 'Development seed copy ' || sc.copy_number
);

INSERT INTO vouchers (
  code, name, description, discount_type, discount_value, max_discount_amount,
  min_order_amount, start_at, end_at, usage_limit_total, usage_limit_per_user, status
) VALUES
  ('WELCOME10', 'Chào mừng khách mới', 'Giảm 10% cho đơn thuê hoặc mua sách.', 'PERCENTAGE', 10, 50000, 100000, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '90 days', 1000, 1, 1),
  ('RENT50K', 'Ưu đãi thuê sách', 'Giảm 50.000 VND cho đơn từ 250.000 VND.', 'FIXED_AMOUNT', 50000, NULL, 250000, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '60 days', 500, 1, 1),
  ('SUMMER15', 'Khuyến mãi mùa hè', 'Giảm 15% cho đơn từ 300.000 VND.', 'PERCENTAGE', 15, 75000, 300000, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '45 days', 300, 1, 1)
ON CONFLICT (code) DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  discount_type = EXCLUDED.discount_type,
  discount_value = EXCLUDED.discount_value,
  max_discount_amount = EXCLUDED.max_discount_amount,
  min_order_amount = EXCLUDED.min_order_amount,
  start_at = EXCLUDED.start_at,
  end_at = EXCLUDED.end_at,
  usage_limit_total = EXCLUDED.usage_limit_total,
  usage_limit_per_user = EXCLUDED.usage_limit_per_user,
  status = EXCLUDED.status;

INSERT INTO subscriptions (name, description, price, duration_days, max_rentals, status)
SELECT 'Đọc Thử', 'Gói thuê sách cơ bản theo tháng.', 99000, 30, 3, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM subscriptions WHERE name = 'Đọc Thử');

INSERT INTO subscriptions (name, description, price, duration_days, max_rentals, status)
SELECT 'Đọc Không Giới Hạn', 'Gói thuê sách nâng cao theo tháng.', 199000, 30, 10, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM subscriptions WHERE name = 'Đọc Không Giới Hạn');

COMMIT;
