/**
 * Pure display-formatting helpers shared between the logged-in cart rows
 * (`CartItemDetail`, prices from BE) and the guest cart rows
 * (`GuestCartItem`, no prices — only intent). No `server-only`: kept testable
 * with Vitest and reusable from either component.
 */
import type { ItemType, RentalTermUnit } from "@/lib/types/cart";
import type { Book } from "@/lib/types/book";

interface RentalTermFields {
  itemType: ItemType;
  rentalTermValue?: number | null;
  rentalTermUnit?: RentalTermUnit | null;
}

const RENTAL_TERM_NOUN: Record<RentalTermUnit, string> = {
  DAY: "ngày",
  WEEK: "tuần",
  MONTH: "tháng",
};

/** e.g. "Mua" or "Thuê 2 tuần". Never computes a price — text only. */
export function itemTypeLabel(item: RentalTermFields): string {
  if (item.itemType === "PURCHASE") return "Mua";
  const noun = item.rentalTermUnit ? RENTAL_TERM_NOUN[item.rentalTermUnit] : "kỳ";
  return `Thuê ${item.rentalTermValue ?? "?"} ${noun}`;
}

/**
 * Listed unit price straight from the Book for a guest cart row — a direct
 * field lookup, never a computed rental fee (that's BE-only, see
 * `RentalPricing.java`). Returns `null` when the term unit is missing/invalid.
 */
export function guestListedUnitPrice(item: RentalTermFields, book: Book): number | null {
  if (item.itemType === "PURCHASE") return book.listPrice;
  switch (item.rentalTermUnit) {
    case "DAY":
      return book.rentalPriceDay;
    case "WEEK":
      return book.rentalPriceWeek;
    case "MONTH":
      return book.rentalPriceMonth;
    default:
      return null;
  }
}
