"use client";

import { useActionState } from "react";

import { deleteAddressAction, type AddressState } from "@/app/actions/address";

const INITIAL: AddressState = { status: "idle", message: "" };

/** Client island: xoá một địa chỉ. Tách riêng để phần danh sách vẫn là server component. */
export function DeleteAddressButton({ addressId, label }: { addressId: number; label: string }) {
  const [state, formAction, pending] = useActionState(deleteAddressAction, INITIAL);

  return (
    <form action={formAction} className="address-delete">
      <input type="hidden" name="addressId" value={addressId} />
      <button type="submit" className="button-small button-danger" disabled={pending}
              aria-label={`Xoá địa chỉ của ${label}`}>
        {pending ? "Đang xoá…" : "Xoá"}
      </button>
      {state.status === "error" && <span className="form-status" role="alert">{state.message}</span>}
    </form>
  );
}
