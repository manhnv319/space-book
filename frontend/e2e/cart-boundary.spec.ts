import { expect, test } from "./fixtures/unauthenticated-page";

/**
 * Unlike /checkout, /gio-hang must NOT require authentication — a guest's
 * cookie cart has to be viewable so they can review it before deciding to
 * log in. These run without a live backend (same as the other smoke specs),
 * so they only cover the guest (cookie) branch, not the logged-in GET /cart
 * branch or actual add-to-cart submissions.
 */
test("guest can view the cart page without being redirected to login", async ({ unauthenticatedPage }) => {
  await unauthenticatedPage.goto("/gio-hang");

  await expect(unauthenticatedPage).toHaveURL(/\/gio-hang$/);
  await expect(unauthenticatedPage.getByRole("heading", { name: "Giỏ hàng của bạn đang trống" })).toBeVisible();
  await expect(unauthenticatedPage.getByRole("link", { name: "Xem danh sách sách" })).toBeVisible();
});

test("guest cart page never shows a computed total (no BE price data available)", async ({ unauthenticatedPage }) => {
  await unauthenticatedPage.goto("/gio-hang");

  await expect(unauthenticatedPage.getByText(/tổng phải trả/i)).toHaveCount(0);
  await expect(unauthenticatedPage.getByText(/tạm tính/i)).toHaveCount(0);
});
