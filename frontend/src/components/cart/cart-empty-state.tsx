import Link from "next/link";

export function CartEmptyState() {
  return (
    <section className="page-section cart-empty" aria-labelledby="cart-empty-title">
      <p className="eyebrow">Giỏ hàng</p>
      <h1 id="cart-empty-title">Giỏ hàng của bạn đang trống</h1>
      <p className="lead">Khám phá thêm sách để mua hoặc thuê.</p>
      <Link className="button" href="/sach">
        Xem danh sách sách
      </Link>
    </section>
  );
}
