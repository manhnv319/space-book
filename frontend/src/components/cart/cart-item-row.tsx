import Link from "next/link";

import { removeCartItemAction, updateCartItemAction, type ActionResult } from "@/app/actions/cart";
import { Badge } from "@/components/ui/badge";
import { BookCover } from "@/components/ui/book-cover";
import { itemTypeLabel } from "@/lib/cart/cart-display";
import { formatVnd } from "@/lib/format/currency";
import type { CartItemDetail } from "@/lib/types/cart";

interface CartItemRowProps {
  item: CartItemDetail;
  coverUrl: string | null;
}

// Server-only forms below call the action directly (no useActionState), so
// there is no pending/prevState UI. Each wrapper is its own inline Server
// Action (`"use server"`) that supplies the prevState arg the action
// signature expects, then discards the return value — `action.bind(...)`
// alone doesn't satisfy the `(formData) => void` type React expects for a
// plain `<form action>`. Keeps the row at 0 client JS (works with JS off).
const NOOP_STATE: ActionResult = { status: "ok", message: "" };

async function submitQuantityUpdate(data: FormData): Promise<void> {
  "use server";
  await updateCartItemAction(NOOP_STATE, data);
}

async function submitRemove(data: FormData): Promise<void> {
  "use server";
  await removeCartItemAction(NOOP_STATE, data);
}

export function CartItemRow({ item, coverUrl }: CartItemRowProps) {
  return (
    <li className="cart-item-row">
      <Link href={`/sach/${item.bookId}`} className="cart-item-cover">
        <BookCover src={coverUrl} alt={item.bookTitle ?? "Sách"} variant="card" />
      </Link>

      <div className="cart-item-info">
        <Link href={`/sach/${item.bookId}`} className="cart-item-title">
          {item.bookTitle ?? "Sách"}
        </Link>
        <Badge tone="muted">{itemTypeLabel(item)}</Badge>
        <p className="cart-item-price">Đơn giá: {formatVnd(item.unitPrice)}</p>
        <p className="cart-item-price">Thành tiền: {formatVnd(item.subtotal)}</p>
        {item.itemType === "RENTAL" ? <p className="cart-item-deposit">Cọc: {formatVnd(item.depositAmount)}</p> : null}
      </div>

      <div className="cart-item-actions">
        {item.itemType === "PURCHASE" ? (
          <form action={submitQuantityUpdate} className="cart-item-update-form">
            <input type="hidden" name="itemId" value={item.id} />
            <input
              type="number"
              name="quantity"
              defaultValue={item.quantity ?? 1}
              min={1}
              max={99}
              aria-label="Số lượng"
            />
            <button type="submit" className="button button-small button-secondary">
              Cập nhật
            </button>
          </form>
        ) : null}
        <form action={submitRemove}>
          <input type="hidden" name="itemId" value={item.id} />
          <button type="submit" className="link-button">
            Xoá
          </button>
        </form>
      </div>
    </li>
  );
}
