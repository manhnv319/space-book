"use server";

import { redirect } from "next/navigation";
import { revalidatePath } from "next/cache";

import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";
import { BackendError } from "@/lib/bff/backend-error";
import type { CustomerSubscription } from "@/lib/types/subscription";

export type SubscriptionState = { status: "idle" | "error"; message: string };

/**
 * Đặt mua một gói.
 *
 * Gói được tạo ở trạng thái chờ thanh toán — chưa dùng được cho tới khi tiền về.
 * Chuyển thẳng sang màn chuyển khoản để khách thấy mã và số tiền ngay.
 */
export async function purchasePlanAction(_state: SubscriptionState, data: FormData): Promise<SubscriptionState> {
  const subscriptionId = Number(data.get("subscriptionId"));
  if (!Number.isInteger(subscriptionId) || subscriptionId <= 0) {
    return { status: "error", message: "Gói không hợp lệ." };
  }

  let purchased: CustomerSubscription;
  try {
    purchased = await executeAuthenticatedMutation<CustomerSubscription>("/api/v1/subscriptions/purchase", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ subscriptionId }),
    });
  } catch (error) {
    console.error("Failed to purchase subscription:", error);
    if (error instanceof BackendError && error.status === 404) {
      return { status: "error", message: "Gói này không còn được bán." };
    }
    return { status: "error", message: "Không đặt mua được gói lúc này. Vui lòng thử lại." };
  }

  revalidatePath("/goi-thue");
  // Ngoài try/catch: redirect hoạt động bằng cách ném lỗi.
  redirect(`/goi-thue/${purchased.id}`);
}
