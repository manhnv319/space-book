import Link from "next/link";
import { redirect } from "next/navigation";

import { CheckoutForm } from "@/components/checkout/checkout-form";
import { getCurrentUser } from "@/lib/bff/current-user";
import { formatVnd } from "@/lib/format/currency";
import { getCart } from "@/lib/services/cart-service";
import { getAddresses } from "@/lib/services/checkout-service";

export const metadata = { title: "Thanh toán" };

export default async function CheckoutPage() {
  const user = await getCurrentUser();
  if (!user) redirect("/login?next=%2Fcheckout");

  const [cart, addresses] = await Promise.all([getCart(), getAddresses()]);

  if (cart.items.length === 0) {
    return (
      <section className="checkout-page empty-state">
        <h1>Không có gì để thanh toán</h1>
        <p>Giỏ hàng của bạn đang trống.</p>
        <Link className="button" href="/sach">Tiếp tục chọn sách</Link>
      </section>
    );
  }

  if (addresses.length === 0) {
    return (
      <section className="checkout-page empty-state">
        <h1>Chưa có địa chỉ nhận hàng</h1>
        <p>Bạn cần thêm ít nhất một địa chỉ trước khi đặt đơn.</p>
        <Link className="button" href="/account">Thêm địa chỉ</Link>
      </section>
    );
  }

  return (
    <section className="checkout-page">
      <h1>Thanh toán</h1>

      <div className="checkout-layout">
        <CheckoutForm addresses={addresses} />

        <aside className="checkout-summary">
          <h2>Đơn hàng</h2>
          <ul className="checkout-items">
            {cart.items.map((item) => (
              <li key={item.id}>
                <span>{item.bookTitle ?? `Sách #${item.bookId}`}</span>
                <span>{item.itemType === "RENTAL" ? "Thuê" : `×${item.quantity ?? 1}`}</span>
              </li>
            ))}
          </ul>
          {/* Hai dòng riêng, không gộp: tổng cuối cùng do máy chủ chốt khi tạo đơn. */}
          <div className="receipt-row"><span>Tiền sách</span><strong>{formatVnd(cart.totalAmount)}</strong></div>
          <div className="receipt-row"><span>Tiền cọc thuê</span><strong>{formatVnd(cart.totalDeposit)}</strong></div>
          <p className="checkout-note">
            Số tiền chính xác phải chuyển sẽ hiển thị ở bước sau, sau khi máy chủ chốt đơn.
          </p>
          <p className="checkout-note">
            Hiện chỉ hỗ trợ <strong>chuyển khoản ngân hàng</strong>.
          </p>
        </aside>
      </div>
    </section>
  );
}
