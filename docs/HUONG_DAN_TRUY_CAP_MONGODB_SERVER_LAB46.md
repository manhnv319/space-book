# HƯỚNG DẪN TRUY CẬP CONTAINER MONGODB TRÊN SERVER LAB46
## DỰ ÁN VELSTRONG BOOKSTORE (SPACE BOOK) — PRODUCTION & STAGING RUNTIME

---

### MỤC LỤC
1. [Thông Số Kết Nối Server Lab46](#1-thông-số-kết-nối-server-lab46)
2. [Cách 1: Truy Cập 1-Dòng Thẳng Từ Máy Mac (Khuyên Dùng)](#2-cách-1-truy-cập-1-dòng-thẳng-từ-máy-mac-khuyên-dùng)
3. [Cách 2: SSH Vào Server Lab46 Trước Rồi Vào Container](#3-cách-2-ssh-vào-server-lab46-trước-rồi-vào-container)
4. [Cách 3: Chạy Lệnh Nhanh Từ Xa Không Cần Mở Shell](#4-cách-3-chạy-lệnh-nhanh-từ-xa-không-cần-mở-shell)
5. [Cẩm Nang Sao Lưu (Backup) & Phục Hồi (Restore) Dữ Liệu Server Về Máy Cá Nhân](#5-cẩm-nang-sao-lưu-backup--phục-hồi-restore-dữ-liệu-server-về-máy-cá-nhân)
6. [Các Lệnh Giám Sát Container Trên Server Lab46](#6-các-lệnh-giám-sát-container-trên-server-lab46)

---

### 1. THÔNG SỐ KẾT NỐI SERVER LAB46

Hạ tầng máy chủ `lab46` phục vụ chạy ứng dụng Space Book (Velstrong Bookstore) đã được định cấu hình sẵn trong file `~/.ssh/config` trên máy Mac của bạn:

- **Tên Host cấu hình (Alias)**: `lab46`
- **Địa chỉ IP mạng nội bộ (LAN Phòng Lab)**: `10.100.200.126` (Port 22, User `root`)
- **Địa chỉ IP mạng ảo bảo mật (Tailscale VPN)**: `100.102.202.99` (Port 22, User `root`)
- **Thư mục ứng dụng trên máy chủ**: `/opt/space-book`
- **Tên Container MongoDB trên server**: `space-book-mongo`
- **Database vận hành**: `book_store` (Cụm Replica Set `rs0`)
- **Tên Container liên quan**:
  - `space-book-backend` (Spring Boot 4)
  - `space-book-frontend` (Next.js 15)
  - `space-book-redis` (Redis 7)

---

### 2. CÁCH 1: TRUY CẬP 1-DÒNG THẲNG TỪ MÁY MAC (KHUYÊN DÙNG)

Đây là phương thức thuận tiện và nhanh nhất. Bạn chỉ cần mở Terminal trên máy Mac cá nhân và chạy đúng một dòng lệnh duy nhất:

```bash
ssh -t lab46 "docker exec -it space-book-mongo mongosh book_store"
```

*Giải thích tham số:*
- `-t`: Cấp phát một pseudo-terminal (PTY) để bạn có thể gõ lệnh tương tác trong `mongosh` với màu sắc, auto-complete và phím mũi tên.
- `lab46`: Sử dụng SSH key có sẵn trong máy Mac để tự động đăng nhập không cần mật khẩu.
- `docker exec -it space-book-mongo mongosh book_store`: Chạy ngay trình điều khiển `mongosh` trỏ thẳng vào CSDL `book_store`.

> Khi bạn hoàn thành công việc và gõ `exit` (hoặc bấm `Ctrl + D`), phiên làm việc sẽ tự động đóng và đưa con trỏ chuột quay trở lại màn hình máy Mac của bạn.

---

### 3. CÁCH 2: SSH VÀO SERVER LAB46 TRƯỚC RỒI VÀO CONTAINER

Nếu bạn muốn kiểm tra trạng thái chung của server (dung lượng đĩa, RAM, logs hệ thống) trước khi truy cập database:

#### Bước 1: Kết nối SSH vào máy chủ `lab46`
- **Khi đang kết nối Wifi / Mạng LAN phòng Lab**:
  ```bash
  ssh lab46
  ```
- **Khi làm việc từ xa (Ở nhà / Quán cà phê qua Tailscale)**:
  Bật ứng dụng Tailscale trên máy Mac và gõ:
  ```bash
  ssh root@100.102.202.99
  ```

#### Bước 2: Truy cập vào container MongoDB
Khi dấu nhắc lệnh hiển thị `root@lab46:~#`:
```bash
# Mở trực tiếp mongosh vào database book_store:
docker exec -it space-book-mongo mongosh book_store

# Hoặc nếu muốn vào môi trường Linux bên trong container:
docker exec -it space-book-mongo bash
# sau đó gõ: mongosh book_store
```

---

### 4. CÁCH 3: CHẠY LỆNH NHANH TỪ XA KHÔNG CẦN MỞ SHELL

Trong trường hợp bạn chỉ muốn kiểm tra nhanh một chỉ số hoặc một bảng dữ liệu mà không muốn mở shell tương tác, hãy thực thi trực tiếp từ máy Mac:

#### 4.1. Kiểm tra trạng thái cụm Replica Set `rs0`
```bash
ssh lab46 "docker exec space-book-mongo mongosh book_store --quiet --eval 'rs.status().ok'"
# Trả về 1 tức là cụm Replica Set đang hoạt động bình thường
```

#### 4.2. Đếm tổng số lượng đơn hàng thực tế trên server
```bash
ssh lab46 "docker exec space-book-mongo mongosh book_store --quiet --eval 'db.orders.countDocuments()'"
```

#### 4.3. Xem 5 cuốn sách mới nhất đang kinh doanh trên server
```bash
ssh lab46 "docker exec space-book-mongo mongosh book_store --quiet --eval 'db.books.find({}, {title: 1, price: 1, _id: 0}).limit(5)'"
```

#### 4.4. Kiểm tra danh sách người dùng trên server
```bash
ssh lab46 "docker exec space-book-mongo mongosh book_store --quiet --eval 'db.users.find({}, {username: 1, email: 1, _id: 0})'"
```

#### 4.5. Xem danh sách các khoản tiền chuyển khoản chưa khớp (Unmatched transfers)
```bash
ssh lab46 "docker exec space-book-mongo mongosh book_store --quiet --eval 'db.unmatched_transfers.find()'"
```

---

### 5. CẨM NANG SAO LƯU (BACKUP) & PHỤC HỒI (RESTORE) DỮ LIỆU SERVER VỀ MÁY CÁ NHÂN

#### 5.1. Kéo toàn bộ Database thực tế từ Server Lab46 về Máy Mac (1 dòng lệnh)
Chạy lệnh sau ngay trên Terminal máy Mac của bạn:
```bash
ssh lab46 "docker exec space-book-mongo mongodump --db book_store --archive --gzip" > ~/Downloads/space_book_server_$(date +%Y%m%d_%H%M%S).archive.gz
```
*Cơ chế hoạt động:*
- Lệnh `mongodump` nén nhị phân toàn bộ dữ liệu, collection và chỉ mục thành 1 dòng stream gzip.
- Dòng dữ liệu truyền thẳng qua đường ống SSH và ghi thành tệp `.archive.gz` trong thư mục `Downloads` của máy Mac.

#### 5.2. Khôi phục dữ liệu vừa tải về vào Container MongoDB Local của máy Mac
Nếu bạn muốn dùng dữ liệu thực tế từ server để kiểm thử trên máy local:
```bash
docker exec -i book-store-mongo mongorestore --archive --gzip --drop < ~/Downloads/space_book_server_*.archive.gz
```
*Tham số `--drop`: Tự động xóa sạch dữ liệu cũ trong database trước khi nạp dữ liệu mới từ bản sao lưu.*

---

### 6. CÁC LỆNH GIÁM SÁT CONTAINER TRÊN SERVER LAB46

Khi đã SSH vào `lab46`, bạn có thể dùng các lệnh sau để quản lý toàn bộ hệ thống Space Book:

```bash
# Xem danh sách các container và tài nguyên tiêu thụ:
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Xem mức sử dụng CPU và RAM thời gian thực của container MongoDB:
docker stats space-book-mongo --no-stream

# Xem log hoạt động của MongoDB (phát hiện lỗi kết nối hoặc transaction):
docker logs --tail 50 -f space-book-mongo

# Khởi động lại container MongoDB nếu cần:
docker restart space-book-mongo
```
