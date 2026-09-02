import Link from "next/link";

import { ReorderButton } from "@/components/account/reorder-button";
import { BookCover } from "@/components/ui/book-cover";
import { formatVnd } from "@/lib/format/currency";
import { statusLabel } from "@/lib/orders/status";
import type { OrderStatus, OrderSummary } from "@/lib/types/checkout";

const DATE = new Intl.DateTimeFormat("vi-VN", {
  day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit",
});

function formatMoment(value: string): string {
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? "—" : DATE.format(parsed);
}

/** Trạng thái cần khách để ý thì tô màu; còn lại giữ trung tính. */
function statusTone(status: OrderStatus): string {
  if (status === "PENDING") return "is-warning";
  if (status === "CANCELLED" || status === "REFUNDED") return "is-muted";
  if (status === "COMPLETED") return "is-done";
  return "is-active";
}

/**
 * Server component: một đơn trong danh sách.
 *
 * Bố cục theo lối "Đơn mua" của Shopee: trạng thái ở đầu thẻ, rồi tới sách —
 * bìa và tên là thứ khách nhận ra đơn của mình. Mã đơn là ULID nên bị đẩy xuống
 * dòng phụ, nó chỉ hữu ích khi cần đối chiếu với nhà sách.
 */
export function OrderCard({ order }: { order: OrderSummary }) {
  const status = order.status as OrderStatus;
  const items = order.items ?? [];
  const hidden = (order.totalItems ?? 0) - items.length;
  const unpaid = status === "PENDING";

  return (
    <li className="order-card">
      <div className="order-card-head">
        <span className="order-card-date">{formatMoment(order.createdAt)}</span>
        <span className={`order-status ${statusTone(status)}`}>{statusLabel(status)}</span>
      </div>

      <Link href={`/account/don-hang/${order.id}`} className="order-card-items">
        {items.length === 0 ? (
          <p className="order-card-noitems">Đơn không còn sản phẩm nào tra được.</p>
        ) : (
          items.map((item, index) => (
            <div key={`${item.bookId}-${index}`} className="order-line">
              <span className="order-line-cover">
                <BookCover src={item.imageUrl} alt="" variant="card" className="order-line-image" />
              </span>
              <span className="order-line-text">
                <strong>{item.title ?? "Sách không còn trong hệ thống"}</strong>
                <span className="order-line-meta">
                  {item.itemType === "RENTAL" ? "Thuê" : `×${item.quantity ?? 1}`}
                </span>
              </span>
            </div>
          ))
        )}
        {hidden > 0 && <p className="order-card-more">và {hidden} sản phẩm khác</p>}
      </Link>

      <div className="order-card-foot">
        <span className="order-card-code">{order.orderCode}</span>
        <span className="order-card-total">
          Thành tiền <strong>{formatVnd(order.finalAmount)}</strong>
        </span>
      </div>

      <div className="order-card-actions">
        {unpaid && <Link className="button button-small" href={`/checkout/${order.id}`}>Tiếp tục thanh toán</Link>}
        {!unpaid && <ReorderButton orderId={order.id} />}
        <Link className="button button-secondary button-small" href={`/account/don-hang/${order.id}`}>
          Xem chi tiết
        </Link>
      </div>
    </li>
  );
}
