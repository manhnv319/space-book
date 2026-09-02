"use client";

import { useActionState } from "react";

import { addToCartAction, type ActionResult } from "@/app/actions/cart";
import { Toast } from "@/components/ui/toast";
import { QuantityStepper } from "@/components/cart/quantity-stepper";
import { RentalTermPicker } from "@/components/cart/rental-term-picker";

type AddToCartFormProps =
  | { variant: "purchase"; bookId: number }
  | {
      variant: "rental";
      bookId: number;
      rentalPriceDay: number;
      rentalPriceWeek: number;
      rentalPriceMonth: number;
    };

// Empty message keeps <Toast> hidden (it renders null when message is falsy)
// until the action actually returns a result — this doubles as the "idle" state.
const INITIAL_STATE: ActionResult = { status: "ok", message: "" };

/**
 * Client island rendered inside the purchase/rental option cards on the book
 * detail page. Submits via `useActionState(addToCartAction)`; the action
 * itself decides logged-in (POST /cart/items) vs guest (cookie) — see
 * src/app/actions/cart.ts.
 */
export function AddToCartForm(props: AddToCartFormProps) {
  const [state, formAction, pending] = useActionState(addToCartAction, INITIAL_STATE);
  const showLoginPrompt = state.status === "ok" && state.message.includes("Đăng nhập để thanh toán");

  return (
    <form action={formAction} className="add-to-cart-form">
      <input type="hidden" name="bookId" value={props.bookId} />
      <input type="hidden" name="itemType" value={props.variant === "purchase" ? "PURCHASE" : "RENTAL"} />

      {props.variant === "purchase" ? (
        <QuantityStepper />
      ) : (
        <RentalTermPicker
          rentalPriceDay={props.rentalPriceDay}
          rentalPriceWeek={props.rentalPriceWeek}
          rentalPriceMonth={props.rentalPriceMonth}
        />
      )}

      <button
        type="submit"
        className={`button button-full${props.variant === "rental" ? " button-secondary" : ""}`}
        disabled={pending}
      >
        {pending ? "Đang thêm…" : props.variant === "purchase" ? "Thêm vào giỏ mua" : "Thuê cuốn sách này"}
      </button>

      {state.message ? <Toast tone={state.status === "ok" ? "success" : "error"} message={state.message} /> : null}
      {showLoginPrompt ? (
        <a className="text-link" href="/login?next=/gio-hang">
          Đăng nhập ngay
        </a>
      ) : null}
    </form>
  );
}
