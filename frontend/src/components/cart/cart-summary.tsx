import Link from "next/link";

import { formatVnd } from "@/lib/format/currency";
import type { CartResponse } from "@/lib/types/cart";

/**
 * Receipt-style summary. Every figure is a value BE already computed
 * (`totalAmount`, `totalDeposit`) — deliberately NOT adding them together
 * into one "grand total" here (FE cấm tính giá, kể cả cộng 2 tổng của BE).
 * The final amount due is confirmed at checkout.
 */
export function CartSummary({ cart }: { cart: CartResponse }) {
  return (
    <aside className="cart-summary" aria-label="Tóm tắt giỏ hàng">
      <h2>Tóm tắt</h2>
      <div className="receipt-row">
        <span>Số lượng sản phẩm</span>
        <strong>{cart.totalItems}</strong>
      </div>
      <div className="receipt-row">
        <span>Tạm tính</span>
        <strong>{formatVnd(cart.totalAmount)}</strong>
      </div>
      <div className="receipt-row receipt-row-deposit">
        <span>Tiền đặt cọc (hoàn lại khi trả sách)</span>
        <strong>{formatVnd(cart.totalDeposit)}</strong>
      </div>
      <p className="cart-summary-note">
        Số tiền thanh toán thực tế (gồm cả tiền cọc nếu có) được máy chủ tính chính xác ở bước thanh toán.
      </p>
      <Link className="button button-full" href="/checkout">
        Tiến hành thanh toán
      </Link>
    </aside>
  );
}
