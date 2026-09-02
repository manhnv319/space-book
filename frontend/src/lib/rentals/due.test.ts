import { describe, expect, it } from "vitest";

import { describeDue, isActiveRental, rentalStatusLabel } from "@/lib/rentals/due";
import type { Rental } from "@/lib/types/rental";

const base: Rental = {
  id: 1, bookCopyId: 12, bookId: 5, bookTitle: "Nhà Giả Kim",
  rentalTermUnit: "WEEK", rentalTermValue: 1, depositAmount: 50_000,
  rentalStartDate: "2026-07-20", plannedReturnDate: "2026-07-27", actualReturnDate: null,
  status: "RENTED", lateDays: null, lateFeeAmount: null, damageFeeAmount: null,
};

const rental = (overrides: Partial<Rental> = {}): Rental => ({ ...base, ...overrides });
const on = (iso: string) => new Date(`${iso}T13:45:00`);

describe("describeDue", () => {
  it("counts whole days left", () => {
    expect(describeDue(rental(), on("2026-07-25")).text).toBe("Còn 2 ngày");
  });

  it("says today when the deadline is today, whatever the hour", () => {
    // A due date is a day, so the wording must not flip at some hour of it.
    expect(describeDue(rental(), new Date("2026-07-27T00:05:00")).text).toBe("Đến hạn hôm nay");
    expect(describeDue(rental(), new Date("2026-07-27T23:55:00")).text).toBe("Đến hạn hôm nay");
  });

  it("reports overdue once the date has passed", () => {
    const info = describeDue(rental(), on("2026-07-30"));
    expect(info.overdue).toBe(true);
    expect(info.text).toBe("Quá hạn 3 ngày");
  });

  it("prefers the server's lateDays over its own arithmetic", () => {
    // The server decides the late fee; showing a different number invites a dispute.
    const info = describeDue(rental({ status: "LATE", lateDays: 10 }), on("2026-07-30"));
    expect(info.text).toBe("Quá hạn 10 ngày");
  });

  it("trusts a LATE status even when the date has not passed yet", () => {
    expect(describeDue(rental({ status: "LATE", lateDays: 1 }), on("2026-07-25")).overdue).toBe(true);
  });

  it("stops counting once the book is back", () => {
    const info = describeDue(rental({ status: "RETURNED", actualReturnDate: "2026-07-26" }), on("2026-07-30"));
    expect(info.overdue).toBe(false);
    expect(info.text).toBe("Đã trả xong");
  });

  it("says nothing rather than guessing when there is no due date", () => {
    expect(describeDue(rental({ plannedReturnDate: null }), on("2026-07-25")).text).toBe("Chưa có hạn trả");
    expect(describeDue(rental({ plannedReturnDate: "khong-phai-ngay" }), on("2026-07-25")).text)
      .toBe("Chưa có hạn trả");
  });
});

describe("isActiveRental", () => {
  it("counts the states where the reader still holds the book", () => {
    expect(isActiveRental(rental({ status: "RENTED" }))).toBe(true);
    expect(isActiveRental(rental({ status: "LATE" }))).toBe(true);
    expect(isActiveRental(rental({ status: "RETURNED" }))).toBe(false);
    expect(isActiveRental(rental({ status: "PENDING" }))).toBe(false);
  });
});

describe("rentalStatusLabel", () => {
  it("translates the states a reader can see", () => {
    expect(rentalStatusLabel("RENTED")).toBe("Đang thuê");
    expect(rentalStatusLabel("LATE")).toBe("Quá hạn");
  });
});
