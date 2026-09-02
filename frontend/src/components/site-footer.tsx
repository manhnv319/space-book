import Link from "next/link";

const YEAR = new Date().getFullYear();

export function SiteFooter() {
  return <footer className="site-footer"><div className="site-footer-grid"><div><strong className="footer-brand">VelstrongBook</strong><p>Một không gian để chọn sách, mua sở hữu hoặc thuê theo nhịp đọc của bạn.</p></div><div><h2>Mua và thuê</h2><Link href="/sach">Tất cả sách</Link><Link href="/goi-thue">Gói thuê</Link><Link href="/gio-hang">Giỏ hàng</Link></div><div><h2>Hỗ trợ</h2><Link href="/#support">Nhắn với nhà sách</Link><Link href="/account">Tài khoản</Link><Link href="/bai-viet">Gợi ý đọc</Link></div><div><h2>Liên hệ</h2><p>08:00 – 22:00 mỗi ngày</p><a href="mailto:support@velstrongbook.asia">support@velstrongbook.asia</a></div></div><div className="site-footer-bottom"><span>© {YEAR} VelstrongBook</span><span>Đọc kỹ. Chọn đúng.</span></div></footer>;
}
