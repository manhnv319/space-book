import { describe, expect, it } from "vitest";

import { uniqueHeroSlides } from "@/lib/home/home-view-model";
import type { Book } from "@/lib/types/book";

function book(id: number): Book {
  return { id, isbn: `isbn-${id}`, title: `Sách ${id}`, description: "", imageUrl: null, format: "PAPERBACK", listPrice: 1, rentalPriceDay: 1, rentalPriceWeek: 1, rentalPriceMonth: 1, depositAmount: 1, publishYear: 2026, publisher: "NXB", language: "vi", pageCount: 1, authors: [], categories: [], createdAt: "2026-08-01", isFeatured: false, isBestseller: false };
}

describe("uniqueHeroSlides", () => {
  it("keeps source priority, removes duplicates, and respects the limit", () => {
    expect(uniqueHeroSlides([{ eyebrow: "Nổi bật", books: [book(1), book(2)] }, { eyebrow: "Bán chạy", books: [book(2), book(3)] }], 2).map((slide) => [slide.book.id, slide.eyebrow])).toEqual([[1, "Nổi bật"], [2, "Nổi bật"]]);
  });
});
