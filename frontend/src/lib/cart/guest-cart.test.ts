import { describe, expect, it } from "vitest";

import {
  addGuestItem,
  countGuestItems,
  decodeGuestCart,
  encodeGuestCart,
  MAX_GUEST_ITEMS,
  removeGuestItem,
  updateGuestQuantity,
  type GuestCartItem,
} from "@/lib/cart/guest-cart";

const purchase = (bookId: number, quantity = 1): GuestCartItem => ({ bookId, itemType: "PURCHASE", quantity });
const rental = (bookId: number, termValue = 7, termUnit: "DAY" | "WEEK" | "MONTH" = "DAY"): GuestCartItem => ({
  bookId,
  itemType: "RENTAL",
  quantity: 1,
  rentalTermValue: termValue,
  rentalTermUnit: termUnit,
});

describe("encodeGuestCart / decodeGuestCart", () => {
  it("round-trips a valid item list", () => {
    const items = [purchase(1, 2), rental(2)];
    expect(decodeGuestCart(encodeGuestCart(items))).toEqual(items);
  });

  it("returns [] for missing/empty/corrupt cookie value", () => {
    expect(decodeGuestCart(undefined)).toEqual([]);
    expect(decodeGuestCart("")).toEqual([]);
    expect(decodeGuestCart("not-base64-json!!!")).toEqual([]);
  });

  it("drops invalid elements instead of throwing (untrusted cookie input)", () => {
    const raw = Buffer.from(
      JSON.stringify([
        purchase(1),
        { bookId: -1, itemType: "PURCHASE", quantity: 1 },
        { bookId: 2, itemType: "NOT_A_TYPE", quantity: 1 },
        { bookId: 3, itemType: "PURCHASE", quantity: 100 },
        { bookId: 4, itemType: "RENTAL", quantity: 1 },
        "garbage",
      ]),
    ).toString("base64url");
    expect(decodeGuestCart(raw)).toEqual([purchase(1)]);
  });

  it("caps decoded items at MAX_GUEST_ITEMS", () => {
    const items = Array.from({ length: MAX_GUEST_ITEMS + 5 }, (_, i) => purchase(i + 1));
    const raw = encodeGuestCart(items);
    expect(decodeGuestCart(raw)).toHaveLength(MAX_GUEST_ITEMS);
  });
});

describe("addGuestItem", () => {
  it("adds a new purchase item", () => {
    const result = addGuestItem([], purchase(1));
    expect(result.rejected).toBeUndefined();
    expect(result.items).toEqual([purchase(1)]);
  });

  it("merges duplicate PURCHASE of same book by summing quantity (capped at 99)", () => {
    const result = addGuestItem([purchase(1, 90)], purchase(1, 20));
    expect(result.items).toEqual([purchase(1, 99)]);
  });

  it("keeps RENTAL duplicate (same book + same term) unchanged, no double intent", () => {
    const existing = [rental(1, 7, "DAY")];
    const result = addGuestItem(existing, rental(1, 7, "DAY"));
    expect(result.items).toEqual(existing);
  });

  it("adds RENTAL as separate item when the term differs", () => {
    const result = addGuestItem([rental(1, 7, "DAY")], rental(1, 1, "MONTH"));
    expect(result.items).toHaveLength(2);
  });

  it("rejects with 'full' at MAX_GUEST_ITEMS without mutating existing items", () => {
    const items = Array.from({ length: MAX_GUEST_ITEMS }, (_, i) => purchase(i + 1));
    const result = addGuestItem(items, purchase(MAX_GUEST_ITEMS + 1));
    expect(result.rejected).toBe("full");
    expect(result.items).toEqual(items);
  });

  it("never stores any price field on the guest cart item", () => {
    const result = addGuestItem([], purchase(1));
    expect(result.items[0]).not.toHaveProperty("unitPrice");
    expect(result.items[0]).not.toHaveProperty("subtotal");
  });
});

describe("updateGuestQuantity / removeGuestItem", () => {
  it("clamps quantity to [1, 99]", () => {
    const items = [purchase(1, 1)];
    expect(updateGuestQuantity(items, 0, 0)[0].quantity).toBe(1);
    expect(updateGuestQuantity(items, 0, 500)[0].quantity).toBe(99);
    expect(updateGuestQuantity(items, 0, 5)[0].quantity).toBe(5);
  });

  it("is a no-op for out-of-range index", () => {
    const items = [purchase(1)];
    expect(updateGuestQuantity(items, 5, 3)).toBe(items);
    expect(removeGuestItem(items, -1)).toBe(items);
  });

  it("removes item by index", () => {
    const items = [purchase(1), purchase(2)];
    expect(removeGuestItem(items, 0)).toEqual([purchase(2)]);
  });
});

describe("countGuestItems", () => {
  it("sums quantities across items", () => {
    expect(countGuestItems([purchase(1, 2), purchase(2, 3), rental(3)])).toBe(6);
  });

  it("returns 0 for an empty cart", () => {
    expect(countGuestItems([])).toBe(0);
  });
});
