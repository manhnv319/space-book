import Link from "next/link";

export default function NotFound() {
  return (
    <section className="page-section placeholder">
      <p className="eyebrow">404</p>
      <h1>Không tìm thấy trang.</h1>
      <p className="lead">Đường dẫn này chưa tồn tại trong nền tảng mới.</p>
      <Link className="text-link" href="/">Về trang chủ</Link>
    </section>
  );
}
