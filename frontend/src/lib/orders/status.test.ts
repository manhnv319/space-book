import { describe, expect, it } from "vitest";

import { buildRoute, countForTab, isOnDeliveryRoute, matchesTab, ORDER_TABS, statusLabel, tabByKey } from "@/lib/orders/status";
import type { OrderStatus, OrderStatusStep } from "@/lib/types/checkout";

const step = (status: OrderStatusStep["status"], changedAt: string): OrderStatusStep => ({
  status, changedAt, source: "AUTO",
});

describe("buildRoute", () => {
  it("shows the whole route so the customer sees what is still ahead", () => {
    const route = buildRoute("CONFIRMED", [step("CONFIRMED", "2026-07-27T10:00:00Z")]);

    expect(route.map((item) => item.status))
      .toEqual(["CONFIRMED", "PROCESSING", "SHIPPING", "COMPLETED"]);
    expect(route.filter((item) => item.reached)).toHaveLength(1);
  });

  it("marks every stage up to the current one as reached", () => {
    const route = buildRoute("SHIPPING", []);

    expect(route.map((item) => item.reached)).toEqual([true, true, true, false]);
    expect(route.find((item) => item.current)?.status).toBe("SHIPPING");
  });

  it("attaches timestamps only where history actually has them", () => {
    const route = buildRoute("PROCESSING", [
      step("CONFIRMED", "2026-07-27T10:00:00Z"),
      step("PROCESSING", "2026-07-27T10:02:00Z"),
    ]);

    expect(route[0].reachedAt).toBe("2026-07-27T10:00:00Z");
    expect(route[1].reachedAt).toBe("2026-07-27T10:02:00Z");
    // Reached but never recorded, and never recorded means no time to show.
    expect(route[2].reachedAt).toBeNull();
  });

  it("invents no timestamps for an order predating the history table", () => {
    const route = buildRoute("COMPLETED", []);

    expect(route.every((item) => item.reached)).toBe(true);
    expect(route.every((item) => item.reachedAt === null)).toBe(true);
  });

  it("keeps the earliest time when a status was recorded more than once", () => {
    const route = buildRoute("CONFIRMED", [
      step("CONFIRMED", "2026-07-27T10:00:00Z"),
      step("CONFIRMED", "2026-07-27T11:30:00Z"),
    ]);

    expect(route[0].reachedAt).toBe("2026-07-27T10:00:00Z");
  });

  it("reaches nothing for a cancelled order", () => {
    const route = buildRoute("CANCELLED", []);

    expect(route.every((item) => !item.reached)).toBe(true);
  });
});

describe("isOnDeliveryRoute", () => {
  it("separates delivery from the states that end it", () => {
    expect(isOnDeliveryRoute("SHIPPING")).toBe(true);
    expect(isOnDeliveryRoute("CANCELLED")).toBe(false);
    expect(isOnDeliveryRoute("PENDING")).toBe(false);
  });
});

describe("statusLabel", () => {
  it("uses the wording the customer was promised", () => {
    expect(statusLabel("PROCESSING")).toBe("Đã giao cho đơn vị vận chuyển");
    expect(statusLabel("COMPLETED")).toBe("Đã nhận hàng");
  });
});

describe("order tabs", () => {
  it("falls back to Tất cả for an unknown or missing tab", () => {
    expect(tabByKey(undefined).key).toBe("all");
    expect(tabByKey("khong-ton-tai").key).toBe("all");
  });

  it("groups the states a customer thinks of as one thing", () => {
    // "Đang chuẩn bị" covers both confirmed and processing: the customer does
    // not care which internal state the shop is in.
    const preparing = tabByKey("preparing");
    expect(matchesTab("CONFIRMED", preparing)).toBe(true);
    expect(matchesTab("PROCESSING", preparing)).toBe(true);
    expect(matchesTab("SHIPPING", preparing)).toBe(false);
  });

  it("matches everything under Tất cả", () => {
    const all = tabByKey("all");
    expect(matchesTab("PENDING", all)).toBe(true);
    expect(matchesTab("CANCELLED", all)).toBe(true);
  });

  it("keeps refunded with cancelled rather than losing it", () => {
    expect(matchesTab("REFUNDED", tabByKey("cancelled"))).toBe(true);
  });

  it("covers every status in at least one tab so nothing is unreachable", () => {
    const statuses: OrderStatus[] =
      ["PENDING", "CONFIRMED", "PROCESSING", "SHIPPING", "COMPLETED", "CANCELLED", "REFUNDED"];
    for (const status of statuses) {
      const reachable = ORDER_TABS.slice(1).some((tab) => matchesTab(status, tab));
      expect(reachable, `${status} không thuộc tab nào`).toBe(true);
    }
  });
});

describe("countForTab", () => {
  const counts = { PENDING: 2, CONFIRMED: 1, PROCESSING: 3, SHIPPING: 1, COMPLETED: 5, CANCELLED: 1 };

  it("sums the statuses a grouped tab covers", () => {
    expect(countForTab(tabByKey("preparing"), counts)).toBe(4);
  });

  it("counts everything under Tất cả", () => {
    expect(countForTab(tabByKey("all"), counts)).toBe(13);
  });

  it("reads zero for a tab with nothing in it", () => {
    expect(countForTab(tabByKey("cancelled"), { COMPLETED: 5 })).toBe(0);
  });

  it("survives a status the server reports that no tab claims", () => {
    // Tất cả sums whatever the server sent, so a new status still shows up there.
    expect(countForTab(tabByKey("all"), { SOMETHING_NEW: 4 })).toBe(4);
  });
});
