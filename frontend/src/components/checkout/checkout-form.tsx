"use client";

import { useActionState } from "react";

import { placeOrderAction, type CheckoutState } from "@/app/actions/checkout";
import type { Address } from "@/lib/types/checkout";

const INITIAL: CheckoutState = { status: "idle", message: "" };

function formatAddress(address: Address): string {
  return [address.addressDetail, address.ward, address.district, address.province].filter(Boolean).join(", ");
}

/**
 * Client island: chọn địa chỉ + đặt đơn.
 *
 * Không hiển thị hay tính bất kỳ số tiền nào — tổng tiền thật do BE chốt khi
 * tạo đơn và được hiện ở màn thanh toán ngay sau đó.
 */
export function CheckoutForm({ addresses }: { addresses: Address[] }) {
  const [state, formAction, pending] = useActionState(placeOrderAction, INITIAL);
  const defaultAddressId = (addresses.find((item) => item.isDefault) ?? addresses[0])?.id;

  return (
    <form action={formAction} className="checkout-form">
      <fieldset className="checkout-fieldset">
        <legend>Địa chỉ nhận hàng</legend>
        <ul className="address-list">
          {addresses.map((address) => (
            <li key={address.id}>
              <label className="address-option">
                <input
                  type="radio"
                  name="shippingAddressId"
                  value={address.id}
                  defaultChecked={address.id === defaultAddressId}
                  required
                />
                <span>
                  <strong>{address.fullName}</strong> · {address.phone}
                  <br />
                  {formatAddress(address)}
                </span>
              </label>
            </li>
          ))}
        </ul>
      </fieldset>

      <label className="checkout-notes">
        <span>Ghi chú cho đơn hàng (không bắt buộc)</span>
        <textarea name="notes" rows={3} maxLength={500} />
      </label>

      {state.status === "error" && (
        <p className="form-status" role="alert">{state.message}</p>
      )}

      <button className="button" type="submit" disabled={pending}>
        {pending ? "Đang tạo đơn…" : "Đặt đơn và lấy mã chuyển khoản"}
      </button>
    </form>
  );
}
