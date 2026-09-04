# CẨM NANG TRUY CẬP CONTAINER MONGODB & THỰC THI CÁC LỆNH CƠ BẢN
## DỰ ÁN VELSTRONG BOOKSTORE (SPACE BOOK) — MÔ HÌNH REPLICA SET RS0

---

### MỤC LỤC
1. [Cách Kết Nối Trực Tiếp Vào Container MongoDB](#1-cách-kết-nối-trực-tiếp-vào-container-mongodb)
2. [Các Lệnh Điều Hướng & Kiểm Tra Trạng Thái Cụm](#2-các-lệnh-điều-hướng--kiểm-tra-trạng-thái-cụm)
3. [Cú Pháp Truy Vấn Dữ Liệu (Đối Chiếu SQL vs MongoDB)](#3-cú-pháp-truy-vấn-dữ-liệu-đối-chiếu-sql-vs-mongodb)
   - 3.1. Xem Cấu Trúc Document Mẫu (`findOne`)
   - 3.2. Lọc Dữ Liệu Bằng Điều Kiện Bằng (`=`)
   - 3.3. Phép Chiếu Thuộc Tính — Projection (Chọn Cột Hiển Thị)
   - 3.4. So Sánh Số Học & Khoảng Giá Trị (`>`, `<`, `>=`, `<=`, `IN`)
   - 3.5. Đếm Số Lượng, Sắp Xếp (Sort) và Phân Trang (Pagination)
4. [Các Lệnh Thao Tác Dữ Liệu: Thêm, Sửa, Xóa (CUD)](#4-các-lệnh-thao-tác-dữ-liệu-thêm-sửa-xóa-cud)
   - 4.1. Thêm Mới Document (`insertOne`, `insertMany`)
   - 4.2. Cập Nhật Document (`updateOne`, `updateMany`) & Cảnh Báo Toán Tử `$set`
   - 4.3. Xóa Document (`deleteOne`, `deleteMany`)
5. [Các Lệnh Nâng Cao Đặc Thù Của Dự Án Velstrong Bookstore](#5-các-lệnh-nâng-cao-đặc-thù-của-dự-án-velstrong-bookstore)
   - 5.1. Xem Bộ Đếm ID Tự Tăng Của Hệ Thống (`_mongo_sequences`)
   - 5.2. Kiểm Tra Chỉ Mục (Indexes) & Chỉ Mục ESR
   - 5.3. Phân Tích Kế Hoạch Thực Thi Truy Vấn Với `explain("executionStats")`
   - 5.4. Đọc Nhật Ký Biến Động Oplog Của Cụm Replica Set `rs0`
6. [Mẹo Thực Thi Lệnh Nhanh Từ Máy Host Không Cần Vào Shell](#6-mẹo-thực-thi-lệnh-nhanh-từ-máy-host-không-cần-vào-shell)

---

### 1. CÁCH KẾT NỐI TRỰC TIẾP VÀO CONTAINER MONGODB

Trước khi thực hiện, đảm bảo container MongoDB đang chạy thông qua Docker Compose:
```bash
docker compose up -d
```

#### Cách 1: Vào thẳng giao diện dòng lệnh `mongosh` (Khuyên dùng)
Chạy lệnh sau tại Terminal của máy host để mở trực tiếp shell tương tác của database `book_store`:
```bash
docker exec -it book-store-mongo mongosh book_store
```
*Lúc này dấu nhắc lệnh sẽ chuyển thành: `book_store> `.*

#### Cách 2: Truy cập vào môi trường Linux của Container rồi mở `mongosh`
```bash
docker exec -it book-store-mongo bash
# Khi đã ở trong terminal container:
mongosh book_store
```

*Để thoát khỏi `mongosh`, bạn gõ `exit` hoặc bấm tổ hợp phím `Ctrl + D`.*

---

### 2. CÁC LỆNH ĐIỀU HƯỚNG & KIỂM TRA TRẠNG THÁI CỤM

| Thao Tác Nghiệp Vụ | Câu Lệnh MongoDB (`mongosh`) | Câu Lệnh Tương Đương Trong SQL |
|:---|:---|:---|
| **Xem danh sách database** | `show dbs` | `SHOW DATABASES;` (MySQL) / `\l` (Postgres) |
| **Chuyển sang database khác** | `use book_store` | `USE book_store;` / `\c book_store` |
| **Xem database hiện tại** | `db` | `SELECT current_database();` |
| **Xem danh sách Collection** | `show collections` | `SHOW TABLES;` / `\dt` |
| **Kiểm tra trạng thái cụm Replica Set** | `rs.status()` | Kiểm tra trạng thái Cluster / Replication |
| **Kiểm tra quyền ghi của Node Primary** | `db.hello().isWritablePrimary` | Kiểm tra node hiện tại có phải Primary không |

---

### 3. CÚ PHÁP TRUY VẤN DỮ LIỆU (ĐỐI CHIẾU SQL VS MONGODB)

Trong MongoDB, cú pháp truy vấn tuân theo cấu trúc hàm hướng đối tượng:  
$$\text{db.<\textbf{collection}>.find(<\textbf{điều\_kiện\_lọc}>, <\textbf{phép\_chiếu\_cột}>)}$$

#### 3.1. Xem Cấu Trúc Document Mẫu (`findOne`)
```javascript
// Tương đương: SELECT * FROM books LIMIT 1;
db.books.findOne()
```

#### 3.2. Lọc Dữ Liệu Bằng Điều Kiện Bằng (`=`)
```javascript
// Tương đương: SELECT * FROM books WHERE is_active = true;
db.books.find({ isActive: true })

// Tương đương: SELECT * FROM users WHERE username = "manh";
db.users.find({ username: "manh" })
```

#### 3.3. Phép Chiếu Thuộc Tính — Projection (Chọn Cột Hiển Thị)
Trong tham số thứ hai của hàm `find()`, truyền `1` để lấy trường và `0` để ẩn trường:
```javascript
// Tương đương: SELECT title, price FROM books WHERE is_active = true;
// Lưu ý: Trường _id mặc định luôn hiển thị trừ khi khai báo _id: 0
db.books.find(
  { isActive: true }, 
  { title: 1, price: 1, _id: 0 }
)
```

#### 3.4. So Sánh Số Học & Khoảng Giá Trị (`>`, `<`, `>=`, `<=`, `IN`)
MongoDB sử dụng các toán tử so sánh bắt đầu bằng ký tự `$`:
- `$gt` (Greater Than $>$): Lớn hơn.
- `$gte` (Greater Than or Equal $\ge$): Lớn hơn hoặc bằng.
- `$lt` (Less Than $<$): Nhỏ hơn.
- `$lte` (Less Than or Equal $\le$): Nhỏ hơn hoặc bằng.
- `$ne` (Not Equal $\ne$): Khác.
- `$in`: Nằm trong danh sách mảng giá trị (`IN (...)`).
- `$nin`: Không nằm trong danh sách mảng (`NOT IN (...)`).

```javascript
// Tương đương: SELECT * FROM books WHERE price >= 100000 AND price <= 250000;
db.books.find({ 
  price: { $gte: 100000, $lte: 250000 } 
})

// Tương đương: SELECT * FROM orders WHERE status IN ('CONFIRMED', 'SHIPPING');
db.orders.find({ 
  status: { $in: ["CONFIRMED", "SHIPPING"] } 
})
```

#### 3.5. Đếm Số Lượng, Sắp Xếp (Sort) và Phân Trang (Pagination)
```javascript
// Tương đương: SELECT COUNT(*) FROM orders WHERE status = 'COMPLETED';
db.orders.countDocuments({ status: "COMPLETED" })

// Tương đương: SELECT * FROM orders ORDER BY created_at DESC LIMIT 5 OFFSET 10;
db.orders.find()
  .sort({ createdAt: -1 })   // -1 là sắp xếp giảm dần (DESC), 1 là tăng dần (ASC)
  .skip(10)                  // Bỏ qua 10 bản ghi đầu tiên (OFFSET 10)
  .limit(5)                  // Lấy tối đa 5 bản ghi (LIMIT 5)
```

---

### 4. CÁC LỆNH THAO TÁC DỮ LIỆU: THÊM, SỬA, XÓA (CUD)

#### 4.1. Thêm Mới Document (`insertOne`, `insertMany`)
```javascript
// Tương đương: INSERT INTO categories (id, name, slug, description) 
// VALUES (99, 'Kinh Tế Học', 'kinh-te-hoc', 'Sách tài chính và kinh doanh');
db.categories.insertOne({
  _id: NumberLong(99),
  name: "Kinh Tế Học",
  slug: "kinh-te-hoc",
  description: "Sách tài chính và kinh doanh"
})
```

#### 4.2. Cập Nhật Document (`updateOne`, `updateMany`) & Cảnh Báo Toán Tử `$set`
> ⚠️ **CẢNH BÁO BẢO TOÀN DỮ LIỆU DÀNH CHO KỸ SƯ SQL**:
> Trong MongoDB, khi cập nhật một trường, bạn **bắt buộc phải đặt trường đó bên trong toán tử `$set`**.
> Nếu bạn viết: `db.users.updateOne({ username: "manh" }, { fullName: "Nguyễn Văn Mạnh" })` (quên `$set`), MongoDB sẽ hiểu rằng bạn muốn **thay thế toàn bộ Document bằng một đối tượng mới chỉ có mỗi `fullName`**, dẫn đến **xóa mất toàn bộ mật khẩu, email, số điện thoại** của người dùng đó!

```javascript
// Tương đương: UPDATE users SET full_name = 'Nguyễn Văn Mạnh', email_verified = true WHERE username = 'manh';
db.users.updateOne(
  { username: "manh" },
  { $set: { fullName: "Nguyễn Văn Mạnh", emailVerified: true } }
)

// Tăng giá trị nguyên tử (Atomic Increment)
// Tương đương: UPDATE books SET price = price + 10000 WHERE id = 10;
db.books.updateOne(
  { _id: NumberLong(10) },
  { $inc: { price: 10000 } }
)
```

#### 4.3. Xóa Document (`deleteOne`, `deleteMany`)
```javascript
// Tương đương: DELETE FROM unmatched_transfers WHERE id = 12;
db.unmatched_transfers.deleteOne({ _id: NumberLong(12) })

// Tương đương: DELETE FROM cart_items WHERE cart_id = 55;
db.cart_items.deleteMany({ cartId: NumberLong(55) })
```

---

### 5. CÁC LỆNH NÂNG CAO ĐẶC THÙ CỦA DỰ ÁN VELSTRONG BOOKSTORE

#### 5.1. Xem Bộ Đếm ID Tự Tăng Của Hệ Thống (`_mongo_sequences`)
Dự án sử dụng cơ chế Sequence Pattern để cấp phát khóa chính số nguyên tuần tự (`Long`) thay cho `ObjectId`:
```javascript
// Xem toàn bộ giá trị bộ đếm hiện tại của các bảng
db._mongo_sequences.find()

// Xem giá trị ID cao nhất hiện tại của bảng đơn hàng
db._mongo_sequences.findOne({ _id: "orders" })
```
*Kết quả mẫu:*
```json
{ "_id": "orders", "value": 78 }
```

#### 5.2. Kiểm Tra Chỉ Mục (Indexes) & Chỉ Mục ESR
Để xem các chỉ mục đã được khởi tạo tự động bởi `MongoSchemaInitializer`:
```javascript
// Xem toàn bộ danh sách chỉ mục trên bảng sách
db.books.getIndexes()

// Xem chỉ mục ESR phục vụ tra cứu sách quá hạn trên bảng rentals
db.rentals.getIndexes()
```
*Bạn sẽ nhìn thấy chỉ mục:*
```json
{
  "v": 2,
  "key": { "status": 1, "plannedReturnDate": 1 },
  "name": "idx_rentals_status_plannedReturnDate"
}
```

#### 5.3. Phân Tích Kế Hoạch Thực Thi Truy Vấn Với `explain("executionStats")`
Tương đương lệnh `EXPLAIN (ANALYZE, BUFFERS)` trong PostgreSQL hoặc `EXPLAIN FORMAT=JSON` trong MySQL:
```javascript
db.rentals.find({ 
  status: "RENTING", 
  plannedReturnDate: { $lt: new Date() } 
}).explain("executionStats")
```
*Các chỉ số cần quan sát trong kết quả:*
- **`winningPlan.inputStage.stage`**:
  - `IXSCAN` (Index Scan): **Tốt nhất**. Truy vấn sử dụng trực tiếp cây B-Tree.
  - `COLLSCAN` (Collection Scan): **Cảnh báo**. Truy vấn quét toàn bộ bảng (tốn CPU và I/O).
- **`totalDocsExamined`**: Số lượng document thực tế phải đọc từ đĩa.
- **`nReturned`**: Số lượng bản ghi kết quả trả về.
- *Nếu `totalDocsExamined == nReturned` $\rightarrow$ Chỉ mục đạt hiệu năng hoàn hảo.*

#### 5.4. Đọc Nhật Ký Biến Động Oplog Của Cụm Replica Set `rs0`
Vì MongoDB đang chạy dưới dạng Replica Set `rs0`, mọi giao dịch thay đổi dữ liệu đều được lưu lại tuần tự trong collection `local.oplog.rs`:
```javascript
// Chuyển sang database hệ thống "local"
use local

// Đọc 3 giao dịch thay đổi dữ liệu mới nhất trên cụm
db.oplog.rs.find().sort({ $natural: -1 }).limit(3)
```

---

### 6. MẸO THỰC THI LỆNH NHANH TỪ MÁY HOST KHÔNG CẦN VÀO SHELL

Nếu bạn đang phát triển ứng dụng và chỉ muốn kiểm tra nhanh trạng thái dữ liệu trong terminal máy tính mà không muốn gõ `mongosh` rồi `exit`:

```bash
# 1. Đếm tổng số đầu sách đang có trong CSDL:
docker exec -it book-store-mongo mongosh book_store --quiet --eval "db.books.countDocuments()"

# 2. Liệt kê danh sách username của người dùng trong hệ thống:
docker exec -it book-store-mongo mongosh book_store --quiet --eval "db.users.find({}, {username: 1, email: 1, _id: 0})"

# 3. Kiểm tra số lượng giao dịch ngân hàng chưa khớp lệnh (unmatched):
docker exec -it book-store-mongo mongosh book_store --quiet --eval "db.unmatched_transfers.countDocuments()"

# 4. Gán quyền ADMIN (roleId = 4) cho user "manh":
docker exec -it book-store-mongo mongosh book_store --quiet --eval '
  const u = db.users.findOne({ username: "manh" });
  if (u) {
    db.user_roles.replaceOne({ userId: u._id, roleId: NumberLong(4) }, { _id: u._id + ":4", userId: u._id, roleId: NumberLong(4) }, { upsert: true });
    print("OK: User " + u.username + " da co quyen ADMIN");
  }
'
```
