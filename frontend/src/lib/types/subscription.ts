export type CustomerSubscriptionStatus = "PENDING_PAYMENT" | "ACTIVE" | "EXPIRED" | "CANCELLED";

/** Gói bán ra — khớp `SubscriptionResponse`. */
export interface SubscriptionPlan {
  id: number;
  name: string;
  description: string | null;
  price: number;
  durationDays: number;
  maxRentals: number | null;
}

/**
 * Gói của một khách — khớp `CustomerSubscriptionResponse`.
 *
 * `startDate`/`endDate` null khi gói còn `PENDING_PAYMENT`: thời hạn chỉ bắt đầu
 * tính từ lúc tiền về, nên trước đó chưa có ngày nào để hiển thị.
 */
export interface CustomerSubscription {
  id: number;
  subscriptionId: number;
  startDate: string | null;
  endDate: string | null;
  usedRentals: number | null;
  status: CustomerSubscriptionStatus;
  subscription: SubscriptionPlan | null;
}
