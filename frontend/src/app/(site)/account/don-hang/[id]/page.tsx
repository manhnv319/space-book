import Link from "next/link";
import { notFound, redirect } from "next/navigation";

import { OrderRoute } from "@/components/account/order-route";
import { getCurrentUser } from "@/lib/bff/current-user";
import { formatVnd } from "@/lib/format/currency";
import { statusLabel } from "@/lib/orders/status";
import { getOrderDetail } from "@/lib/services/checkout-service";

export const metadata = { title: "Chi tiết đơn hàng" };

export default async function OrderDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  if (!/^\d+$/.test(id)) notFound();

  const user = await getCurrentUser();
  if (!user) redirect(`/login?next=%2Faccount%2Fdon-hang%2F${id}`);

  // Backend chỉ trả đơn của chính người gọi, nên đoán id chỉ nhận được lỗi.
  const order = await getOrderDetail(Number(id)).catch(() => null);
  if (!order) notFound();

  const unpaid = order.paymentStatus !== "PAID" && order.status !== "CANCELLED";

  return (
    <section className="account-page">
      <p className="eyebrow">Đơn {order.orderCode}</p>
      <h1>{statusLabel(order.status as never)}</h1>

      <div className="order-detail-layout">
        <div>
          <div className="account-section">
            <h2>Lộ trình giao hàng</h2>
            <OrderRoute status={order.status as never} timeline={order.timeline ?? []} />
          </div>

          <div className="account-section">
            <h2>Sản phẩm</h2>
            <ul className="order-items">
              {order.items.map((item, index) => (
                <li key={`${item.bookId}-${index}`}>
                  <Link href={`/sach/${item.bookId}`}>Sách #{item.bookId}</Link>
                  <span>
                    {item.itemType === "RENTAL"
                      ? `Thuê ${item.rentalTermValue ?? ""} ${item.rentalTermUnit ?? ""}`.trim()
                      : `×${item.quantity ?? 1}`}
                  </span>
                  <span>{formatVnd(item.subtotal)}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>

        <aside className="checkout-summary">
          <h2>Thanh toán</h2>
          {/* Từng dòng riêng, tổng cuối do máy chủ tính. */}
          <div className="receipt-row"><span>Tiền sách</span><strong>{formatVnd(order.totalAmount)}</strong></div>
          <div className="receipt-row"><span>Tiền cọc</span><strong>{formatVnd(order.totalDeposit)}</strong></div>
          {order.totalDiscount > 0 && (
            <div className="receipt-row"><span>Giảm giá</span><strong>-{formatVnd(order.totalDiscount)}</strong></div>
          )}
          <div className="receipt-row"><span>Tổng</span><strong>{formatVnd(order.finalAmount)}</strong></div>
          {unpaid && (
            <Link className="button" href={`/checkout/${order.id}`}>Tiếp tục thanh toán</Link>
          )}
          <Link className="text-link" href="/account/don-hang">&larr; Tất cả đơn hàng</Link>
        </aside>
      </div>
    </section>
  );
}
