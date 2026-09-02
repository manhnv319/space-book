import { buildRoute, isOnDeliveryRoute, statusLabel } from "@/lib/orders/status";
import type { OrderStatus, OrderStatusStep } from "@/lib/types/checkout";

const TIME = new Intl.DateTimeFormat("vi-VN", {
  day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit",
});

function formatMoment(value: string | null): string | null {
  if (!value) return null;
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? null : TIME.format(parsed);
}

/**
 * Server component: lộ trình giao hàng của một đơn.
 *
 * Vẽ đủ bốn chặng để khách thấy cả đoạn phía trước, nhưng **chỉ hiện giờ ở chặng
 * thực sự có trong lịch sử**. Đơn tạo trước khi có bảng lịch sử vẫn hiện đúng vị
 * trí mà không bịa ra thời điểm.
 */
export function OrderRoute({ status, timeline }: { status: OrderStatus; timeline: OrderStatusStep[] }) {
  if (!isOnDeliveryRoute(status)) {
    return (
      <p className="order-route-halted">
        Đơn ở trạng thái <strong>{statusLabel(status)}</strong> nên không còn lộ trình giao hàng.
      </p>
    );
  }

  const route = buildRoute(status, timeline);

  return (
    <ol className="order-route">
      {route.map((step) => {
        const moment = formatMoment(step.reachedAt);
        const className = ["order-route-step", step.reached && "is-reached", step.current && "is-current"]
          .filter(Boolean).join(" ");
        return (
          <li key={step.status} className={className}>
            <span className="order-route-dot" aria-hidden="true" />
            <div>
              <p className="order-route-label">{step.label}</p>
              {moment ? (
                <time dateTime={step.reachedAt ?? undefined}>{moment}</time>
              ) : (
                <span className="order-route-pending">{step.reached ? "—" : "Chưa tới"}</span>
              )}
            </div>
          </li>
        );
      })}
    </ol>
  );
}
