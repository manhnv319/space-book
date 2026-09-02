import Link from "next/link";

import { removeCartItemAction, updateCartItemAction, type ActionResult } from "@/app/actions/cart";
import { CartEmptyState } from "@/components/cart/cart-empty-state";
import { Badge } from "@/components/ui/badge";
import { BookCover } from "@/components/ui/book-cover";
import { guestListedUnitPrice, itemTypeLabel } from "@/lib/cart/cart-display";
import type { GuestCartItem } from "@/lib/cart/guest-cart";
import { formatVnd } from "@/lib/format/currency";
import { getBooksByIds } from "@/lib/services/book-service";

// See cart-item-row.tsx for why these need their own inline "use server"
// wrapper instead of `action.bind(...)` directly.
const NOOP_STATE: ActionResult = { status: "ok", message: "" };

async function submitQuantityUpdate(data: FormData): Promise<void> {
  "use server";
  await updateCartItemAction(NOOP_STATE, data);
}

async function submitRemove(data: FormData): Promise<void> {
  "use server";
  await removeCartItemAction(NOOP_STATE, data);
}

/**
 * Guest (cookie) cart view. The cookie only stores intent (bookId/itemType/
 * quantity/term) — no BE-computed price ever touches it — so this fetches
 * each book (cap 20, same as the cookie's own cap) to show title/cover/
 * listed price. There is intentionally NO total shown: the cookie has never
 * been validated/priced by the cart service, only `GET /cart` after login
 * produces a trustworthy total.
 */
export async function GuestCartView({ items }: { items: GuestCartItem[] }) {
  if (!items.length) return <CartEmptyState />;

  const books = await getBooksByIds(items.map((item) => item.bookId));

  return (
    <div className="cart-page">
      <h1>Giỏ hàng</h1>
      <div className="cart-guest-banner" role="note">
        <p>Bạn chưa đăng nhập. Đăng nhập để xem tổng tiền chính xác và thanh toán.</p>
        <Link className="button" href="/login?next=/gio-hang">
          Đăng nhập
        </Link>
      </div>
      <ul className="cart-items">
        {items.map((item, index) => {
          const book = books.get(item.bookId);
          const unitPrice = book ? guestListedUnitPrice(item, book) : null;
          return (
            <li key={`${item.bookId}-${index}`} className="cart-item-row">
              <Link href={`/sach/${item.bookId}`} className="cart-item-cover">
                <BookCover src={book?.imageUrl} alt={book?.title ?? "Sách"} variant="card" />
              </Link>
              <div className="cart-item-info">
                <Link href={`/sach/${item.bookId}`} className="cart-item-title">
                  {book?.title ?? "Không tải được thông tin sách"}
                </Link>
                <Badge tone="muted">{itemTypeLabel(item)}</Badge>
                <p className="cart-item-price">Giá niêm yết: {formatVnd(unitPrice)}</p>
              </div>
              <div className="cart-item-actions">
                {item.itemType === "PURCHASE" ? (
                  <form action={submitQuantityUpdate} className="cart-item-update-form">
                    <input type="hidden" name="index" value={index} />
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
                  <input type="hidden" name="index" value={index} />
                  <button type="submit" className="link-button">
                    Xoá
                  </button>
                </form>
              </div>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
