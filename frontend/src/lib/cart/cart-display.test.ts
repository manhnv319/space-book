import { describe, expect, it } from "vitest";

import { guestListedUnitPrice, itemTypeLabel } from "@/lib/cart/cart-display";
import type { Book } from "@/lib/types/book";

const book: Book = {
  id: 1,
  isbn: "123",
  title: "Test Book",
  description: "",
  imageUrl: null,
  format: "PAPERBACK",
  listPrice: 100_000,
  rentalPriceDay: 5_000,
  rentalPriceWeek: 25_000,
  rentalPriceMonth: 80_000,
  depositAmount: 200_000,
  publishYear: 2024,
  publisher: "NXB",
  language: "vi",
  pageCount: 100,
  authors: [],
  categories: [],
  createdAt: "2024-01-01T00:00:00Z",
  isFeatured: false,
  isBestseller: false,
};

describe("itemTypeLabel", () => {
  it("labels a purchase item", () => {
    expect(itemTypeLabel({ itemType: "PURCHASE" })).toBe("Mua");
  });

  it("labels a rental item with its term", () => {
    expect(itemTypeLabel({ itemType: "RENTAL", rentalTermValue: 2, rentalTermUnit: "WEEK" })).toBe("Thuê 2 tuần");
  });

  it("falls back gracefully when rental term fields are missing", () => {
    expect(itemTypeLabel({ itemType: "RENTAL" })).toBe("Thuê ? kỳ");
  });
});

describe("guestListedUnitPrice", () => {
  it("returns the list price for a purchase", () => {
    expect(guestListedUnitPrice({ itemType: "PURCHASE" }, book)).toBe(100_000);
  });

  it("returns the day rate for a DAY rental", () => {
    expect(guestListedUnitPrice({ itemType: "RENTAL", rentalTermUnit: "DAY" }, book)).toBe(5_000);
  });

  it("returns the week rate for a WEEK rental", () => {
    expect(guestListedUnitPrice({ itemType: "RENTAL", rentalTermUnit: "WEEK" }, book)).toBe(25_000);
  });

  it("returns the month rate for a MONTH rental", () => {
    expect(guestListedUnitPrice({ itemType: "RENTAL", rentalTermUnit: "MONTH" }, book)).toBe(80_000);
  });

  it("returns null when the rental unit is missing", () => {
    expect(guestListedUnitPrice({ itemType: "RENTAL" }, book)).toBeNull();
  });

  it("never derives a price by multiplying term value by unit price", () => {
    const twoWeeks = guestListedUnitPrice({ itemType: "RENTAL", rentalTermValue: 2, rentalTermUnit: "WEEK" }, book);
    expect(twoWeeks).toBe(book.rentalPriceWeek);
    expect(twoWeeks).not.toBe(book.rentalPriceWeek * 2);
  });
});
