-- Non-production homepage showcase seed.
-- Run manually against local/staging only after Flyway; NEVER add this DML to migrations or run it in production.
-- Cover URLs must be reviewed/replaced with owned or licensed assets before any public deployment.

BEGIN;

-- 1. Insert Categories
INSERT INTO categories (name, slug) VALUES
  ('Văn học', 'van-hoc'),
  ('Kỹ năng sống', 'ky-nang-song'),
  ('Khoa học', 'khoa-hoc'),
  ('Công nghệ', 'cong-nghe'),
  ('Kinh tế', 'kinh-te'),
  ('Tâm lý học', 'tam-ly-hoc')
ON CONFLICT (slug) DO NOTHING;

-- 2. Insert Books with Cover Images and Rental/Purchase Pricing
INSERT INTO books (
  isbn, title, description, image_url, format, list_price,
  rental_price_day, rental_price_week, rental_price_month, deposit_amount,
  publish_year, publisher, language, page_count, is_active
) VALUES
  -- Văn học
  ('9786040000001', 'Dế Mèn Phiêu Lưu Ký', 'Hành trình trưởng thành đầy thử thách và bài học nhân văn của Dế Mèn.', NULL, 'PAPERBACK', 85000, 5000, 25000, 70000, 60000, 2020, 'NXB Kim Đồng', 'vi', 192, TRUE),
  ('9786040000002', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Tuổi thơ trong trẻo, hồn nhiên và đầy hoài niệm ở một làng quê miền Trung.', NULL, 'PAPERBACK', 125000, 7000, 35000, 100000, 90000, 2021, 'NXB Trẻ', 'vi', 384, TRUE),
  ('9786040000003', 'Mắt Biếc', 'Câu chuyện tình cảm thanh xuân êm đềm nhưng trầm buồn của Ngạn và Hà Lan.', NULL, 'PAPERBACK', 110000, 6000, 30000, 90000, 80000, 2022, 'NXB Trẻ', 'vi', 296, TRUE),
  ('9786040000004', 'Nhà Giả Kim', 'Hành trình đi tìm kho báu và bài học sâu sắc về việc theo đuổi ước mơ.', NULL, 'PAPERBACK', 99000, 6000, 30000, 80000, 70000, 2020, 'NXB Nhã Nam', 'vi', 228, TRUE),
  ('9786040000014', 'Bố Già (The Godfather)', 'Tiểu thuyết kinh điển về gia tộc Corleone và thế giới ngầm nước Mỹ.', NULL, 'PAPERBACK', 145000, 8000, 40000, 120000, 105000, 2023, 'NXB Đông A', 'vi', 592, TRUE),
  ('9786040000015', 'Sherlock Holmes Toàn Tập', 'Những vụ án trinh thám ly kỳ và tư duy phá án tài tình của thám tử lừng danh.', NULL, 'HARDCOVER', 220000, 12000, 60000, 180000, 160000, 2022, 'NXB Đông A', 'vi', 720, TRUE),
  ('9780141439518', 'Kiêu Hãnh Và Định Kiến', 'Câu chuyện tình yêu đầy trắc trở giữa Elizabeth Bennet và ngài Darcy.', NULL, 'PAPERBACK', 135000, 7000, 35000, 100000, 95000, 2021, 'NXB Văn Học', 'vi', 435, TRUE),
  ('9780451524935', '1984', 'Tiểu thuyết viễn tưởng kinh điển của George Orwell về xã hội bị kiểm soát tuyệt đối.', NULL, 'PAPERBACK', 120000, 7000, 32000, 90000, 85000, 2020, 'NXB Nhã Nam', 'vi', 328, TRUE),
  ('9780061120084', 'Giết Con Chim Nhại', 'Bài học về lòng nhân ái, sự công bằng và chống phân biệt chủng tộc.', NULL, 'PAPERBACK', 140000, 8000, 38000, 110000, 100000, 2022, 'NXB Nhã Nam', 'vi', 323, TRUE),
  ('9780743273565', 'Gatsby Vĩ Đại', 'Bức tranh giấc mơ Mỹ hoang hoài và bi kịch tình yêu của Jay Gatsby.', NULL, 'PAPERBACK', 105000, 6000, 28000, 80000, 75000, 2021, 'NXB Hội Nhà Văn', 'vi', 208, TRUE),
  ('9780307474278', 'Mật Mã Da Vinci', 'Hành trình giải mã các bí ẩn lịch sử và tôn giáo đầy nghẹt thở.', NULL, 'PAPERBACK', 165000, 9000, 45000, 130000, 120000, 2022, 'NXB Bách Khoa', 'vi', 608, TRUE),
  ('9780525559474', 'Thư Viện Nửa Đêm', 'Giữa sự sống và cái chết là một thư viện chứa vô vàn lựa chọn cuộc đời.', NULL, 'PAPERBACK', 148000, 8000, 40000, 115000, 105000, 2023, 'NXB Nhã Nam', 'vi', 288, TRUE),
  ('9786040000016', 'Cây Cam Ngọt Của Tôi', 'Câu chuyện xúc động về tình yêu thương, sự cô đơn và tuổi thơ của chú bé Zezé.', NULL, 'PAPERBACK', 108000, 6000, 30000, 85000, 75000, 2021, 'NXB Nhã Nam', 'vi', 244, TRUE),
  ('9786040000017', 'Hoàng Tử Bé (Le Petit Prince)', 'Cuộc phiêu lưu kỳ diệu của Hoàng tử bé và những bài học triết lý nhân sinh.', NULL, 'PAPERBACK', 85000, 5000, 24000, 68000, 60000, 2022, 'NXB Kim Đồng', 'vi', 160, TRUE),
  ('9786040000018', 'Số Đỏ', 'Tác phẩm trào phúng kinh điển về xã hội thành thị Việt Nam thời kỳ Âu hóa.', NULL, 'PAPERBACK', 95000, 5000, 26000, 75000, 65000, 2020, 'NXB Văn Học', 'vi', 260, TRUE),

  -- Kỹ năng sống
  ('9786040000007', 'Atomic Habits - Thói Quen Nguyên Tử', 'Thay đổi nhỏ, kết quả kinh ngạc: Phương pháp xây dựng thói quen tốt hiệu quả.', NULL, 'PAPERBACK', 158000, 9000, 45000, 130000, 110000, 2023, 'NXB Nhã Nam', 'vi', 320, TRUE),
  ('9786040000008', 'Đắc Nhân Tâm', 'Nghệ thuật giao tiếp, ứng xử và thu phục lòng người kinh điển nhất mọi thời đại.', NULL, 'PAPERBACK', 108000, 6000, 30000, 90000, 75000, 2021, 'NXB First News', 'vi', 320, TRUE),
  ('9786040000009', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 'Cuốn sách truyền cảm hứng học tập, trải nghiệm và định hướng cho giới trẻ.', NULL, 'PAPERBACK', 98000, 6000, 30000, 80000, 70000, 2022, 'NXB Nhã Nam', 'vi', 280, TRUE),
  ('9786040000013', 'Đi Tìm Lẽ Sống (Man''s Search for Meaning)', 'Trải nghiệm trong trại tập trung Nazi và bài học tìm kiếm ý nghĩa cuộc sống.', NULL, 'PAPERBACK', 118000, 7000, 35000, 95000, 85000, 2021, 'NXB Văn Học', 'vi', 224, TRUE),
  ('9786040000019', 'Khéo Ăn Nói Sẽ Có Được Thiên Hạ', 'Kỹ năng giao tiếp ứng xử giúp bạn làm chủ các mối quan hệ và sự nghiệp.', NULL, 'PAPERBACK', 128000, 7000, 36000, 100000, 90000, 2022, 'NXB Thanh Niên', 'vi', 350, TRUE),
  ('9786040000020', 'Đọc Vị Bất Kỳ Ai', 'Cẩm nang thấu hiểu tâm lý, cảm xúc và hành vi người đối diện.', NULL, 'PAPERBACK', 115000, 6000, 32000, 90000, 80000, 2021, 'NXB Lao Động', 'vi', 256, TRUE),
  ('9786040000021', '7 Thói Quen Để Thành Đạt', 'Những nguyên tắc cốt lõi giúp bạn đạt được hiệu quả cá nhân và tổ chức.', NULL, 'PAPERBACK', 169000, 9000, 48000, 135000, 120000, 2022, 'NXB First News', 'vi', 480, TRUE),
  ('9786040000022', 'Sức Mạnh Của Hiện Tại', 'Hướng dẫn thức tỉnh tâm thức và sống trọn vẹn trong khoảnh khắc hiện tại.', NULL, 'PAPERBACK', 145000, 8000, 40000, 115000, 100000, 2023, 'NXB Thái Hà', 'vi', 312, TRUE),

  -- Khoa học
  ('9786040000006', 'Sapiens: Lược Sử Loài Người', 'Hành trình tiến hóa của loài người từ thời kỳ đồ đá đến kỷ nguyên hiện đại.', NULL, 'PAPERBACK', 189000, 10000, 50000, 150000, 130000, 2022, 'NXB Tri Thức', 'vi', 560, TRUE),
  ('9786040000012', 'Tư Duy Nhanh Và Chậm', 'Giải mã hai hệ thống tư duy chi phối mọi quyết định của con người.', NULL, 'PAPERBACK', 175000, 10000, 50000, 140000, 120000, 2022, 'NXB Thế Giới', 'vi', 608, TRUE),
  ('9786040000005', 'Muôn Kiếp Nhân Sinh', 'Những chiêm nghiệm sâu sắc về nhân quả, vũ trụ và đời sống con người.', NULL, 'HARDCOVER', 168000, 9000, 45000, 140000, 120000, 2023, 'NXB First News', 'vi', 432, TRUE),
  ('9786040000023', 'Vũ Trụ (Cosmos)', 'Khám phá vẻ đẹp kỳ diệu của vũ trụ, hành tinh và nguồn gốc sự sống.', NULL, 'PAPERBACK', 195000, 11000, 55000, 155000, 140000, 2021, 'NXB Nhã Nam', 'vi', 450, TRUE),
  ('9786040000024', 'Lược Sử Thời Gian', 'Cuốn sách khoa học đại chúng kinh điển về lỗ đen, Big Bang và vũ trụ.', NULL, 'PAPERBACK', 135000, 7000, 38000, 105000, 95000, 2020, 'NXB Trẻ', 'vi', 280, TRUE),
  ('9786040000025', 'Homo Deus: Lược Sử Tương Lai', 'Dự báo về tương lai của nhân loại trong kỷ nguyên trí tuệ nhân tạo và công nghệ sinh học.', NULL, 'PAPERBACK', 198000, 11000, 56000, 160000, 140000, 2023, 'NXB Tri Thức', 'vi', 520, TRUE),

  -- Công nghệ
  ('9786040000010', 'Clean Code - Mã Sạch', 'Cẩm nang hướng dẫn nguyên tắc viết code sạch, dễ đọc, dễ bảo trì.', NULL, 'PAPERBACK', 245000, 13000, 65000, 200000, 180000, 2021, 'O''Reilly Media', 'en', 464, TRUE),
  ('9786040000011', 'Design Patterns - Các Mẫu Thiết Kế', '23 mẫu thiết kế phần mềm kinh điển nền tảng cho lập trình hướng đối tượng.', NULL, 'HARDCOVER', 295000, 15000, 75000, 240000, 220000, 2020, 'Addison-Wesley', 'en', 416, TRUE),
  ('9780135957059', 'The Pragmatic Programmer', 'Các lời khuyên thực tế giúp bạn trở thành một lập trình viên chuyên nghiệp.', NULL, 'PAPERBACK', 265000, 14000, 70000, 215000, 190000, 2022, 'Addison-Wesley', 'en', 352, TRUE),
  ('9780321125217', 'Domain-Driven Design', 'Phương pháp luận kết nối thiết kế phần mềm với miền nghiệp vụ phức tạp.', NULL, 'HARDCOVER', 310000, 16000, 80000, 250000, 230000, 2021, 'Addison-Wesley', 'en', 529, TRUE),
  ('9780134494166', 'Clean Architecture', 'Hướng dẫn xây dựng cấu trúc phần mềm linh hoạt, dễ mở rộng và kiểm thử.', NULL, 'PAPERBACK', 275000, 14000, 72000, 220000, 200000, 2022, 'Prentice Hall', 'en', 432, TRUE),
  ('9780134685991', 'Effective Java (3rd Edition)', 'Các thực hành tốt nhất giúp bạn khai thác tối đa sức mạnh của ngôn ngữ Java.', NULL, 'PAPERBACK', 285000, 15000, 74000, 230000, 210000, 2020, 'Addison-Wesley', 'en', 416, TRUE),

  -- Kinh tế
  ('9786040000026', 'Dạy Con Làm Giàu (Rich Dad Poor Dad)', 'Thay đổi tư duy tài chính, phân biệt tài sản và tiêu sản để tự do tài chính.', NULL, 'PAPERBACK', 138000, 7000, 38000, 110000, 98000, 2022, 'NXB Trẻ', 'vi', 384, TRUE),
  ('9786040000027', 'Nhà Đầu Tư Thông Thái', 'Cuốn sách gối đầu giường về đầu tư giá trị của người thầy Warren Buffett.', NULL, 'PAPERBACK', 245000, 13000, 65000, 195000, 180000, 2021, 'NXB Lao Động', 'vi', 640, TRUE),
  ('9786040000028', 'Từ Tốt Đến Vĩ Đại (Good to Great)', 'Nghiên cứu về các công ty vượt trội và bí quyết chuyển mình vĩ đại.', NULL, 'PAPERBACK', 185000, 10000, 50000, 145000, 130000, 2020, 'NXB Trẻ', 'vi', 400, TRUE),
  ('9786040000029', 'Tâm Lý Học Về Tiền', 'Những bài học vượt thời gian về tiền bạc, sự tham vọng và hạnh phúc.', NULL, 'PAPERBACK', 152000, 8000, 42000, 120000, 110000, 2023, 'NXB Thái Hà', 'vi', 304, TRUE),
  ('9786040000030', 'Chiến Lược Đại Dương Xanh', 'Cách tạo dựng khoảng trống thị trường không cạnh tranh và vô hiệu hóa đối thủ.', NULL, 'PAPERBACK', 178000, 9000, 48000, 140000, 125000, 2021, 'NXB Tri Thức', 'vi', 360, TRUE),

  -- Tâm lý học
  ('9786040000031', 'Tâm Lý Học Đám Đông', 'Nghiên cứu kinh điển về tâm lý và bản chất hành vi của tập thể đám đông.', NULL, 'PAPERBACK', 112000, 6000, 30000, 88000, 80000, 2021, 'NXB Thế Giới', 'vi', 272, TRUE),
  ('9786040000032', 'Phi Lý Trí (Predictably Irrational)', 'Khám phá những lực lượng ngầm chi phối các quyết định thiếu lý trí của chúng ta.', NULL, 'PAPERBACK', 148000, 8000, 40000, 115000, 105000, 2022, 'NXB Trẻ', 'vi', 380, TRUE),
  ('9786040000033', 'Tâm Lý Học Tội Phạm', 'Phân tích diễn biến tâm lý, nguyên nhân và động cơ đằng sau hành vi phạm tội.', NULL, 'PAPERBACK', 165000, 9000, 45000, 130000, 120000, 2023, 'NXB Thanh Niên', 'vi', 420, TRUE)
ON CONFLICT (isbn) DO NOTHING;

-- 3. Link Books with Categories
WITH seed_book_categories (isbn, category_slug) AS (
  VALUES
    ('9786040000001', 'van-hoc'), ('9786040000002', 'van-hoc'), ('9786040000003', 'van-hoc'),
    ('9786040000004', 'van-hoc'), ('9786040000014', 'van-hoc'), ('9786040000015', 'van-hoc'),
    ('9780141439518', 'van-hoc'), ('9780451524935', 'van-hoc'), ('9780061120084', 'van-hoc'),
    ('9780743273565', 'van-hoc'), ('9780307474278', 'van-hoc'), ('9780525559474', 'van-hoc'),
    ('9786040000016', 'van-hoc'), ('9786040000017', 'van-hoc'), ('9786040000018', 'van-hoc'),
    
    ('9786040000007', 'ky-nang-song'), ('9786040000008', 'ky-nang-song'), ('9786040000009', 'ky-nang-song'),
    ('9786040000013', 'ky-nang-song'), ('9786040000019', 'ky-nang-song'), ('9786040000020', 'ky-nang-song'),
    ('9786040000021', 'ky-nang-song'), ('9786040000022', 'ky-nang-song'),

    ('9786040000006', 'khoa-hoc'), ('9786040000012', 'khoa-hoc'), ('9786040000005', 'khoa-hoc'),
    ('9786040000023', 'khoa-hoc'), ('9786040000024', 'khoa-hoc'), ('9786040000025', 'khoa-hoc'),

    ('9786040000010', 'cong-nghe'), ('9786040000011', 'cong-nghe'), ('9780135957059', 'cong-nghe'),
    ('9780321125217', 'cong-nghe'), ('9780134494166', 'cong-nghe'), ('9780134685991', 'cong-nghe'),

    ('9786040000026', 'kinh-te'), ('9786040000027', 'kinh-te'), ('9786040000028', 'kinh-te'),
    ('9786040000029', 'kinh-te'), ('9786040000030', 'kinh-te'),

    ('9786040000031', 'tam-ly-hoc'), ('9786040000032', 'tam-ly-hoc'), ('9786040000033', 'tam-ly-hoc')
)
INSERT INTO book_categories (book_id, category_id)
SELECT b.id, c.id
FROM seed_book_categories sbc
JOIN books b ON b.isbn = sbc.isbn
JOIN categories c ON c.slug = sbc.category_slug
ON CONFLICT DO NOTHING;

-- 4. Insert Physical Book Copies (3 copies per book for rental stock)
WITH target_books AS (
  SELECT id, isbn FROM books
  WHERE isbn IN (
    '9786040000001', '9786040000002', '9786040000003', '9786040000004', '9786040000005',
    '9786040000006', '9786040000007', '9786040000008', '9786040000009', '9786040000010',
    '9786040000011', '9786040000012', '9786040000013', '9786040000014', '9786040000015',
    '9786040000016', '9786040000017', '9786040000018', '9786040000019', '9786040000020',
    '9786040000021', '9786040000022', '9786040000023', '9786040000024', '9786040000025',
    '9786040000026', '9786040000027', '9786040000028', '9786040000029', '9786040000030',
    '9786040000031', '9786040000032', '9786040000033', '9780141439518', '9780451524935',
    '9780061120084', '9780743273565', '9780307474278', '9780525559474', '9780135957059',
    '9780321125217', '9780134494166', '9780134685991'
  )
), seed_copies (copy_number) AS (
  VALUES (1), (2), (3)
)
INSERT INTO book_copies (book_id, status, condition_status, notes)
SELECT b.id, 'AVAILABLE', 'GOOD', 'Stock copy #' || sc.copy_number
FROM target_books b
CROSS JOIN seed_copies sc
WHERE NOT EXISTS (
  SELECT 1
  FROM book_copies bc
  WHERE bc.book_id = b.id
    AND bc.notes = 'Stock copy #' || sc.copy_number
);

-- 5. Homepage curation and deterministic new-arrival ordering.
UPDATE books
SET is_featured = isbn IN ('9786040000033', '9786040000010', '9786040000015'),
    is_bestseller = isbn IN ('9786040000007', '9786040000008', '9786040000006', '9786040000029', '9786040000015'),
    created_at = CASE isbn
      WHEN '9786040000033' THEN TIMESTAMP '2026-07-31 10:00:00'
      WHEN '9786040000029' THEN TIMESTAMP '2026-07-30 10:00:00'
      WHEN '9786040000015' THEN TIMESTAMP '2026-07-29 10:00:00'
      WHEN '9780135957059' THEN TIMESTAMP '2026-07-28 10:00:00'
      WHEN '9786040000017' THEN TIMESTAMP '2026-07-27 10:00:00'
      ELSE created_at
    END
WHERE isbn IN (
  '9786040000033', '9786040000010', '9786040000015', '9786040000007', '9786040000008',
  '9786040000006', '9786040000029', '9780135957059', '9786040000017'
);

-- 6. Published editorial content for local/staging homepage verification.
-- Cover URLs intentionally remain NULL until approved assets are available.
INSERT INTO blog_posts (slug, title, excerpt, content, cover_image_url, status, book_id, published_at)
SELECT seed.slug, seed.title, seed.excerpt, seed.content, NULL, 'PUBLISHED', b.id, seed.published_at
FROM (
  VALUES
    ('sach-cong-nghe-cho-nguoi-moi-bat-dau', 'Sách công nghệ cho người mới bắt đầu', 'Năm cuốn giúp bạn bước vào thế giới công nghệ với nền tảng vững chắc.', E'## Bắt đầu từ tư duy\n\nThực hành đều và giữ một nhịp đọc bền vững.', '9786040000010', TIMESTAMP '2026-07-31 10:00:00'),
    ('kinh-te-cho-nguoi-moi', 'Sách kinh tế cho người mới bắt đầu', 'Gợi ý đọc để hiểu tiền bạc, đầu tư và các quyết định thường ngày.', E'## Đọc để đối chiếu\n\nĐọc chậm, ghi chú và đối chiếu với mục tiêu tài chính của bạn.', '9786040000029', TIMESTAMP '2026-07-29 10:00:00'),
    ('tam-ly-hoc-ung-dung', 'Tâm lý học ứng dụng trong đời sống', 'Những cuốn sách khơi gợi cách quan sát hành vi và cảm xúc.', E'## Quan sát với tinh thần cởi mở\n\nTôn trọng bối cảnh riêng của mỗi người khi đọc về hành vi và cảm xúc.', '9786040000033', TIMESTAMP '2026-07-27 10:00:00'),
    ('tu-sach-van-hoc-de-doc-lai', 'Tủ sách văn học để đọc lại', 'Các tác phẩm để trở về khi bạn muốn đọc chậm hơn.', E'## Đọc lại để thấy điều mới\n\nMột cuốn sách hay thường mở ra câu hỏi mới ở mỗi lần đọc lại.', '9786040000015', TIMESTAMP '2026-07-25 10:00:00')
) AS seed(slug, title, excerpt, content, isbn, published_at)
JOIN books b ON b.isbn = seed.isbn
ON CONFLICT (slug) DO UPDATE SET
  title = EXCLUDED.title,
  excerpt = EXCLUDED.excerpt,
  content = EXCLUDED.content,
  status = EXCLUDED.status,
  book_id = EXCLUDED.book_id,
  published_at = EXCLUDED.published_at,
  updated_at = CURRENT_TIMESTAMP;

-- 7. International-reader editorial collection. These are editorial-only
-- catalogue records: unavailable for commerce until local edition, pricing,
-- stock, and asset rights have been verified.
INSERT INTO books (
  isbn, title, description, image_url, format, list_price,
  rental_price_day, rental_price_week, rental_price_month, deposit_amount,
  publish_year, publisher, language, page_count, is_active
) VALUES
  ('9781649374042', 'Fourth Wing', 'A dragon-rider fantasy about survival, trust, and the cost of choosing your own side.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2023, 'Red Tower Books', 'en', NULL, FALSE),
  ('9781649374189', 'Onyx Storm', 'A high-stakes continuation for readers who want action, loyalty, and difficult choices.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2025, 'Red Tower Books', 'en', 544, FALSE),
  ('9781538742570', 'The Housemaid', 'A domestic thriller where an apparently perfect home hides a shifting balance of power.', NULL, 'PAPERBACK', 0, 0, 0, 0, 0, 2022, 'Grand Central Publishing', 'en', 336, FALSE),
  ('9781401971366', 'The Let Them Theory', 'A practical self-help framework about focusing energy on choices within your control.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2024, 'Hay House', 'en', 336, FALSE),
  ('9781250178633', 'The Women', 'A novel about friendship, war, and the private cost of becoming an adult in public history.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2024, 'St. Martin''s Press', 'en', 480, FALSE),
  ('9780385550369', 'James', 'A reimagining that changes the point of view and makes familiar history feel newly unstable.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2024, 'Doubleday', 'en', 318, FALSE),
  ('9780593135204', 'Project Hail Mary', 'A science-fiction survival story built around curiosity, problem-solving, and an unlikely friendship.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2021, 'Ballantine Books', 'en', 496, FALSE),
  ('9780593655030', 'The Anxious Generation', 'A conversation-starter about adolescence, attention, technology, and the environments young people need.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2024, 'Penguin Press', 'en', NULL, FALSE),
  ('9780063250833', 'Yellowface', 'A sharp publishing-world satire about ambition, authorship, and the stories people choose to believe.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2023, 'William Morrow', 'en', NULL, FALSE),
  ('9780593321201', 'Tomorrow, and Tomorrow, and Tomorrow', 'A long friendship novel that uses game design to ask what collaboration can and cannot repair.', NULL, 'HARDCOVER', 0, 0, 0, 0, 0, 2022, 'Knopf', 'en', 416, FALSE)
ON CONFLICT (isbn) DO UPDATE SET
  title = EXCLUDED.title,
  description = EXCLUDED.description,
  format = EXCLUDED.format,
  publish_year = EXCLUDED.publish_year,
  publisher = EXCLUDED.publisher,
  language = EXCLUDED.language,
  page_count = EXCLUDED.page_count,
  is_active = EXCLUDED.is_active;

WITH editorial_book_categories (isbn, category_slug) AS (
  VALUES
    ('9781649374042', 'van-hoc'), ('9781649374189', 'van-hoc'), ('9781538742570', 'van-hoc'),
    ('9781250178633', 'van-hoc'), ('9780385550369', 'van-hoc'), ('9780063250833', 'van-hoc'),
    ('9780593321201', 'van-hoc'), ('9780593135204', 'khoa-hoc'), ('9781401971366', 'ky-nang-song'),
    ('9780593655030', 'tam-ly-hoc')
)
INSERT INTO book_categories (book_id, category_id)
SELECT b.id, c.id
FROM editorial_book_categories ebc
JOIN books b ON b.isbn = ebc.isbn
JOIN categories c ON c.slug = ebc.category_slug
ON CONFLICT DO NOTHING;

INSERT INTO blog_posts (slug, title, excerpt, content, cover_image_url, status, book_id, published_at)
SELECT seed.slug, seed.title, seed.excerpt, seed.content, seed.cover_image_url,
       'PUBLISHED', b.id, seed.published_at
FROM (
  VALUES
    (
      'fourth-wing-khong-chi-la-chuyen-rong',
      'Fourth Wing: khi sự can đảm không còn là một câu khẩu hiệu',
      'Một fantasy nhịp nhanh về lựa chọn, lòng tin và cái giá của việc trưởng thành trong một thế giới đầy luật ngầm.',
      $$## Vì sao cuốn này cuốn đến vậy?

*Fourth Wing* mở đầu như một bài kiểm tra sinh tồn, nhưng thứ giữ người đọc ở lại không chỉ là rồng hay những màn huấn luyện khắc nghiệt. Đó là cảm giác một nhân vật đang bị ép phải sống theo kịch bản của người khác, rồi dần học cách viết lại nó bằng chính lựa chọn của mình.

## Ba điều đáng mang theo sau khi gấp sách

- **Can đảm không phải không sợ.** Nó là quyết định vẫn bước tiếp khi đã nhìn thấy rủi ro.
- **Năng lực cần thời gian để thành hình.** Một người yếu thế không trở nên đáng tin chỉ vì một khoảnh khắc chiến thắng.
- **Lòng tin là tài sản đắt giá.** Trong thế giới nhiều bí mật, biết ai đáng tin thường khó hơn biết ai mạnh hơn.

> Đọc cuốn này như một chuyến tàu lượn: đừng vội giải nghĩa mọi bí mật, hãy để nhịp truyện kéo bạn đi.

## Nên đọc thế nào?

Nếu bạn mới bước vào fantasy, hãy đọc chậm khoảng một trăm trang đầu để làm quen luật chơi. Sau đó, câu chuyện tăng tốc rất nhanh. Cuốn sách hợp với người muốn một trải nghiệm giàu cảm xúc, nhưng vẫn cần một nhân vật chính biết suy nghĩ thay vì chỉ chờ được cứu.$$,
      NULL,
      '9781649374042', TIMESTAMP '2026-07-31 09:00:00'
    ),
    (
      'onyx-storm-doc-phan-tiep-theo-khong-lo-spoiler',
      'Onyx Storm: đọc phần tiếp theo mà không để spoiler cướp mất niềm vui',
      'Một bài đọc nhập môn không tiết lộ nút thắt, dành cho người muốn quay lại thế giới rồng mà vẫn giữ nguyên cảm giác khám phá.',
      $$## Trước khi mở trang đầu

Phần tiếp theo luôn có một thử thách lạ: người đọc vừa muốn trở lại với những nhân vật cũ, vừa sợ câu chuyện lặp lại chính mình. *Onyx Storm* hấp dẫn ở chỗ nó không cho các mối quan hệ đứng yên. Những lời hứa cũ phải chịu áp lực mới, và những điều tưởng đã hiểu lại cần được nhìn lại.

## Cách vào truyện cho trọn vẹn

1. Đọc lại vài ghi chú hoặc chương cuối phần trước, thay vì lao ngay vào bản tóm tắt đầy đủ.
2. Tạm tránh video, bình luận và tiêu đề giật gân. Một câu vô tình cũng đủ làm mất một cú ngoặt hay.
3. Để ý những cuộc đối thoại nhỏ. Chúng thường nói nhiều hơn các cảnh hành động lớn.

## Điều cuốn sách làm tốt

Cuốn này dùng nhịp nhanh để đặt câu hỏi chậm: khi nghĩa vụ và tình cảm kéo về hai phía, ta chọn điều gì? Bởi vậy, trải nghiệm hay nhất không phải là đoán đúng chuyện sắp xảy ra, mà là quan sát nhân vật trả giá cho mỗi lựa chọn.

> Đây là bài recap không spoiler. Hãy đọc phần trước trước khi bắt đầu để cảm xúc không bị đứt đoạn.$$,
      NULL,
      '9781649374189', TIMESTAMP '2026-07-30 09:00:00'
    ),
    (
      'the-housemaid-cam-giac-mot-chuong-nua',
      'The Housemaid: vì sao ta luôn tự nhủ “thêm một chương nữa thôi”',
      'Một thriller tâm lý dựng căng thẳng từ những chi tiết rất đời thường: một ngôi nhà đẹp, công việc mới và những điều không ai nói thẳng.',
      $$## Một căn nhà, nhiều luật không thành lời

*The Housemaid* không cần mở đầu bằng tiếng động lớn. Nó chọn một không gian tưởng an toàn rồi đặt vào đó những quy tắc mơ hồ: ai được nói gì, ai có quyền bước vào đâu, và ai phải giả vờ như không nhìn thấy điều bất thường.

## Vì sao nhịp truyện hiệu quả?

- Mỗi chương thường khép lại ở đúng lúc người đọc muốn biết thêm một mẩu thông tin.
- Góc nhìn gần nhân vật khiến sự nghi ngờ lớn dần từ những chi tiết nhỏ.
- Cuốn sách liên tục buộc ta xem lại phán đoán đầu tiên của mình.

## Đọc để thưởng thức, không chỉ để đoán twist

Thriller dễ khiến ta biến việc đọc thành cuộc đua tìm đáp án. Thử chậm lại một nhịp: để ý cách quyền lực vận hành trong căn nhà, cách sự lịch sự có thể trở thành một lớp vỏ, và cách im lặng đôi khi là lựa chọn duy nhất còn lại.

> Càng ít biết trước, trải nghiệm càng tốt. Đừng tìm tóm tắt cốt truyện trước khi đọc.$$,
      NULL,
      '9781538742570', TIMESTAMP '2026-07-29 09:00:00'
    ),
    (
      'let-them-theory-bot-tieu-hao-vi-dieu-khong-the-kiem-soat',
      'The Let Them Theory: bớt tiêu hao vì điều bạn không thể kiểm soát',
      'Một khung suy nghĩ đơn giản để nhận ra ranh giới giữa quan tâm, kiểm soát và tự chịu trách nhiệm cho đời sống của mình.',
      $$## Ý tưởng nghe đơn giản, áp dụng lại không hề dễ

Điểm hấp dẫn của *The Let Them Theory* không nằm ở một mẹo thần kỳ. Nó gợi một câu hỏi rất thẳng: ta đang dùng bao nhiêu năng lượng để cố thay đổi phản ứng, lựa chọn hoặc đánh giá của người khác?

## Ba cách hiểu để không biến nó thành sự buông xuôi

- **Để người khác chọn** không có nghĩa là chấp nhận mọi hành vi làm tổn thương mình.
- **Tập trung vào phần của mình** là đặt ranh giới, giao tiếp rõ và hành động nhất quán.
- **Không kiểm soát được kết quả** không làm nỗ lực của bạn trở nên vô nghĩa.

## Một bài tập đọc chậm

Sau mỗi chương, thử ghi ra một tình huống bạn đang muốn kiểm soát. Bên cạnh nó, chia đôi trang giấy: phần nào thuộc lựa chọn của người khác, phần nào thuộc phản hồi của bạn. Bài tập nhỏ này làm cuốn sách gần với đời sống hơn bất kỳ câu slogan nào.

> Nếu một ý tưởng khiến bạn thấy nhẹ hơn, hãy kiểm tra xem nó có giúp bạn hành động rõ ràng hơn không.$$,
      NULL,
      '9781401971366', TIMESTAMP '2026-07-28 09:00:00'
    ),
    (
      'the-women-khi-lich-su-di-qua-mot-doi-nguoi',
      'The Women: khi lịch sử đi qua một đời người rất riêng',
      'Một cuốn tiểu thuyết về tình bạn, chiến tranh và những vết nứt kéo dài sau khi trang tin đã chuyển sang câu chuyện khác.',
      $$## Lịch sử không chỉ là mốc thời gian

Có những cuốn sách đặt một con người vào giữa biến cố lớn để nhắc ta rằng lịch sử luôn có phần riêng tư. *The Women* đi theo cảm giác đó: những lựa chọn tưởng nhỏ, những tình bạn giữ ta đứng vững, và những điều một người phải mang theo rất lâu sau khi mọi người khác nghĩ rằng chuyện đã qua.

## Điều làm cuốn sách ở lại

- Nó quan tâm tới hậu quả, không chỉ khoảnh khắc kịch tính.
- Tình bạn được viết như một nơi trú ẩn nhưng không được lý tưởng hóa.
- Nhân vật phải tự tìm ngôn ngữ cho trải nghiệm mà xã hội chưa sẵn sàng lắng nghe.

## Ai nên đọc?

Hợp với người thích historical fiction giàu cảm xúc và muốn đọc về chiến tranh từ một khoảng cách gần con người. Hãy dành thời gian sau mỗi phần để thở; đây không phải cuốn nên đọc vội chỉ để hoàn thành mục tiêu số trang.

> Một cuốn sách mạnh không chỉ làm ta biết thêm chuyện đã xảy ra; nó làm ta hỏi ai đã được phép kể chuyện đó.$$,
      NULL,
      '9781250178633', TIMESTAMP '2026-07-27 09:00:00'
    ),
    (
      'james-doi-goc-nhin-doi-ca-cau-chuyen',
      'James: đổi góc nhìn, đổi cả câu chuyện ta tưởng đã biết',
      'Một bản tái tưởng tượng sắc sảo về quyền được kể, quyền được nhìn nhận và sự bất ổn của một câu chuyện quen thuộc.',
      $$## Khi người đứng bên lề cầm lấy giọng kể

*James* gợi ra một trải nghiệm đọc rất thú vị: câu chuyện không chỉ thay đổi vì có thêm chi tiết, mà thay đổi vì người kể đã khác. Khi góc nhìn dịch chuyển, những điều từng được xem là nền cảnh bỗng có trọng lượng, còn điều từng được coi là hiển nhiên bắt đầu lộ ra nhiều khoảng trống.

## Đọc cuốn này để nhìn thấy gì?

- Quyền lực thường ẩn trong việc ai được nói và ai bị nói thay.
- Sự thông minh không phải lúc nào cũng được phép xuất hiện công khai.
- Hài hước có thể là một cách sống sót, không chỉ là một thủ pháp kể chuyện.

## Cách đọc hợp nhất

Không cần phải biết trước tác phẩm nguồn để theo dõi cuốn sách, nhưng nếu bạn từng biết, trải nghiệm đối chiếu sẽ rất giàu có. Đừng chỉ hỏi cốt truyện đổi chỗ nào. Hãy hỏi: cảm giác của mình về các nhân vật đổi ra sao khi tiếng nói trung tâm đã thay đổi?

> Một góc nhìn mới không làm quá khứ biến mất; nó làm ta nhận ra quá khứ từng bị kể thiếu thế nào.$$,
      NULL,
      '9780385550369', TIMESTAMP '2026-07-26 09:00:00'
    ),
    (
      'project-hail-mary-khoa-hoc-van-day-tinh-nguoi',
      'Project Hail Mary: khoa học vẫn có thể rất đỗi tình người',
      'Một cuộc phiêu lưu không gian khiến kiến thức trở thành động lực kể chuyện, chứ không phải rào cản để bước vào.',
      $$## Sự tò mò là nhân vật chính

*Project Hail Mary* có những bài toán, con số và giả thuyết khoa học. Nhưng điều làm nó dễ đọc là mọi kiến thức đều phục vụ một cảm giác rất người: trước một vấn đề quá lớn, ta bắt đầu từ điều nhỏ nhất có thể thử, rồi lặp lại cho đến khi có hy vọng.

## Ba lý do để thử cuốn này

- Mỗi vấn đề được trình bày như một câu đố đủ rõ để người đọc cùng suy nghĩ.
- Nhịp hài hước giữ câu chuyện không rơi vào khô cứng.
- Tình bạn trong truyện nhắc rằng hợp tác không bắt đầu từ giống nhau, mà từ chịu khó hiểu nhau.

## Đọc nếu bạn sợ science fiction quá khó

Đừng áp lực phải nắm mọi thuật ngữ. Hãy theo dõi mục tiêu của nhân vật và cảm xúc sau mỗi lần thử sai. Nếu một đoạn kỹ thuật dày, đọc chậm hơn thay vì bỏ qua; nhiều khi chính cách giải thích đó lại là phần tạo nên khoảnh khắc thỏa mãn nhất.

> Khoa học hay không chỉ cho ta đáp án; nó cho ta một cách tử tế để đặt câu hỏi.$$,
      NULL,
      '9780593135204', TIMESTAMP '2026-07-25 09:00:00'
    ),
    (
      'the-anxious-generation-doc-de-bat-dau-cuoc-tro-chuyen',
      'The Anxious Generation: đọc để bắt đầu cuộc trò chuyện, không để kết luận vội',
      'Một cuốn phi hư cấu gợi nhiều tranh luận về tuổi thơ, công nghệ và môi trường phát triển của người trẻ.',
      $$## Đây là cuốn nên đọc cùng một cây bút

*The Anxious Generation* chạm vào chủ đề dễ gây phản ứng mạnh: màn hình, mạng xã hội, sự lo âu và tuổi thơ. Giá trị của nó nằm ở việc tạo ra một điểm khởi đầu cho cuộc trò chuyện, không phải ở chỗ cung cấp một câu trả lời duy nhất cho mọi gia đình hay mọi trường học.

## Ba câu hỏi đáng ghi lại

1. Trải nghiệm số nào đang giúp kết nối, và trải nghiệm nào đang thay thế đời sống thật?
2. Trẻ em đang có đủ không gian để tự do, thử sai và xây kỹ năng xã hội chưa?
3. Người lớn đang thiết kế môi trường hay chỉ phản ứng khi vấn đề đã lớn?

## Đọc phản biện là cách tôn trọng cuốn sách

Hãy đối chiếu luận điểm với hoàn cảnh của bạn, tìm thêm nguồn có góc nhìn khác và tránh biến một lập luận thành nhãn dán cho cả một thế hệ. Những chủ đề liên quan sức khỏe tinh thần cần sự cẩn trọng và hỗ trợ chuyên môn khi cần thiết.

> Một cuốn sách hay về xã hội không bắt ta đồng ý ngay; nó khiến ta đặt câu hỏi chính xác hơn.$$,
      NULL,
      '9780593655030', TIMESTAMP '2026-07-24 09:00:00'
    ),
    (
      'yellowface-chuyen-ai-duoc-phep-ke',
      'Yellowface: câu chuyện không chỉ là “ai viết hay hơn”',
      'Một satire sắc bén về xuất bản, tham vọng và cách danh tiếng có thể biến một câu chuyện thành cuộc giằng co đạo đức.',
      $$## Một cuốn sách khiến ta khó đứng ngoài

*Yellowface* không viết về ngành xuất bản theo kiểu hậu trường lấp lánh. Nó đẩy người đọc vào vùng khó chịu, nơi tham vọng, ghen tị và cảm giác mình “xứng đáng” liên tục tự biện hộ cho các quyết định tệ hơn.

## Điều nên để ý khi đọc

- Người kể chuyện có thể thuyết phục mà vẫn không đáng tin.
- Thành công công khai thường che giấu nhiều cuộc mặc cả riêng tư.
- Câu hỏi về đại diện không thể tách khỏi quyền lực và lợi ích.

## Đừng chỉ đọc để tìm người có lỗi

Thú vị nhất là khi cuốn sách khiến ta thấy các nhân vật vừa đáng trách vừa đáng nhận ra. Nó không xin người đọc tha thứ cho họ; nó buộc ta quan sát cách một người có thể tự kể lại bản thân cho đến khi tin rằng mình vô tội.

> Satire hiệu quả nhất là satire làm ta cười rồi nhận ra mình vừa cười vì một điều không hề dễ chịu.$$,
      NULL,
      '9780063250833', TIMESTAMP '2026-07-23 09:00:00'
    ),
    (
      'tomorrow-va-tomorrow-va-tomorrow-tinh-ban-va-game',
      'Tomorrow, and Tomorrow, and Tomorrow: tình bạn không có nút “lưu lại”',
      'Một tiểu thuyết dùng thế giới game để kể về sáng tạo chung, tổn thương riêng và những mối quan hệ không thể được sửa bằng một bản cập nhật.',
      $$## Game chỉ là cánh cửa đi vào câu chuyện

Bạn không cần là người chơi game để đọc *Tomorrow, and Tomorrow, and Tomorrow*. Game ở đây là ngôn ngữ: cách các nhân vật hợp tác, cạnh tranh, tạo ra thế giới mới và đôi khi trốn vào đó khi thế giới thật trở nên quá khó chịu.

## Ba lớp cảm xúc của cuốn sách

- **Sáng tạo chung:** một ý tưởng có thể lớn lên nhờ nhiều người, nhưng quyền sở hữu nó hiếm khi đơn giản.
- **Tình bạn dài hạn:** yêu quý một người không đồng nghĩa với luôn hiểu hoặc tha thứ cho họ.
- **Thời gian:** không phải mọi điều đã mất đều có thể quay lại như phiên bản cũ.

## Đọc chậm ở những đoạn lặng

Cuốn sách có nhiều khoảnh khắc không cần cao trào. Đừng lướt qua chúng. Chính các khoảng lặng mới cho thấy nhân vật thay đổi thế nào, và vì sao một dự án chung có thể vừa cứu một mối quan hệ vừa làm nó nứt vỡ.

> Có những cuộc chơi đáng nhớ không vì thắng, mà vì ta từng có người cùng xây thế giới với mình.$$,
      NULL,
      '9780593321201', TIMESTAMP '2026-07-22 09:00:00'
    )
) AS seed(slug, title, excerpt, content, cover_image_url, isbn, published_at)
JOIN books b ON b.isbn = seed.isbn
ON CONFLICT (slug) DO UPDATE SET
  title = EXCLUDED.title,
  excerpt = EXCLUDED.excerpt,
  content = EXCLUDED.content,
  status = EXCLUDED.status,
  book_id = EXCLUDED.book_id,
  published_at = EXCLUDED.published_at;

COMMIT;
