# BẢNG PHÂN CHIA CÔNG VIỆC DỰ ÁN VELSTRONG BOOKSTORE (SPACE BOOK)
## ĐỒ ÁN THẠC SĨ: HỌC PHẦN CƠ SỞ DỮ LIỆU NÂNG CAO (HAUI MASTER)

---

### MỤC LỤC
1. [Thông Tin Đội Ngũ & Định Vị Vai Trò](#1-thông-tin-đội-ngũ--định-vị-vai-trò)
2. [Chi Tiết Phân Chia Công Việc Theo 4 Trụ Cột Kỹ Thuật](#2-chi-tiết-phân-chia-công-việc-theo-4-trụ-cột-kỹ-thuật)
   - 2.1. Trụ Cột 1: Backend Development (Java 21, Spring Boot 4, Hexagonal, MongoDB 7, Redis 7)
   - 2.2. Trụ Cột 2: Frontend Development (Next.js 15, React 19, TypeScript, Tailwind CSS)
   - 2.3. Trụ Cột 3: DevOps, CI/CD & Quản Trị Hạ Tầng
   - 2.4. Trụ Cột 4: Tài Liệu Học Thuật, Sơ Đồ Kiến Trúc & Báo Cáo Thạc Sĩ
3. [Bảng Ma Trận Phân Công Trách Nhiệm (RACI Matrix)](#3-bảng-ma-trận-phân-công-trách-nhiệm-raci-matrix)
4. [Lộ Trình Triển Khai 4 Giai Đoạn (Sprint Roadmap & Milestones)](#4-lộ-trình-triển-khai-4-giai-đoạn-sprint-roadmap--milestones)
5. [Quy Chuẩn Phối Hợp Kỹ Thuật & Quản Trị Chất Lượng Mã Nguồn](#5-quy-chuẩn-phối-hợp-kỹ-thuật--quản-trị-chất-lượng-mã-nguồn)

---

### 1. THÔNG TIN ĐỘI NGŨ & ĐỊNH VỊ VAI TRÒ

Dự án **Velstrong Bookstore (Space Book)** là nền tảng thương mại điện tử kết hợp mua bán và cho thuê sách thông minh, được xây dựng theo tiêu chuẩn công nghiệp (Production-ready) phục vụ học phần **Cơ sở dữ liệu nâng cao** trong chương trình đào tạo Thạc sĩ tại Đại học Công nghiệp Hà Nội (HaUI).

Khối lượng công việc lớn bao gồm toàn bộ các khía cạnh từ kiến trúc backend phân tán, cơ sở dữ liệu tài liệu MongoDB Replica Set, giao diện người dùng hiện đại, hạ tầng DevOps tự động hóa cho đến hồ sơ tài liệu học thuật. Để tối ưu hóa hiệu suất và đảm bảo tính chuyên sâu, công việc được phân chia rõ ràng cho 3 thành viên:

```text
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                               ĐỘI NGŨ DỰ ÁN VELSTRONG BOOKSTORE                         │
├──────────────────────────┬─────────────────────────────┬────────────────────────────────┤
│          MẠNH            │            HIẾU             │              HƯNG              │
│  (Nguyễn Văn Mạnh)       │                             │                                │
├──────────────────────────┼─────────────────────────────┼────────────────────────────────┤
│ • Tech Lead / Architect  │ • Frontend Lead             │ • Backend Business Logic Lead  │
│ • DevOps & Cloud Lead    │ • UI/UX Specialist          │ • Admin Portal Specialist      │
│ • Core Backend & Security│ • Customer Flow Fullstack   │ • Data Arch & Master Docs Lead │
│ • Payment & Integrations │ • Real-time Client & PWA    │ • QA & Benchmark Testing Lead  │
└──────────────────────────┴─────────────────────────────┴────────────────────────────────┘
```

#### Định vị năng lực & Trách nhiệm chính:
1. **MẠNH (Technical Leader / Software Architect / DevOps & Core Backend)**:
   - **Vai trò**: Trưởng nhóm kỹ thuật, chịu trách nhiệm cao nhất về kiến trúc hệ thống, thiết kế hạ tầng dữ liệu, giải pháp bảo mật và quy trình tự động hóa triển khai (CI/CD).
   - **Trọng tâm**: Thiết kế lõi Kiến trúc Lục giác (Hexagonal Architecture), quản trị cụm MongoDB Replica Set `rs0`, cấu hình giao dịch phân tán ACID, cơ chế khóa (Optimistic/Pessimistic Locking), bảo mật JWT đa tầng, tích hợp thanh toán (VNPay & IMAP Poller quét email Timo tự động), thiết lập CI/CD và máy chủ VPS Production.
2. **HIẾU (Frontend Lead / Fullstack Customer Experience Specialist)**:
   - **Vai trò**: Trưởng nhóm giao diện, chịu trách nhiệm chính về toàn bộ trải nghiệm người dùng cuối (Customer Experience), kiến trúc mã nguồn Next.js 15 và kết nối API phía người dùng.
   - **Trọng tâm**: Xây dựng toàn bộ Cổng giao diện khách hàng (Customer Portal), thiết kế Design System & Responsive UI, luồng mua sắm & thuê sách (Dual-mode Cart, Checkout, VietQR dynamic display), tích hợp tính năng thời gian thực (SSE Notifications, Web Push VAPID, Live Support Chat) và phát triển các API Backend phụ trợ cho luồng người dùng cá nhân.
3. **HƯNG (Backend Business Specialist / Admin Portal / QA & Academic Docs Lead)**:
   - **Vai trò**: Chuyên viên logic nghiệp vụ chuyên sâu, phụ trách Cổng quản trị vận hành (Admin Portal), bảo đảm chất lượng phần mềm và chủ trì toàn bộ hồ sơ báo cáo Thạc sĩ.
   - **Trọng tâm**: Hiện thực hóa toàn bộ logic nghiệp vụ quản lý kho bản sao sách (`BookCopy`), quy trình hoàn tất thuê sách (`RentalFulfillment`), máy trạng thái đơn hàng, đối soát thủ công ngân hàng, xây dựng Cổng Quản trị Admin (Next.js Admin Console), viết bộ kiểm thử tự động (ArchUnit & Vitest), và chủ trì soạn thảo Luận văn/Báo cáo đề tài môn CSDL nâng cao kèm sơ đồ kiến trúc động (Archify).

---

### 2. CHI TIẾT PHÂN CHIA CÔNG VIỆC THEO 4 TRỤ CỘT KỸ THUẬT

#### 2.1. Trụ Cột 1: Backend Development (Java 21, Spring Boot 4, Hexagonal + DDD, MongoDB 7, Redis 7)

##### 👤 MẠNH (Core Backend, Security, Persistence & Payment Integration):
- [x] **Thiết kế Khung Kiến trúc Lục giác (Hexagonal Architecture)**:
  - Phân tách cấu trúc module: `domain.*` (hoàn toàn thuần khiết POJO), `application.*` (Use cases & Services), `infrastructure.*` (REST, Mongo, JPA, External adapters).
  - Thiết lập bộ quy tắc kiến trúc bất biến và kiểm soát bằng ArchUnit (`HexagonalArchitectureTest.java`).
- [x] **Cơ chế Bảo mật & Quản lý Phiên (Security & Auth Engine)**:
  - Hiện thực hóa xác thực không trạng thái (Stateless JWT) với Access Token (1h) và Refresh Token (7 ngày).
  - Tích hợp Redis Token Denylist: Hủy token tức thì khi gọi `/api/v1/auth/logout`.
  - Thiết kế Session Versioning (`user_session_versions`): Vô hiệu hóa toàn bộ phiên đăng nhập cũ trên mọi thiết bị khi người dùng đổi mật khẩu hoặc đăng xuất diện rộng.
  - Cơ chế Quên mật khẩu không trạng thái (Stateless HMAC-SHA256 Challenge): Không cần lưu token tạm vào database.
- [x] **Tầng Lưu Trữ MongoDB Lõi (Core Persistence Engine)**:
  - Xây dựng lớp cơ sở `MongoPersistenceSupport` bao bọc `MongoTemplate`.
  - Cơ chế tự sinh khóa chính số nguyên tuần tự (Sequence Pattern) thông qua collection `_mongo_sequences` bằng lệnh nguyên tử `findAndModify` với `$inc`.
  - Hiện thực hóa Khóa Lạc Quan (`saveVersioned()`): So sánh và tăng trường `version`, ném `OptimisticLockingFailureException` khi phát hiện xung đột dữ liệu.
  - Hiện thực hóa Khóa Bi Quan (`findFirstAvailableByBookIdForUpdate()`): Cập nhật `_mongoLock` bên trong giao dịch `ClientSession` để tạo xung đột ghi độc quyền trong WiredTiger Engine.
  - Cấu hình Giao dịch Đa tài liệu (Multi-Document ACID Transactions) với `MongoTransactionManager` và Spring `@Transactional`.
  - Viết bộ khởi tạo chỉ mục và quyền hạn (`MongoSchemaInitializer.java`): Tạo tự động 34 collections, 35+ indexes (Unique, Compound, Sparse) và seed dữ liệu RBAC ban đầu.
- [x] **Hệ Thống Thanh Toán & Đối Soát Ngân Hàng Tự Động**:
  - Tích hợp cổng VNPay: Xây dựng thuật toán sinh URL thanh toán và xác thực chữ ký số HMAC-SHA512 qua IPN & Callback.
  - Sinh mã VietQR động chuẩn Napas247 (BVBank - Timo) kèm mã tham chiếu chuyển khoản độc nhất.
  - Xây dựng background service IMAP Poller (Gmail SSL Port 993): Tự động đọc email báo biến động số dư từ Timo, kiểm tra tính xác thực email (`Authentication-Results`), chống trùng lặp qua `processed_bank_messages`.
  - Tự động khớp lệnh thanh toán: Cập nhật đơn hàng thành `PAID` và `CONFIRMED`, kích hoạt gói hội viên, và đẩy các khoản tiền không khớp vào vùng cách ly `unmatched_transfers`.
- [x] **Hạ tầng Real-time & Web Push**:
  - Xây dựng `UserNotificationEventHub` sử dụng Server-Sent Events (`SseEmitter`) đẩy sự kiện tức thì tới trình duyệt.
  - Triển khai `WebPushNotificationAdapter` tích hợp thư viện BouncyCastle và mã hóa VAPID đẩy thông báo qua Service Worker.

##### 👤 HIẾU (Customer-facing Use Cases & APIs):
- [ ] **Phân hệ Giỏ Hàng Đa Hình (Cart Module)**:
  - Hiện thực hóa `CartController`, `CartService`, `CartItemRepository`.
  - Xử lý logic nghiệp vụ giỏ hàng hỗ trợ song song hai hình thức: Mua sách (`PURCHASE`) và Thuê sách có kỳ hạn (`RENTAL` theo Ngày/Tuần/Tháng).
  - Triển khai logic tự động gộp sản phẩm trùng lặp (Deduplication & Auto-merge) dựa trên `bookId`, `itemType`, `rentalTermValue`, `rentalTermUnit`.
  - Tính toán tiền cọc tạm tính (`depositAmount`) và phí thuê tạm tính.
- [ ] **Phân hệ Khuyến Mãi Phía Khách Hàng (Voucher Quote & Reserve)**:
  - Hiện thực hóa `QuoteVoucherService`: Kiểm tra tính hợp lệ của mã voucher, điều kiện đơn hàng tối thiểu, trích dẫn số tiền giảm thực tế.
  - Hiện thực hóa `ReserveVoucherService`: Giữ chỗ voucher trong `voucher_usages` khi khách hàng bắt đầu tiến trình đặt hàng.
- [ ] **Phân hệ Đặt Hàng Phía Khách Hàng (Customer Order Flow)**:
  - Hiện thực hóa `CreateOrderService`: Tiếp nhận lệnh đặt hàng, thẩm định giỏ hàng, tính toán tổng tiền, gán địa chỉ giao hàng, lưu vết đơn hàng vào collection `orders` và `order_items`, làm sạch giỏ hàng trong cùng một transaction.
  - Hiện thực hóa `GetMyOrdersService`, `GetOrderService`: Tra cứu danh sách đơn hàng cá nhân, thống kê số lượng đơn theo trạng thái (`/api/v1/orders/me/summary`).
  - Xử lý tính năng khách tự hủy đơn (`CancelOrderService`) khi đơn hàng còn ở trạng thái `PENDING`.
- [ ] **Phân hệ Đánh Giá Sách & Sổ Địa Chỉ**:
  - Hiện thực hóa `ReviewEligibilityService`: Thẩm định điều kiện khách hàng đã mua hoặc đã thuê và trả sách mới được gửi đánh giá.
  - Hiện thực hóa `BookReviewService`: Tạo mới và cập nhật đánh giá 1-5 sao kèm bình luận.
  - Hiện thực hóa `AddressService`: Thêm, sửa, xóa địa chỉ nhận hàng và xử lý cờ địa chỉ mặc định (`isDefault`).
- [ ] **Phân hệ Gói Hội Viên Phía Khách Hàng**:
  - Hiện thực hóa `PurchaseSubscriptionService`: Tiếp nhận yêu cầu mua gói đọc sách, tạo yêu cầu thanh toán chuyển khoản hoặc VNPay.
  - Hiện thực hóa `GetActiveSubscriptionService`: Kiểm tra tình trạng hiệu lực của gói hội viên hiện tại của độc giả.

##### 👤 HƯNG (Admin Business Logic, Inventory Management & Rental Engine):
- [ ] **Phân hệ Quản Lý Kho & Bản Sao Sách (Inventory & Book Copies)**:
  - Hiện thực hóa `ManageBookCopiesService` và `BookCopyManagementController`: CRUD các bản sao sách vật lý `BookCopy`, gắn mã vạch định danh, quản lý tình trạng (`NEW`, `GOOD`, `FAIR`, `POOR`) và trạng thái (`AVAILABLE`, `RENTED`, `DAMAGED`, `LOST`).
  - Hiện thực hóa tính năng nhập kho (`stock:receive`), điều chỉnh tồn kho (`stock:adjust`), kiểm kê kho (`inventory:audit`).
  - Hiện thực hóa `BookManagementController`: Cập nhật cờ tủ sách (`BookShelf`: New Arrivals, Bestsellers, Featured), tải lên ảnh bìa sách (`BookCoverUploadController`).
- [ ] **Máy Trạng Thái & Quản Trị Đơn Hàng (Order State Progression)**:
  - Hiện thực hóa `UpdateOrderStatusService`: Kiểm soát các bước chuyển trạng thái đơn hàng (`PENDING` $\rightarrow$ `CONFIRMED` $\rightarrow$ `PROCESSING` $\rightarrow$ `SHIPPING` $\rightarrow$ `COMPLETED`).
  - Ghi vết lịch sử biến động trạng thái kiểm toán bất biến vào `order_status_history`.
  - Cung cấp API cho phép nhân viên xem danh sách toàn bộ đơn hàng hệ thống (`order:read:all`).
- [ ] **Động Cơ Quản Lý Thuê Sách (Rental Fulfillment & Lifecycle)**:
  - Hiện thực hóa `RentalFulfillmentService`, `RentalFulfillmentRecordService`, `RentalFulfillmentRetryService`: Quét các đơn thuê đã thanh toán, tự động gán bản sao sách trống bằng cơ chế khóa bi quan, lập lịch thực hiện lại (Retry) nếu kho tạm thời thiếu bản sao.
  - Hiện thực hóa `ReturnRentalService`: Xử lý quy trình nhận trả sách, thẩm định độ hao mòn, tính toán phí trễ hạn, hoàn trả tiền cọc hoặc khấu trừ tiền cọc vào tài khoản khách.
  - Hiện thực hóa `ForceReturnRentalService`: Đóng cưỡng chế hợp đồng thuê quá hạn lâu ngày (`force-return`), tịch thu tiền cọc và đánh dấu sách mất.
  - Hiện thực hóa `GetOverdueRentalsService`: Quét danh sách các hợp đồng thuê quá hạn để phục vụ công tác nhắc nợ và cảnh báo.
- [ ] **Phân hệ Đối Soát Thủ Công & Quản Trị Hệ Thống**:
  - Hiện thực hóa `ResolveUnmatchedTransferService`: Cho phép nhân viên kế toán/thu ngân tra cứu các giao dịch tiền treo trong `unmatched_transfers` và thực hiện khớp lệnh thủ công với đơn hàng chính xác.
  - Hiện thực hóa CRUD Quản trị Voucher (`VoucherManagementService`): Tạo mới, cấu hình hạn mức, thời gian hiệu lực và đóng mã khuyến mãi.
  - Hiện thực hóa CMS Bài viết (`BlogPostService`): Tạo bài viết, quản lý slug chuẩn SEO, lưu bản nháp và phát hành công khai.
  - Xây dựng các truy vấn thống kê nâng cao (Aggregation): Thuật toán gợi ý sách bán chạy `findTopSellingBooks`, báo cáo doanh thu bán vs doanh thu thuê sách.

---

#### 2.2. Trụ Cột 2: Frontend Development (Next.js 15, React 19, TypeScript, Tailwind CSS)

##### 👤 HIẾU (Chủ trì - Cổng Khách Hàng / Customer Experience Portal):
- [ ] **Kiến trúc Giao diện & Design System**:
  - Cấu hình Next.js 15 App Router, TypeScript, Tailwind CSS v4, hệ thống theme màu sắc thương hiệu Velstrong Bookstore.
  - Thiết kế Site Shell dùng chung: Header (Menu điều hướng, thanh tìm kiếm thông minh, icon giỏ hàng có huy hiệu số lượng, chuông thông báo thời gian thực, menu tài khoản), Footer chuẩn nhận diện.
  - Tối ưu hóa trải nghiệm Responsive: Hiển thị mượt mà trên Mobile, Tablet và Desktop; hỗ trợ Touch Gestures.
- [ ] **Trang Chủ & Trải Nghiệm Khám Phá Sách**:
  - Trang chủ (`/`): Hero Carousel tương tác, dải băng giá trị thương hiệu (Value Props), Campaign Banner, dải biên tập sách (Editorial Strip).
  - Trang danh mục sách (`/sach`): Bộ lọc danh mục đa chiều, lọc theo mức giá, lọc theo hình thức (Mua / Thuê), sắp xếp (Mới nhất, Bán chạy, Giá tăng/giảm), phân trang mượt mà.
  - Trang chi tiết sách (`/sach/[id]`): Thư viện ảnh bìa, tóm tắt tác phẩm, thông số chi tiết (ISBN, NXB, Năm phát hành), bảng giá mua vs giá thuê theo kỳ hạn, hiển thị số lượng bản sao sẵn có trên kệ, khối bình luận & đánh giá đã xác minh (Verified Reviews).
- [ ] **Trải Nghiệm Mua Sắm & Thanh Toán (Cart & Checkout Journey)**:
  - Trang Giỏ hàng (`/gio-hang`): Phân tách rõ ràng giữa mục Mua và mục Thuê, bộ chọn thời hạn thuê (Số ngày/tháng) tự động cập nhật tiền cọc và phí thuê, nhập mã voucher và hiển thị số tiền tiết kiệm.
  - Trang Thanh toán (`/checkout`): Chọn địa chỉ nhận hàng từ sổ địa chỉ hoặc thêm mới, chọn phương thức thanh toán (VNPay, Chuyển khoản VietQR, COD), tóm tắt đơn hàng minh bạch.
  - Trang Thanh toán Chuyển khoản VietQR (`/checkout/[orderId]`): Hiển thị mã QR động chuẩn Napas247, đồng hồ đếm ngược 30 phút, hướng dẫn chuyển tiền, cơ chế tự động lắng nghe kết quả thanh toán qua SSE/polling để chuyển hướng ngay khi tiền vào tài khoản.
- [ ] **Trung Tâm Khách Hàng Cá Nhân (Customer Account Portal)**:
  - Trang thông tin tài khoản (`/account`): Chỉnh sửa hồ sơ, đổi mật khẩu, quản lý sổ địa chỉ nhận hàng.
  - Quản lý đơn hàng (`/account/don-hang` và `/account/don-hang/[id]`): Lịch sử đơn hàng, dòng thời gian trạng thái đơn (Timeline trực quan), nút Hủy đơn cho đơn chờ thanh toán, nút Đánh giá sách cho đơn đã hoàn thành.
  - Quản lý sách thuê (`/account/sach-thue`): Danh sách sách đang thuê, số ngày còn lại, cảnh báo sắp đến hạn trả, nút gửi yêu cầu trả sách hoặc gia hạn thuê.
  - Quản lý gói hội viên (`/goi-thue` và `/account`): Xem danh sách gói cước đọc sách VIP, đăng ký gói mới, kiểm tra ngày hết hạn gói hiện tại.
- [ ] **Hỗ Trợ Trực Tuyến & Thông Báo Đa Kênh Phía Khách Hàng**:
  - Widget chat hỗ trợ trực tuyến (`/ho-tro` & Floating Support Widget): Nhắn tin thời gian thực với nhân viên, đính kèm hình ảnh sách lỗi/biên lai.
  - Chuông thông báo in-app: Danh sách thông báo, đánh dấu đã đọc, đếm số tin chưa đọc.
  - Đăng ký Web Push Notifications qua Service Worker (`notification-service-worker.js`): Nhận thông báo biến động đơn hàng ngay cả khi đã tắt trình duyệt.

##### 👤 HƯNG (Chủ trì - Cổng Quản Trị Vận Hành / Admin & Staff Portal):
- [ ] **Kiến trúc Cổng Quản Trị (`frontend/src/app/(admin)/admin/*`)**:
  - Xây dựng Layout Admin (`admin-shell.tsx`): Thanh điều hướng chuyên dụng cho nhân viên và ban quản trị, phân chia khu vực làm việc theo quyền hạn (Sales Staff, Warehouse Manager, Admin).
- [ ] **Phân Hệ Quản Trị Sách & Kho Hàng (`/admin/sach`)**:
  - Bảng dữ liệu toàn bộ đầu sách: Tìm kiếm, lọc theo thể loại và trạng thái kinh doanh.
  - Modal thêm/sửa sách: Cập nhật thông tin chi tiết, upload ảnh bìa lên media storage, gán cờ tủ sách (`flags`).
  - Giao diện Quản lý Bản sao Vật lý (`BookCopy Manager`): Xem danh sách từng cuốn sách trên kệ kho, thêm bản sao mới, đổi trạng thái (`AVAILABLE`, `RENTED`, `DAMAGED`, `LOST`) và cập nhật tình trạng vật lý sách (`NEW`, `GOOD`, `FAIR`, `POOR`).
  - Khối gợi ý sách bán chạy (`Bestseller Suggestions`) hiển thị dữ liệu phân tích tự động từ hệ thống.
- [ ] **Phân Hệ Quản Trị Đơn Hàng (`/admin/don-hang`)**:
  - Danh sách đơn hàng toàn hệ thống với bộ lọc theo mã đơn, khách hàng, ngày tạo và trạng thái.
  - Giao diện chi tiết đơn hàng: Xem danh sách sản phẩm mua/thuê, thông tin thanh toán, địa chỉ người nhận.
  - Thao tác chuyển đổi trạng thái đơn hàng theo đúng quy trình: Xác nhận đơn (`CONFIRMED`), Chuẩn bị hàng (`PROCESSING`), Giao cho vận chuyển (`SHIPPING`), Hoàn thành (`COMPLETED`).
- [ ] **Phân Hệ Quản Trị Thuê Sách & Quá Hạn (`/admin/thue-sach`)**:
  - Quản lý hợp đồng thuê sách: Tra cứu theo người thuê, cuốn sách, hạn trả.
  - Tab cảnh báo sách quá hạn (`Overdue Rentals`): Danh sách độc giả quá hạn trả sách kèm số ngày trễ và số tiền phạt tạm tính.
  - Thao tác Tiếp nhận trả sách (`Check-in`): Đánh giá tình trạng sách trả lại, hệ thống tự động tính tiền phạt trễ/hỏng và hiển thị số tiền cọc cần hoàn lại cho khách.
  - Thao tác Đóng cưỡng chế hợp đồng (`Force Return`): Xử lý các trường hợp mất sách hoặc khách không trả.
- [ ] **Phân Hệ Quản Trị Đối Soát Ngân Hàng (`/admin/doi-soat`)**:
  - Bảng danh sách các giao dịch chuyển khoản không khớp tự động (`unmatched_transfers`) do IMAP Poller phát hiện.
  - Hiển thị đầy đủ thông tin: Mã giao dịch ngân hàng, số tiền thực nhận, nội dung khách chuyển khoản, thời gian nhận tiền, nguyên nhân không khớp.
  - Modal xử lý đối soát thủ công: Cho phép kế toán tra cứu mã đơn hàng hợp lệ và bấm nút "Khớp lệnh" (`Resolve`) để tự động chuyển trạng thái đơn sang Đã thanh toán và xóa giao dịch khỏi danh sách treo.
- [ ] **Phân Hệ Quản Trị Nội Dung Blog & Hỗ Trợ Khách Hàng**:
  - Quản trị bài viết (`/admin/bai-viet`): Danh sách bài viết, tạo mới/chỉnh sửa với trình soạn thảo Markdown, quản lý trạng thái Bản nháp (`DRAFT`) hoặc Xuất bản (`PUBLISHED`).
  - Cổng chat hỗ trợ của nhân viên (`/admin/ho-tro`): Danh sách các cuộc trò chuyện của khách hàng sắp xếp theo tin nhắn mới nhất, hiển thị số tin chưa đọc, phòng chat trả lời trực tiếp cho khách hàng kèm tải tệp đính kèm.

##### 👤 MẠNH (Tư Vấn Kiến Trúc Frontend, API Client & Security Bridge):
- [ ] **Chuẩn hóa Tầng API Client & Auth Bridge**:
  - Xây dựng tầng HTTP Client đồng nhất (Fetch wrapper), tự động gắn `Authorization: Bearer <token>` cho các request yêu cầu xác thực.
  - Cơ chế tự động làm mới token (Silent Token Refresh): Khi API trả về HTTP 401 Unauthorized, client tự động gọi `/api/v1/auth/refresh` lấy token mới và gửi lại request ban đầu mà không bắt người dùng đăng nhập lại.
  - Xử lý Router Guards & Phân quyền phía Client: Chặn người dùng không có quyền truy cập vào các tuyến đường `/admin/*`, điều hướng về trang đăng nhập khi phiên hết hạn.
  - Tối ưu hóa SEO: Khai báo Metadata động (OpenGraph, Twitter Cards, Canonical URLs) cho từng trang chi tiết sách và bài viết blog.

---

#### 2.3. Trụ Cột 3: DevOps, CI/CD & Quản Trị Hạ Tầng

##### 👤 MẠNH (Chủ trì toàn bộ Hạ tầng, Containerization & CI/CD):
- [x] **Containerization & Docker Orchestration**:
  - Xây dựng cấu hình `docker-compose.yml` tích hợp toàn diện:
    - Cụm MongoDB 7 cấu hình Replica Set `rs0` (`--replSet rs0 --bind_ip_all`) để hỗ trợ Multi-document ACID Transactions.
    - Container phụ `mongo-init` tự động thực thi `rs.initiate()` khi khởi động lần đầu.
    - Dịch vụ Redis 7 phục vụ Caching và Token Denylist.
    - Dịch vụ Backend Spring Boot và Frontend Next.js.
  - Viết `Dockerfile` tối ưu hóa Multi-stage build:
    - Backend: Sử dụng Maven wrapper build jar và chạy trên nền Eclipse Temurin 21 JRE Alpine dung lượng siêu nhẹ, bảo mật cao.
    - Frontend: Sử dụng Node 20 Alpine, kích hoạt chế độ Next.js standalone output nhằm tối thiểu hóa dung lượng image.
- [x] **Xây Dựng Pipeline Tự Động Hóa CI/CD (GitHub Actions)**:
  - **CI Workflow (`.github/workflows/ci.yml`)**:
    - Tự động kích hoạt khi có Pull Request hoặc Push vào nhánh `main`.
    - Kiểm tra chất lượng Frontend: Chạy kiểm tra định dạng ESLint, biên dịch kiểu TypeScript (`tsc --noEmit`), chạy bộ kiểm thử Vitest (105 tests), kiểm tra build Next.js.
    - Kiểm tra chất lượng Backend: Khởi động dịch vụ MongoDB Replica Set và Redis trong runner, chạy bộ kiểm thử đơn vị, kiểm thử Mockito, kiểm tra các quy tắc kiến trúc Hexagonal ArchUnit (`mvn test`), build gói ứng dụng.
  - **CD Deployment Workflow (`.github/workflows/deploy.yml`)**:
    - Tự động kích hoạt khi nhánh `main` được cập nhật sau khi vượt qua CI Gate.
    - Đóng gói container images và đẩy lên kho lưu trữ **GitHub Container Registry (GHCR)** với tag định danh commit git bất biến.
    - Kết nối SSH an toàn tới máy chủ VPS Production (`100.102.202.99:/opt/velstrong-book`).
    - Kéo image mới, chạy cập nhật không gián đoạn dịch vụ (Zero-downtime rolling update) và dọn dẹp image cũ.
- [x] **Quản Trị Mạng, Reverse Proxy & An Toàn Máy Chủ**:
  - Cấu hình Nginx Reverse Proxy làm cổng tiếp nhận duy nhất cho toàn bộ hệ thống.
  - Thiết lập chứng chỉ bảo mật SSL/TLS tự động qua Let's Encrypt (HTTPS cho các domain `books.velstrong.asia`, `sachnha.velstrong.asia`).
  - Cấu hình giới hạn tốc độ (Rate Limiting) trên Nginx chống tấn công DDoS/Brute-force vào các endpoint `/api/v1/auth/token`.
  - Cấu hình tường lửa UFW: Chỉ mở các cổng cần thiết (22 SSH, 80 HTTP, 443 HTTPS); cô lập các cổng cơ sở dữ liệu MongoDB (27017) và Redis (6379) chỉ cho phép truy cập nội bộ trong Docker Network.
  - Thiết lập kịch bản sao lưu tự động (Backup Cronjob): Hàng ngày chạy `mongodump` nén gzip toàn bộ database và lưu trữ vào thư mục sao lưu bảo mật có phân kỳ lưu trữ 30 ngày.

##### 👤 HƯNG & HIẾU (Phối hợp kiểm thử môi trường & Quản lý Cấu hình):
- [ ] Phối hợp kiểm thử toàn diện môi trường Local Development (`docker compose up -d`).
- [ ] Thiết lập và quản lý danh mục biến môi trường an toàn: `.env.example`, `.env.local` cho Frontend, `.env` cho Backend.
- [ ] Giám sát nhật ký lỗi ứng dụng thời gian thực thông qua `docker compose logs -f` trên môi trường Staging/Production để kịp thời phản hồi khi phát sinh sự cố.

---

#### 2.4. Trụ Cột 4: Tài Liệu Học Thuật, Sơ Đồ Kiến Trúc & Báo Cáo Thạc Sĩ

##### 👤 HƯNG (Chủ trì Báo Cáo Học Thuật, Đặc Tả Dữ Liệu & Sơ Đồ Kiến Trúc):
- [ ] **Soạn Thảo Báo Cáo Đề Tài / Luận Văn Thạc Sĩ (Học phần Cơ sở dữ liệu nâng cao)**:
  - **Chương 1: Tổng quan bài toán & Lý thuyết NoSQL**: Phân tích sự chuyển dịch từ RDBMS sang Document Database, ưu nhược điểm của mô hình dữ liệu linh hoạt so với mô hình quan hệ chuẩn hóa 3NF.
  - **Chương 2: Thiết kế Kiến trúc Dữ liệu MongoDB Dự Án**: Đặc tả chi tiết toàn bộ 34 collections của hệ thống, thiết kế cấu trúc Document, giải pháp phân tách dữ liệu chống mảng vô hạn (Unbounded Arrays), chiến lược đánh chỉ mục (Single-field, Compound Index tuân thủ quy tắc ESR, Sparse Index, Unique Index).
  - **Chương 3: Cơ chế Giao dịch & Kiểm soát Đồng thời**: Phân tích kỹ thuật Multi-document ACID Transactions trên Replica Set `rs0`, cơ chế Khóa Lạc Quan (`version`), cơ chế Khóa Bi Quan mô phỏng `SELECT FOR UPDATE` (`_mongoLock`), và giải pháp Sequence Pattern (`_mongo_sequences`).
  - **Chương 4: Hiện thực hóa & Đánh giá Hiệu năng**: Đo lường và so sánh hiệu năng truy vấn giữa MongoDB và PostgreSQL, phân tích các truy vấn tổng hợp phức tạp (Aggregation Pipeline vs SQL Group By / Join).
  - **Chương 5: Kết luận & Hướng phát triển**: Tổng kết các đóng góp học thuật và khả năng ứng dụng vào thực tiễn ngành phân phối sách.
- [ ] **Thiết Kế Sơ Đồ Kiến Trúc Động (Archify System Diagrams)**:
  - Áp dụng công cụ **Archify** thiết kế các sơ đồ kiến trúc động chuẩn quốc tế, xuất bản thành tệp HTML tương tác và hình ảnh minh họa chất lượng cao trong `docs/architecture/`:
    1. *Sơ đồ Kiến trúc Lục giác (Hexagonal Architecture Diagram)*: Phân tách rõ rệt Domain Core, Ports và Adapters.
    2. *Sơ đồ Luồng Đặt Hàng & Thanh Toán Đối Soát Tự Động (Order & Payment Reconciliation Lifecycle)*.
    3. *Sơ đồ Luồng Phân Bổ & Trả Sách Thuê (Rental Fulfillment & Return Sequence Diagram)*.
    4. *Sơ đồ Luồng Giao Dịch Phân Tán Đa Tài Liệu (Multi-document Transaction Flow)*.
- [ ] **Xây Dựng Slide Báo Cáo Thuyết Trình (Defense Presentation)**:
  - Thiết kế slide báo cáo bảo vệ đề tài (PowerPoint / PDF) theo phong cách hiện đại, chuyên nghiệp.
  - Làm nổi bật các luận điểm học thuật về CSDL nâng cao, biểu đồ kiến trúc và kết quả thực nghiệm.

##### 👤 MẠNH (Phản Biện Kỹ Thuật, Benchmark & Bảo Chứng Kiến Trúc):
- [ ] Rà soát, hiệu đính toàn bộ nội dung kỹ thuật trong báo cáo thạc sĩ: Đảm bảo độ chính xác tuyệt đối của các thuật ngữ chuyên ngành (WiredTiger, Oplog, Journaling, Checkpointing, Write Concern, Read Concern).
- [ ] Thực hiện bài đo kiểm chuẩn hiệu năng (Performance Benchmark): Đo đạc Throughput (req/sec) và Latency (ms) của MongoDB trong các kịch bản tải cao (Concurrency Test) bằng k6 / JMeter để đưa số liệu thực nghiệm vào luận văn.

##### 👤 HIẾU (Chuẩn Bị Tư Liệu Demo, UI Showcase & Video Trải Nghiệm):
- [ ] Chụp ảnh màn hình giao diện thực tế (Screenshots) chất lượng cao cho tất cả các phân hệ nghiệp vụ, chú thích chi tiết luồng thao tác để chèn vào báo cáo.
- [ ] Quay và dựng video clip kịch bản trải nghiệm người dùng (User Journey Demo Video - 5 đến 7 phút): Thể hiện trọn vẹn quy trình: Đặt sách $\rightarrow$ Thuê sách $\rightarrow$ Quét mã VietQR thanh toán $\rightarrow$ Tiền về tài khoản poller tự động khớp lệnh trong 15 giây $\rightarrow$ Thông báo đẩy tức thì $\rightarrow$ Nhân viên kho chuẩn bị sách.

---

### 3. BẢNG MA TRẬN PHÂN CÔNG TRÁCH NHIỆM (RACI MATRIX)

> **Quy ước chuẩn RACI**:
> - **R - Responsible (Người thực hiện chính)**: Thành viên trực tiếp bắt tay vào làm và hoàn thành công việc.
> - **A - Accountable (Người chịu trách nhiệm cao nhất)**: Người phê duyệt cuối cùng, chịu trách nhiệm về chất lượng và tiến độ.
> - **C - Consulted (Người được tham vấn ý kiến)**: Chuyên gia được hỏi ý kiến đóng góp chuyên môn trước và trong khi làm.
> - **I - Informed (Người được cập nhật thông tin)**: Thành viên được thông báo về tiến độ và kết quả công việc.

| Hạng Mục Công Việc Cụ Thể | MẠNH | HIẾU | HƯNG | Sản Phẩm Bàn Giao (Deliverables) |
|:---|:---:|:---:|:---:|:---|
| **1. KIẾN TRÚC & HẠ TẦNG BACKEND** | | | | |
| Thiết kế Kiến trúc Lục giác (Hexagonal) | **A / R** | I | C | `HexagonalArchitectureTest.java`, Domain Core |
| Cấu hình MongoDB Replica Set `rs0` & Drivers | **A / R** | I | C | `docker-compose.yml`, `MongoPersistenceConfig` |
| Triển khai Sequence Pattern (`_mongo_sequences`) | **A / R** | I | C | `MongoPersistenceSupport.nextId()` |
| Khóa Lạc quan (`saveVersioned`) & Bi quan (`_mongoLock`) | **A / R** | I | C | `saveVersioned()`, `findFirstAvailable...` |
| Cấu hình Multi-document ACID Transaction | **A / R** | I | C | `MongoTransactionManager`, `@Transactional` |
| Hệ thống Bảo mật: JWT, Redis Denylist, Session Version | **A / R** | C | I | `SecurityConfig`, `EndpointAuthorizationConfigurer` |
| **2. TÍCH HỢP THANH TOÁN & REAL-TIME** | | | | |
| Tích hợp cổng thanh toán VNPay | **A / R** | C | I | `VNPayPort`, `PaymentController`, Webhook IPN |
| Tích hợp VietQR động (BVBank - Timo) | **A / R** | C | I | `VietQrGenerator`, `CreateBankTransferPayment` |
| Xây dựng IMAP Poller quét email Timo tự động | **A / R** | I | C | `BankTransferPoller`, `processed_bank_messages` |
| Xây dựng SSE Hub & Web Push VAPID Service | **A / R** | C | I | `UserNotificationEventHub`, `WebPushAdapter` |
| **3. NGHIỆP VỤ PHÍA KHÁCH HÀNG (CUSTOMER USE CASES)** | | | | |
| Phân hệ Giỏ hàng đa hình (Mua & Thuê) | C | **A / R** | I | `CartController`, `CartService`, `cart_items` |
| Phân hệ Áp mã giảm giá Voucher (Quote/Reserve) | C | **A / R** | I | `VoucherQuoteService`, `voucher_usages` |
| Phân hệ Tạo đơn hàng & Hủy đơn khách hàng | C | **A / R** | I | `CreateOrderService`, `CancelOrderService` |
| Phân hệ Đánh giá sách & Thẩm định điều kiện | I | **A / R** | C | `BookReviewService`, `ReviewEligibilityService` |
| Phân hệ Sổ địa chỉ giao hàng (`user_addresses`) | I | **A / R** | I | `AddressController`, `UserAddressService` |
| Phân hệ Đăng ký gói hội viên đọc sách | C | **A / R** | I | `PurchaseSubscriptionService` |
| **4. NGHIỆP VỤ PHÍA QUẢN TRỊ & KHO (ADMIN USE CASES)** | | | | |
| Quản lý Kho & Bản sao sách vật lý (`BookCopy`) | C | I | **A / R** | `ManageBookCopiesService`, `BookCopyController` |
| Tiến trình phân bổ sách thuê (`RentalFulfillment`) | C | I | **A / R** | `RentalFulfillmentService`, `rentals` |
| Quy trình nhận trả sách thuê & Hoàn cọc (`Check-in`) | C | I | **A / R** | `ReturnRentalService`, `ForceReturnRental` |
| Máy trạng thái đơn hàng (`UpdateOrderStatus`) | C | I | **A / R** | `UpdateOrderStatusService`, `order_status_history` |
| Phân hệ Đối soát thủ công tiền treo ngân hàng | C | I | **A / R** | `ResolveUnmatchedTransferService` |
| Quản trị Voucher & Khuyến mãi hệ thống (CRUD) | I | I | **A / R** | `VoucherManagementService` |
| CMS Bài viết Blog & Đánh giá sách | I | I | **A / R** | `BlogPostService`, `blog_posts` |
| Aggregation Pipelines & Gợi ý sách bán chạy | C | I | **A / R** | `findTopSellingBooks`, Báo cáo doanh thu |
| **5. PHÁT TRIỂN GIAO DIỆN KHÁCH HÀNG (CUSTOMER UI)** | | | | |
| Next.js 15 App Shell, Layout & Design System | C | **A / R** | I | `site-shell.tsx`, `layout.tsx`, Tailwind Theme |
| Trang chủ, Tủ sách, Tìm kiếm & Bộ lọc nâng cao | I | **A / R** | I | `/sach`, `/`, Hero Carousel, Product Filters |
| Trang chi tiết sách (Mua/Thuê, Chọn hạn thuê) | I | **A / R** | I | `/sach/[id]`, Book detail pricing card |
| Giao diện Giỏ hàng & Quy trình Checkout | I | **A / R** | I | `/gio-hang`, `/checkout`, Cart Summary |
| Màn hình hiển thị mã VietQR động & Đồng hồ đếm | C | **A / R** | I | `/checkout/[orderId]`, QR Timer countdown |
| Cổng thông tin cá nhân, Đơn hàng & Sách thuê | I | **A / R** | I | `/account/*`, `/account/don-hang`, `/sach-thue` |
| Widget Chat hỗ trợ & Chuông thông báo in-app | C | **A / R** | I | Floating Support Widget, Notification Center |
| Tích hợp Web Push Service Worker | C | **A / R** | I | `notification-service-worker.js` |
| **6. PHÁT TRIỂN GIAO DIỆN QUẢN TRỊ (ADMIN UI)** | | | | |
| Cấu trúc Layout Admin Shell (`(admin)/admin/*`) | C | C | **A / R** | `admin-shell.tsx`, Admin Sidebar & Header |
| Giao diện Quản trị sách & Quản lý bản sao kho | I | I | **A / R** | `/admin/sach`, Book Copies Table & Modals |
| Giao diện Cập nhật tiến độ đơn hàng | I | I | **A / R** | `/admin/don-hang`, Order Status Stepper |
| Giao diện Quản lý thuê sách & Báo cáo quá hạn | I | I | **A / R** | `/admin/thue-sach`, Return Check-in Modal |
| Giao diện Đối soát ngân hàng tiền treo | C | I | **A / R** | `/admin/doi-soat`, Unmatched Transfer Table |
| Giao diện Soạn thảo bài viết Blog (Markdown) | I | I | **A / R** | `/admin/bai-viet`, Markdown Editor & Preview |
| Giao diện Phòng chat hỗ trợ của nhân viên | C | I | **A / R** | `/admin/ho-tro`, Staff Support Thread View |
| **7. DEVOPS, CI/CD & HẠ TẦNG TRIỂN KHAI** | | | | |
| Cấu hình Dockerfile tối ưu hóa Multi-stage build | **A / R** | C | C | `backend/Dockerfile`, `frontend/Dockerfile` |
| Xây dựng GitHub Actions CI Pipeline | **A / R** | C | C | `.github/workflows/ci.yml` |
| Xây dựng GitHub Actions CD Pipeline (GHCR $\rightarrow$ VPS)| **A / R** | I | I | `.github/workflows/deploy.yml` |
| Cấu hình Nginx, SSL Let's Encrypt & Firewall UFW | **A / R** | I | I | Nginx configs, VPS `100.102.202.99` |
| Thiết lập Kịch bản Tự động Sao lưu MongoDB | **A / R** | I | C | Backup script `mongodump` & Cronjob |
| Giám sát hệ thống & Nhật ký lỗi thời gian thực | **A** | R | R | Docker logs monitoring, Health checks |
| **8. TÀI LIỆU HỌC THUẬT & BÁO CÁO THẠC SĨ** | | | | |
| Soạn thảo Báo cáo Đề tài / Luận văn Thạc sĩ | C | C | **A / R** | Báo cáo hoàn chỉnh (Word / PDF) |
| Tài liệu Kiến trúc CSDL MongoDB (34 collections) | C | I | **A / R** | `docs/MONGODB_TOAN_TAP_CHO_KY_SU_SQL.md` |
| Thiết kế Sơ đồ Kiến trúc Động bằng Archify | C | I | **A / R** | `docs/architecture/*.html` & PNGs |
| Thực hiện Benchmark hiệu năng & Phân tích số liệu| **A / R** | I | C | Báo cáo đo kiểm tải cao (Load test results) |
| Thiết kế Slide Thuyết trình Bảo vệ Đề tài | C | C | **A / R** | Slide trình chiếu PowerPoint / PDF |
| Chụp ảnh màn hình & Quay Video Demo kịch bản | I | **A / R** | C | Video Demo Full Workflow & Screenshots |

---

### 4. LỘ TRÌNH TRIỂN KHAI 4 GIAI ĐOẠN (SPRINT ROADMAP & MILESTONES)

Kế hoạch làm việc được chia thành **4 Sprint (mỗi Sprint kéo dài 1 - 2 tuần)** đảm bảo dự án tiến triển đều đặn, có điểm kiểm soát chất lượng rõ ràng:

```text
  ┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
  │                                      TIẾN ĐỘ 4 GIAI ĐOẠN                                        │
  ├───────────────────┬───────────────────┬────────────────────────┬────────────────────────────────┤
  │     SPRINT 1      │     SPRINT 2      │        SPRINT 3        │            SPRINT 4            │
  │  Nền Tảng & Core  │ Nghiệp Vụ Mua Bán │  Admin, Đối Soát &     │  Kiểm Thử, Đo Tải,             │
  │  Dữ Liệu MongoDB  │ & Thuê Sách Cốt Lõi│  Thời Gian Thực        │  Hồ Sơ Luận Văn & Bảo Vệ       │
  └───────────────────┴───────────────────┴────────────────────────┴────────────────────────────────┘
```

#### 🚩 SPRINT 1: Nền Tảng Kiến Trúc, Mô Hình Dữ Liệu MongoDB & Khung Giao Diện
- **Mục tiêu**: Thiết lập toàn bộ hạ tầng cơ bản, cơ sở dữ liệu MongoDB Replica Set, bảo mật JWT và dựng khung giao diện chính.
- **Phân công cụ thể**:
  - **Mạnh**: Thiết lập Monorepo, cấu hình Spring Boot 4 + Hexagonal rules, dựng cụm MongoDB Replica Set `rs0` + Redis trên Docker, hiện thực hóa `MongoPersistenceSupport`, Sequence Pattern, Optimistic Locking, dựng khung bảo mật Spring Security + JWT.
  - **Hiếu**: Khởi tạo dự án Next.js 15 App Router, cấu hình Tailwind CSS, xây dựng Site Shell (Header, Footer), trang chủ với Hero Carousel và trang danh mục sách sơ bộ (`/sach`).
  - **Hưng**: Rà soát cấu trúc 34 collections MongoDB, thiết kế các Domain Model thuần POJO (`Book`, `User`, `Order`, `Rental`), viết bộ kiểm tra kiến trúc ArchUnit ban đầu và phác thảo đề cương Báo cáo Thạc sĩ.
- **Mốc bàn giao (Milestone 1)**: Đăng ký/đăng nhập thành công, token lưu vào cookie/header, MongoDB khởi tạo đầy đủ chỉ mục, Docker Compose chạy mượt mà trên môi trường cục bộ.

#### 🚩 SPRINT 2: Hoàn Thiện Nghiệp Vụ Mua Bán, Thuê Sách & Tích Hợp Thanh Toán
- **Mục tiêu**: Thông luồng hoàn chỉnh từ chọn sách, thêm giỏ, áp mã voucher, đặt hàng và thanh toán.
- **Phân công cụ thể**:
  - **Mạnh**: Tích hợp cổng thanh toán VNPay (IPN/Callback), tích hợp VietQR động, xây dựng IMAP Poller đọc email Timo, hiện thực hóa logic tự động khớp lệnh thanh toán trong một transaction.
  - **Hiếu**: Hoàn thiện trang chi tiết sách (`/sach/[id]`), giỏ hàng đa hình (`/gio-hang`), trang checkout (`/checkout`), trang hiển thị mã VietQR đếm ngược (`/checkout/[orderId]`), kết nối các API tạo đơn và áp mã voucher.
  - **Hưng**: Hiện thực hóa tầng quản lý bản sao sách `BookCopy`, cơ chế khóa bi quan chống tranh chấp khi thuê sách, quy trình hoàn tất thuê `RentalFulfillment`, viết các Unit Test kiểm thử logic tính tiền thuê và hoàn tiền cọc.
- **Mốc bàn giao (Milestone 2)**: Khách hàng có thể lên web chọn sách mua hoặc thuê, tạo đơn, quét mã VietQR chuyển khoản, poller quét email xác nhận trong 15s và đơn hàng tự động đổi trạng thái sang `PAID` và `CONFIRMED`.

#### 🚩 SPRINT 3: Cổng Quản Trị Admin, Thuê Sách Toàn Diện & Tính Năng Thời Gian Thực
- **Mục tiêu**: Hoàn thiện toàn bộ các tính năng dành cho Nhân viên/Admin và trải nghiệm thời gian thực.
- **Phân công cụ thể**:
  - **Mạnh**: Xây dựng Server-Sent Events (SSE) đẩy thông báo tức thì, cấu hình Web Push VAPID qua Service Worker, thiết lập CI/CD GitHub Actions build image đẩy lên GHCR và deploy tự động lên VPS `100.102.202.99`.
  - **Hiếu**: Hoàn thiện trang Quản lý tài khoản (`/account`), lịch sử đơn hàng, trang theo dõi sách đang thuê, tích hợp Widget Chat hỗ trợ phía khách hàng, nhận thông báo thời gian thực qua SSE và Web Push.
  - **Hưng**: Xây dựng toàn bộ giao diện Cổng Quản Trị (`(admin)/admin/*`): Quản lý sách & bản sao, cập nhật tiến độ đơn hàng, giao diện tiếp nhận trả sách (`Check-in`), giao diện xử lý tiền treo đối soát ngân hàng (`/admin/doi-soat`), CMS bài viết blog.
- **Mốc bàn giao (Milestone 3)**: Nhân viên quản trị có thể vận hành trọn vẹn trên Cổng Admin, xử lý nhận trả sách, hoàn cọc, khớp lệnh tiền treo, hệ thống triển khai thành công lên máy chủ VPS qua CI/CD tự động.

#### 🚩 SPRINT 4: Tối Ưu Hóa, Đo Kiểm Tải, Hoàn Thiện Hồ Sơ Luận Văn & Slide Báo Cáo
- **Mục tiêu**: Đảm bảo chất lượng toàn diện, đo kiểm benchmark và hoàn tất tài liệu bảo vệ đề tài Thạc sĩ.
- **Phân công cụ thể**:
  - **Mạnh**: Chạy kiểm thử tải cao (Load Testing) bằng k6 / JMeter, đo đạc thông số Throughput, Latency của MongoDB dưới tải lớn, tối ưu hóa các chỉ mục truy vấn chậm, rà soát cấu hình bảo mật máy chủ và kiểm tra kịch bản sao lưu tự động.
  - **Hiếu**: Rà soát và hoàn thiện giao diện người dùng, kiểm tra hiển thị trên nhiều kích thước màn hình, chụp ảnh toàn bộ màn hình chức năng, quay và dựng video demo kịch bản hoàn chỉnh (5 - 7 phút).
  - **Hưng**: Hoàn tất toàn bộ tài liệu Báo cáo Thạc sĩ học phần CSDL nâng cao, vẽ hoàn chỉnh các sơ đồ kiến trúc động bằng công cụ Archify, chuẩn bị bộ Slide thuyết trình bảo vệ đề tài (PowerPoint / PDF).
- **Mốc bàn giao (Milestone 4 - Final Deliverable)**: Hệ thống hoạt động ổn định 100% trên Production, bộ mã nguồn xanh trên GitHub Actions, video demo sắc nét, báo cáo đề tài Thạc sĩ và slide thuyết trình sẵn sàng bảo vệ.

---

### 5. QUY CHUẨN PHỐI HỢP KỸ THUẬT & QUẢN TRỊ CHẤT LƯỢNG MÃ NGUỒN

Để bảo đảm 3 thành viên làm việc ăn khớp, không gây xung đột mã nguồn (Merge Conflicts) và duy trì chất lượng kỹ thuật cao nhất, toàn đội bắt buộc tuân thủ các quy tắc sau:

#### 1. Quy chuẩn Git Flow & Quản lý Nhánh (Branching Model)
- **Nhánh chính (`main`)**: Nhánh sản phẩm chính thức, luôn ở trạng thái sẵn sàng triển khai (Production-ready). Không ai được phép push trực tiếp vào `main`.
- **Nhánh tính năng**: Đặt tên theo chuẩn:
  - `feat/<tên-tính-năng>` (ví dụ: `feat/vietqr-dynamic-display`, `feat/admin-rental-checkin`).
  - `fix/<tên-lỗi>` (ví dụ: `fix/token-refresh-expiry`).
  - `docs/<nội-dung>` (ví dụ: `docs/master-thesis-database-chapter`).
- **Quy tắc Commit**: Tuân thủ chuẩn **Conventional Commits**:
  - `feat: ...`, `fix: ...`, `refactor: ...`, `docs: ...`, `test: ...`, `ci: ...`.
  - Nguyên tắc: "One fix/feature, one commit" - commit rõ ràng, có ý nghĩa, không commit bừa bãi các nội dung thừa.

#### 2. Cổng Kiểm Soát Chất Lượng Pull Request (CI Gates)
Mọi Pull Request muốn được merge vào `main` bắt buộc phải vượt qua các bài kiểm tra tự động của `.github/workflows/ci.yml`:
1. **Frontend Gate**:
   - `npm run lint`: Không có bất kỳ cảnh báo ESLint nào.
   - `npm run typecheck`: TypeScript biên dịch không có lỗi (`tsc --noEmit`).
   - `npm run test:unit`: Vượt qua 100% bộ kiểm thử Vitest (105 tests).
   - `npm run build`: Build Next.js thành công.
2. **Backend Gate**:
   - `mvn test`: Vượt qua toàn bộ Unit Tests, Mockito Mocks và các bài kiểm tra kiến trúc ArchUnit.
   - Đảm bảo tính toàn vẹn của mô hình lục giác: `domain.*` không bị xâm phạm bởi các thư viện ngoài.
3. **Quy định Review**: Mọi Pull Request phải có ít nhất 1 thành viên review và phê duyệt (Approve) trước khi merge.
