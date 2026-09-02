import { createServer } from "node:http";

import { expect, test } from "@playwright/test";

async function ensurePasswordResetBackend(): Promise<{ close: () => Promise<void>; requests: string[]; tracksRequests: boolean }> {
  const requests: string[] = [];
  const server = createServer((request, response) => {
    requests.push(`${request.method} ${request.url}`);
    response.writeHead(200, { "content-type": "application/json" });
    response.end(JSON.stringify({ data: {} }));
  });

  try {
    await new Promise<void>((resolve, reject) => {
      server.once("error", reject);
      server.listen(8080, resolve);
    });
  } catch (error) {
    if ((error as NodeJS.ErrnoException).code === "EADDRINUSE") {
      return { close: async () => {}, requests, tracksRequests: false };
    }
    throw error;
  }

  return {
    close: () => new Promise<void>((resolve, reject) => {
      server.close((error) => error ? reject(error) : resolve());
    }),
    requests,
    tracksRequests: true,
  };
}

test("password reset routes expose the public auth flow without session persistence", async ({ page }) => {
  await page.goto("/forgot-password");
  await expect(page.getByRole("heading", { name: "Quên mật khẩu?" })).toBeVisible();
  await expect(page.getByRole("button", { name: "Gửi mã xác minh" })).toBeVisible();
  await expect(page.locator("[data-testid=auth-backdrop]")).toHaveAttribute("inert", "");

  await page.goto("/reset-password");
  await expect(page.getByRole("heading", { name: "Đặt lại mật khẩu" })).toBeVisible();
  await expect(page.getByLabel("Mã xác minh")).toHaveAttribute("maxlength", "6");
  await expect(page.getByRole("link", { name: "Quay lại đăng nhập" })).toHaveAttribute("href", "/login");
});

test("forgot password continues into reset fields without asking for email again", async ({ page }) => {
  const backend = await ensurePasswordResetBackend();
  try {
    await page.goto("/forgot-password");
    await page.getByLabel("Email").fill("reader@example.com");
    await page.getByRole("button", { name: "Gửi mã xác minh" }).click();

    await expect(page.getByRole("heading", { name: "Đặt lại mật khẩu" })).toBeVisible();
    await expect(page.getByText("Mã xác minh đã được gửi tới reader@example.com.")).toBeVisible();
    await expect(page.getByLabel("Mã xác minh")).toBeVisible();
    await expect(page.getByLabel("Mật khẩu mới")).toBeVisible();
    await expect(page.getByLabel("Xác nhận mật khẩu")).toBeVisible();
    await expect(page.locator("label", { hasText: "Email" })).toHaveCount(0);
    await expect(page.locator('input[name="email"][type="hidden"]')).toHaveValue("reader@example.com");
    await page.getByRole("button", { name: "Dùng email khác" }).click();
    await expect(page.getByRole("heading", { name: "Quên mật khẩu?" })).toBeVisible();
    await expect(page.getByLabel("Email")).toBeVisible();
    if (backend.tracksRequests) expect(backend.requests).toContain("POST /api/v1/users/forgot-password");
  } finally {
    await backend.close();
  }
});

test("login form keeps the single horizontal brand and accessible password controls", async ({ page }) => {
  await page.goto("/login");
  await expect(page.getByRole("dialog", { name: "Đăng nhập" }).getByRole("img", { name: "VelstrongBook" })).toBeVisible();
  await expect(page.getByLabel("Tên đăng nhập/Email")).toBeVisible();
  await expect(page.locator('input[name="password"]')).toHaveAttribute("type", "password");
  await expect(page.getByRole("button", { name: "Đăng nhập" })).toBeDisabled();
  await page.getByLabel("Tên đăng nhập/Email").fill("reader");
  await page.locator('input[name="password"]').fill("password1");
  await expect(page.getByRole("button", { name: "Đăng nhập" })).toBeEnabled();
  await page.getByRole("button", { name: "Hiện mật khẩu" }).click();
  await expect(page.locator('input[name="password"]')).toHaveAttribute("type", "text");
  await expect(page.getByRole("link", { name: "Quên mật khẩu?" })).toHaveAttribute("href", "/forgot-password");
});

test("registration requires matching password confirmation", async ({ page }) => {
  await page.goto("/register");
  await expect(page.getByLabel("Nhập lại mật khẩu")).toBeVisible();
  await page.getByLabel("Tên đăng nhập").fill("new-reader");
  await page.locator('input[name="password"]').fill("password1");
  await page.locator('input[name="confirmation"]').fill("different");
  await expect(page.getByRole("button", { name: "Tạo tài khoản" })).toBeDisabled();
  await page.locator('input[name="confirmation"]').fill("password1");
  await expect(page.getByRole("button", { name: "Tạo tài khoản" })).toBeEnabled();
});

test("site shell keeps the ecommerce header and footer consistent", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("banner")).toBeVisible();
  await expect(page.getByLabel("Tìm sách")).toBeVisible();
  await expect(page.getByRole("link", { name: "Khám phá", exact: true })).toBeVisible();
  await expect(page.getByLabel("Điều hướng chính").getByRole("link", { name: "Tất cả sách", exact: true })).toBeVisible();
  await expect(page.getByLabel("Giỏ hàng")).toBeVisible();
  await expect(page.getByRole("img", { name: "Tài khoản" })).toHaveCount(0);
  await expect(page.getByRole("img", { name: "Giỏ thuê" })).toHaveCount(0);
  await expect(page.getByRole("banner").getByRole("link", { name: "Đăng nhập", exact: true })).toBeVisible();
  await expect(page.getByRole("banner").getByRole("link", { name: "Đăng ký", exact: true })).toBeVisible();
  await expect(page.getByRole("contentinfo")).toContainText("VelstrongBook");
});

test("site shell remains usable on a phone viewport", async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto("/");
  await expect(page.getByLabel("Tìm sách")).toBeVisible();
  await expect(page.getByRole("banner").getByRole("link", { name: "Đăng ký", exact: true })).toBeVisible();
  expect(await page.evaluate(() => document.documentElement.scrollWidth)).toBeLessThanOrEqual(390);
});

test("site shell moves search below actions at tablet width", async ({ page }) => {
  await page.setViewportSize({ width: 834, height: 1112 });
  await page.goto("/");
  await expect(page.getByLabel("Tìm sách")).toBeVisible();
  expect(await page.evaluate(() => document.documentElement.scrollWidth)).toBeLessThanOrEqual(834);
});
