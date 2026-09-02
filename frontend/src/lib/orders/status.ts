import type { OrderStatus, OrderStatusStep } from "@/lib/types/checkout";

/** Lộ trình giao hàng, theo đúng thứ tự khách nhìn thấy. */
export const DELIVERY_ROUTE: OrderStatus[] = ["CONFIRMED", "PROCESSING", "SHIPPING", "COMPLETED"];

const LABELS: Record<OrderStatus, string> = {
  PENDING: "Chờ thanh toán",
  CONFIRMED: "Đã xác nhận",
  PROCESSING: "Đã giao cho đơn vị vận chuyển",
  SHIPPING: "Đang vận chuyển",
  COMPLETED: "Đã nhận hàng",
  CANCELLED: "Đã huỷ",
  REFUNDED: "Đã hoàn tiền",
};

export function statusLabel(status: OrderStatus): string {
  return LABELS[status] ?? status;
}

export interface RouteStep {
  status: OrderStatus;
  label: string;
  /** Thời điểm thật, hoặc null nếu chặng chưa tới (hoặc đơn cũ không có lịch sử). */
  reachedAt: string | null;
  reached: boolean;
  current: boolean;
}

/**
 * Ghép lộ trình cố định với lịch sử thật.
 *
 * Trả về đủ 4 chặng để khách thấy được cả đường đi phía trước, nhưng **chỉ gắn
 * mốc giờ cho chặng thật sự có trong lịch sử** — đơn tạo trước khi có bảng lịch
 * sử sẽ hiện đúng trạng thái mà không bịa ra thời điểm.
 *
 * Thuần tuý, không I/O.
 */
export function buildRoute(status: OrderStatus, timeline: OrderStatusStep[]): RouteStep[] {
  const reachedAt = new Map<OrderStatus, string>();
  for (const step of timeline) {
    // Giữ mốc sớm nhất: trạng thái có thể được ghi lại nhiều lần.
    if (!reachedAt.has(step.status)) reachedAt.set(step.status, step.changedAt);
  }

  const currentIndex = DELIVERY_ROUTE.indexOf(status);

  return DELIVERY_ROUTE.map((step, index) => ({
    status: step,
    label: statusLabel(step),
    reachedAt: reachedAt.get(step) ?? null,
    // Suy ra từ trạng thái hiện tại chứ không chỉ từ lịch sử, để đơn cũ vẫn
    // hiển thị đúng vị trí dù không có bản ghi nào.
    reached: currentIndex >= 0 && index <= currentIndex,
    current: index === currentIndex,
  }));
}

/** Đơn đã huỷ hoặc hoàn tiền thì không còn lộ trình giao hàng để vẽ. */
export function isOnDeliveryRoute(status: OrderStatus): boolean {
  return DELIVERY_ROUTE.includes(status);
}

/**
 * Các nhóm trạng thái cho thanh lọc, theo lối "Đơn mua" của Shopee.
 *
 * Một tab gom nhiều trạng thái vì khách nghĩ theo "hàng của tôi đang ở đâu",
 * không nghĩ theo tên trạng thái trong cơ sở dữ liệu.
 */
export interface OrderTab {
  key: string;
  label: string;
  /** Rỗng nghĩa là tất cả. */
  statuses: OrderStatus[];
}

export const ORDER_TABS: OrderTab[] = [
  { key: "all", label: "Tất cả", statuses: [] },
  { key: "unpaid", label: "Chờ thanh toán", statuses: ["PENDING"] },
  { key: "preparing", label: "Đang chuẩn bị", statuses: ["CONFIRMED", "PROCESSING"] },
  { key: "shipping", label: "Đang giao", statuses: ["SHIPPING"] },
  { key: "done", label: "Hoàn thành", statuses: ["COMPLETED"] },
  { key: "cancelled", label: "Đã huỷ", statuses: ["CANCELLED", "REFUNDED"] },
];

export function tabByKey(key: string | undefined): OrderTab {
  return ORDER_TABS.find((tab) => tab.key === key) ?? ORDER_TABS[0];
}

/** Lọc phía máy chủ chỉ nhận một trạng thái, nên tab gom nhiều trạng thái phải lọc lại ở đây. */
export function matchesTab(status: OrderStatus, tab: OrderTab): boolean {
  return tab.statuses.length === 0 || tab.statuses.includes(status);
}

/** Tổng số đơn của một tab, cộng từ bảng đếm theo trạng thái mà máy chủ trả về. */
export function countForTab(tab: OrderTab, counts: Record<string, number>): number {
  const statuses = tab.statuses.length > 0 ? tab.statuses : (Object.keys(counts) as OrderStatus[]);
  return statuses.reduce((total, status) => total + (counts[status] ?? 0), 0);
}
