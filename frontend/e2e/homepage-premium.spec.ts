import { expect, test } from "@playwright/test";

test.describe("premium homepage", () => {
  test("renders real catalogue sections without hidden core content", async ({ page }) => {
    await page.goto("/");
    await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
    await expect(page.locator(".book-card").first()).toBeVisible();
    expect(await page.locator(".product-carousel-section").count()).toBeGreaterThan(2);
    expect(await page.locator(".product-carousel-section").evaluateAll((sections) => sections.every((section) => getComputedStyle(section).opacity === "1"))).toBe(true);
    expect(await page.evaluate(() => document.documentElement.scrollWidth)).toBeLessThanOrEqual(await page.evaluate(() => document.documentElement.clientWidth));
  });

  test("supports collection navigation and guest purchase/rental cart actions", async ({ page }) => {
    await page.goto("/sach?collection=new");
    await expect(page.getByRole("heading", { name: "Sách mới về" })).toBeVisible();
    const bookHref = await page.locator(".book-card-image-wrapper").first().getAttribute("href");
    expect(bookHref).toMatch(/^\/sach\/\d+$/);
    await page.goto(bookHref!);
    const purchaseForm = page.locator("form.add-to-cart-form").filter({ has: page.locator('input[value="PURCHASE"]') });
    const rentalForm = page.locator("form.add-to-cart-form").filter({ has: page.locator('input[value="RENTAL"]') });
    await purchaseForm.getByRole("button", { name: "Thêm vào giỏ mua" }).click();
    await expect(purchaseForm.getByText(/Đã thêm vào giỏ/)).toBeVisible();
    await rentalForm.getByRole("button", { name: "Thuê cuốn sách này" }).click();
    await expect(rentalForm.getByText(/Đã thêm vào giỏ/)).toBeVisible();
    await page.goto("/gio-hang");
    await expect(page.locator(".cart-item-row")).toHaveCount(2);
  });

  test("opens and closes the mobile navigation drawer", async ({ page }) => {
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto("/");
    await page.getByLabel("Mở menu").click();
    await expect(page.getByLabel("Menu mobile")).toBeVisible();
    await page.keyboard.press("Escape");
    await expect(page.getByLabel("Menu mobile")).toHaveCount(0);
  });
});
