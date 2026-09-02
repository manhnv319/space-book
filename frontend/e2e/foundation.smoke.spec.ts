import { expect, test } from "./fixtures/unauthenticated-page";

test("public home renders without backend credentials", async ({ unauthenticatedPage }) => {
  await unauthenticatedPage.goto("/");

  // The hero spotlights a real book when the catalog responds and falls back to
  // static copy when it does not, so assert the shape rather than either headline
  // — pinning the fallback text would make this pass only while the backend is down.
  await expect(unauthenticatedPage.locator(".hero-spotlight")).toBeVisible();
  await expect(unauthenticatedPage.getByRole("heading", { level: 1 })).toBeVisible();
  await expect(unauthenticatedPage.getByRole("banner").getByRole("link", { name: "VelstrongBook, trang chủ" })).toBeVisible();
  await expect(unauthenticatedPage.getByRole("banner").getByRole("link", { name: "Đăng nhập" })).toBeVisible();
});

test("account deep link redirects unauthenticated visitors with next", async ({ unauthenticatedPage }) => {
  await unauthenticatedPage.goto("/account/don-hang/demo?tab=all");

  await expect(unauthenticatedPage).toHaveURL(
    /\/login\?next=%2Faccount%2Fdon-hang%2Fdemo%3Ftab%3Dall/,
  );
});

test("checkout deep link redirects unauthenticated visitors with next", async ({ unauthenticatedPage }) => {
  await unauthenticatedPage.goto("/checkout?from=cart");

  await expect(unauthenticatedPage).toHaveURL(/\/login\?next=%2Fcheckout%3Ffrom%3Dcart/);
});

test("admin deep link redirects unauthenticated visitors with next, before any admin markup renders", async ({ unauthenticatedPage }) => {
  await unauthenticatedPage.goto("/admin/bai-viet");

  await expect(unauthenticatedPage).toHaveURL(/\/login\?next=%2Fadmin%2Fbai-viet/);
  await expect(unauthenticatedPage.getByRole("heading", { name: "Bài viết" })).toHaveCount(0);
});
