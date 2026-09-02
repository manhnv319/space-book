import Link from "next/link";

import { quickAddPurchaseAction } from "@/app/actions/cart";

export function ProductCardActions({ bookId, canBuy, canRent }: { bookId: number; canBuy: boolean; canRent: boolean }) {
  return (
    <div className="product-card-actions">
      {canBuy ? (
        <form action={quickAddPurchaseAction}>
          <input name="bookId" type="hidden" value={bookId} />
          <input name="itemType" type="hidden" value="PURCHASE" />
          <input name="quantity" type="hidden" value="1" />
          <button aria-label="Thêm vào giỏ" className="product-card-quick-add" type="submit">Thêm</button>
        </form>
      ) : null}
      {canRent ? <Link className="product-card-rent-link" href={`/sach/${bookId}`}>Thuê sách</Link> : null}
    </div>
  );
}
