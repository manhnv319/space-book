import "server-only";

import { apiRead } from "@/lib/bff/server-fetch";
import type { BankTransferPayment } from "@/lib/types/checkout";
import type { CustomerSubscription, SubscriptionPlan } from "@/lib/types/subscription";

/** Các gói đang bán — công khai với người đã đăng nhập. */
export async function getActivePlans(): Promise<SubscriptionPlan[]> {
  return apiRead<SubscriptionPlan[]>("/api/v1/subscriptions/active");
}

export async function getMySubscription(): Promise<CustomerSubscription | null> {
  // BE trả 404 khi chưa có gói nào đang chạy — đó là trạng thái bình thường.
  return apiRead<CustomerSubscription>("/api/v1/subscriptions/me/active").catch(() => null);
}

/** Thông tin chuyển khoản cho gói đang chờ thanh toán. */
export async function getSubscriptionPayment(id: number): Promise<BankTransferPayment> {
  return apiRead<BankTransferPayment>(`/api/v1/subscriptions/me/${id}/payment`);
}
