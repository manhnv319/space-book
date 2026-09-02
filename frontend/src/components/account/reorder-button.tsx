"use client";

import { useActionState } from "react";

import { reorderAction, type CheckoutState } from "@/app/actions/checkout";

const INITIAL: CheckoutState = { status: "idle", message: "" };

/** Client island: mua lại một đơn. Danh sách sách do máy chủ đọc lại từ đơn. */
export function ReorderButton({ orderId }: { orderId: number }) {
  const [state, formAction, pending] = useActionState(reorderAction, INITIAL);

  return (
    <form action={formAction} className="order-reorder">
      <input type="hidden" name="orderId" value={orderId} />
      <button className="button button-secondary button-small" type="submit" disabled={pending}>
        {pending ? "Đang thêm…" : "Mua lại"}
      </button>
      {state.status === "error" && <p className="form-status" role="alert">{state.message}</p>}
    </form>
  );
}
