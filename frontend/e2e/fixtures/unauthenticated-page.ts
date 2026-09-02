import { expect, test as base } from "@playwright/test";
import type { Page } from "@playwright/test";

type SmokeFixtures = { unauthenticatedPage: Page };

export const test = base.extend<SmokeFixtures>({
  unauthenticatedPage: async ({ page }, handoff) => {
    await page.context().clearCookies();
    await handoff(page);
  },
});

export { expect };
