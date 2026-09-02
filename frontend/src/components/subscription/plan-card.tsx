"use client";

import { useActionState } from "react";

import { purchasePlanAction, type SubscriptionState } from "@/app/actions/subscription";
import { formatVnd } from "@/lib/format/currency";
import type { SubscriptionPlan } from "@/lib/types/subscription";

const INITIAL: SubscriptionState = { status: "idle", message: "" };

/**
 * Client island: một gói kèm nút đặt mua.
 *
 * Giá hiển thị nguyên số máy chủ trả về; không quy đổi ra "mỗi ngày" hay tính
 * gì thêm ở đây — số tiền phải chuyển do máy chủ chốt ở bước sau.
 */
export function PlanCard({ plan, disabled }: { plan: SubscriptionPlan; disabled?: boolean }) {
  const [state, formAction, pending] = useActionState(purchasePlanAction, INITIAL);

  return (
    <li className="plan-card">
      <div className="plan-card-body">
        <h2>{plan.name}</h2>
        {plan.description && <p className="plan-card-desc">{plan.description}</p>}
        <ul className="plan-card-facts">
          <li>Thời hạn {plan.durationDays} ngày</li>
          {plan.maxRentals != null && <li>Tối đa {plan.maxRentals} lượt thuê</li>}
        </ul>
      </div>

      <form action={formAction} className="plan-card-buy">
        <input type="hidden" name="subscriptionId" value={plan.id} />
        <strong className="plan-card-price">{formatVnd(plan.price)}</strong>
        <button className="button" type="submit" disabled={pending || disabled}>
          {pending ? "Đang tạo…" : disabled ? "Đang dùng gói khác" : "Mua gói"}
        </button>
        {state.status === "error" && <p className="form-status" role="alert">{state.message}</p>}
      </form>
    </li>
  );
}
