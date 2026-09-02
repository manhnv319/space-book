import { expect, test } from "@playwright/test";

const routes = ["/", "/sach", "/login", "/register", "/forgot-password", "/reset-password"];

const viewports = [
  { name: "mobile-320", width: 320, height: 720 },
  { name: "mobile-375", width: 375, height: 812 },
  { name: "tablet-768", width: 768, height: 1024 },
  { name: "laptop-1024", width: 1024, height: 768 },
  { name: "desktop-1440", width: 1440, height: 900 },
];

test.describe("responsive layout", () => {
  for (const viewport of viewports) {
    test(`does not overflow at ${viewport.name}`, async ({ page }) => {
      await page.setViewportSize({ width: viewport.width, height: viewport.height });

      for (const route of routes) {
        await page.goto(route);

        const dimensions = await page.evaluate(() => ({
          clientWidth: document.documentElement.clientWidth,
          scrollWidth: document.documentElement.scrollWidth,
        }));

        expect(dimensions.scrollWidth, `${route} overflows at ${viewport.name}`).toBeLessThanOrEqual(
          dimensions.clientWidth,
        );
      }
    });
  }

  for (const width of [320, 375]) {
    test(`keeps mobile touch targets at least 44px wide/high at ${width}px`, async ({ page }) => {
      await page.setViewportSize({ width, height: 812 });

      for (const route of routes) {
        await page.goto(route);

        const smallTargets = await page.evaluate(() => {
          return [...document.querySelectorAll<HTMLElement>("a, button, input, summary")]
            .map((element) => {
              const rect = element.getBoundingClientRect();
              const style = getComputedStyle(element);
              const label = (
                element.textContent ||
                element.getAttribute("aria-label") ||
                element.getAttribute("placeholder") ||
                element.tagName
              ).trim();

              return {
                label,
                width: Math.round(rect.width),
                height: Math.round(rect.height),
                isVisible:
                  style.display !== "none" &&
                  style.visibility !== "hidden" &&
                  rect.width > 0 &&
                  rect.height > 0,
              };
            })
            .filter((target) => target.isVisible && (target.width < 44 || target.height < 44));
        });

        expect(smallTargets, `${route} has undersized mobile targets at ${width}px`).toEqual([]);
      }
    });
  }

  test("keeps the auth preview from sitting under the dialog at the reported wide ratio", async ({ page }) => {
    await page.setViewportSize({ width: 1180, height: 800 });
    await page.goto("/login");
    await expect(page.locator(".auth-card")).toBeVisible();
    await expect(page.locator(".auth-preview-content")).toBeVisible();

    const layout = await page.evaluate(() => {
      const card = document.querySelector<HTMLElement>(".auth-card")?.getBoundingClientRect();
      const preview = document.querySelector<HTMLElement>(".auth-preview-content")?.getBoundingClientRect();
      if (!card || !preview) return null;

      return {
        overlap: Math.max(0, Math.min(card.right, preview.right) - Math.max(card.left, preview.left)),
        overflow: document.documentElement.scrollWidth - document.documentElement.clientWidth,
      };
    });

    expect(layout).not.toBeNull();
    expect(layout?.overlap).toBe(0);
    expect(layout?.overflow).toBe(0);
  });

  test("keeps the desktop auth split centered on wide screens", async ({ page }) => {
    await page.setViewportSize({ width: 2048, height: 1000 });
    await page.goto("/login");
    await expect(page.locator(".auth-card")).toBeVisible();
    await expect(page.locator(".auth-preview-content")).toBeVisible();

    const layout = await page.evaluate(() => {
      const card = document.querySelector<HTMLElement>(".auth-card")?.getBoundingClientRect();
      const preview = document.querySelector<HTMLElement>(".auth-preview-content")?.getBoundingClientRect();
      if (!card || !preview) return null;

      const viewportCenter = document.documentElement.clientWidth / 2;
      const splitCenter = (preview.left + card.right) / 2;

      return {
        splitCenterOffset: Math.abs(splitCenter - viewportCenter),
        cardRightGap: document.documentElement.clientWidth - card.right,
        overflow: document.documentElement.scrollWidth - document.documentElement.clientWidth,
      };
    });

    expect(layout).not.toBeNull();
    expect(layout?.splitCenterOffset).toBeLessThanOrEqual(80);
    expect(layout?.cardRightGap).toBeGreaterThan(180);
    expect(layout?.overflow).toBe(0);
  });

  test("uses the compact mobile ecommerce header pattern", async ({ page }) => {
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto("/");

    await expect(page.locator(".mobile-menu-button")).toBeVisible();
    await expect(page.getByLabel("Điều hướng chính")).toBeHidden();
    await page.locator(".mobile-menu-button").click();
    await expect(page.getByLabel("Menu mobile").getByRole("link", { name: "Khám phá" })).toBeVisible();
    await expect(page.getByLabel("Menu mobile").getByRole("link", { name: "Tất cả sách" })).toBeVisible();
    await expect(page.locator(".header-actions .header-action").first()).toBeVisible();
    await expect(page.locator(".header-actions .header-login")).toBeHidden();
    await expect(page.locator(".header-actions .header-register")).toBeHidden();
    await expect(page.locator(".mobile-join-row .header-login")).toBeVisible();
    await expect(page.locator(".mobile-join-row .header-register")).toBeVisible();
  });

  test("keeps the mobile footer compact", async ({ page }) => {
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto("/");

    const footer = page.getByRole("contentinfo");
    await expect(footer).toBeVisible();

    const layout = await footer.evaluate((element) => {
      const rect = element.getBoundingClientRect();
      const grid = element.querySelector<HTMLElement>(".site-footer-grid");
      return {
        height: Math.round(rect.height),
        columns: grid ? getComputedStyle(grid).gridTemplateColumns.split(" ").length : 0,
      };
    });

    expect(layout.columns).toBe(2);
    expect(layout.height).toBeLessThanOrEqual(460);
  });

  test("keeps authenticated mobile actions out of the compact heart slot", async ({ page }) => {
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto("/");

    const layout = await page.evaluate(() => {
      const actions = document.querySelector<HTMLElement>(".header-actions");
      const header = document.querySelector<HTMLElement>(".site-header");
      if (!actions || !header) return null;

      actions.innerHTML = `
        <a class="header-action" href="/sach" aria-label="Yêu thích">Yêu thích</a>
        <a class="header-action" href="/account" aria-label="Tài khoản">Tài khoản</a>
        <a class="header-action" href="/gio-hang" aria-label="Giỏ thuê">Giỏ thuê</a>
        <form><button class="header-logout" type="button">Đăng xuất</button></form>
      `;
      document.querySelector(".mobile-join-row")?.remove();
      header.insertAdjacentHTML(
        "beforeend",
        `<div class="mobile-user-row">
          <a href="/account">Tài khoản</a>
          <a href="/gio-hang">Giỏ thuê</a>
          <form><button class="header-logout" type="button">Đăng xuất</button></form>
        </div>`,
      );

      const visibleHeaderActions = [...actions.querySelectorAll<HTMLElement>(".header-action, form")]
        .filter((element) => getComputedStyle(element).display !== "none").length;

      return {
        visibleHeaderActions,
        userRowDisplay: getComputedStyle(document.querySelector<HTMLElement>(".mobile-user-row")!).display,
        overflow: document.documentElement.scrollWidth - document.documentElement.clientWidth,
      };
    });

    expect(layout).toEqual({
      visibleHeaderActions: 1,
      userRowDisplay: "grid",
      overflow: 0,
    });
  });
});
