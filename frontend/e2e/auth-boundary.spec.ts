import { readFile } from "node:fs/promises";
import { join } from "node:path";

import { expect, test } from "@playwright/test";

import { safeAuthDestination } from "../src/lib/auth-navigation";
import {
  ACCESS_TOKEN_MAX_AGE_SECONDS,
  authCookieOptions,
  REFRESH_TOKEN_MAX_AGE_SECONDS,
} from "../src/lib/bff/auth-cookie-options";
import { createAuthService } from "../src/lib/bff/auth-service";
import { createActionMutationAdapter } from "../src/lib/bff/request-flow";

const projectRoot = join(__dirname, "..");

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "content-type": "application/json" },
  });
}

test("auth exchange stores tokens only through the server persistence boundary", async () => {
  const requests: Array<{ path: string; authorization: string | null; body: string | null }> = [];
  const stored: unknown[] = [];
  const auth = createAuthService({
    request: async (path, init) => {
      requests.push({
        path,
        authorization: new Headers(init?.headers).get("authorization"),
        body: typeof init?.body === "string" ? init.body : null,
      });
      return json({ accessToken: "access-secret", refreshToken: "refresh-secret" });
    },
    parse: async <T>(response: Response) => response.json() as Promise<T>,
    persistSession: async (tokens) => { stored.push(tokens); },
  });

  await auth.login({ username: "reader", password: "password1" });

  expect(requests).toEqual([{
    path: "/api/v1/auth/token",
    authorization: null,
    body: '{"username":"reader","password":"password1"}',
  }]);
  expect(stored).toEqual([{ accessToken: "access-secret", refreshToken: "refresh-secret" }]);
});

test("rejects external and protocol-relative return destinations", () => {
  expect(safeAuthDestination("/account/don-hang?tab=open")).toBe("/account/don-hang?tab=open");
  expect(safeAuthDestination("https://attacker.example/steal")).toBe("/account");
  expect(safeAuthDestination("//attacker.example/steal")).toBe("/account");
  expect(safeAuthDestination("javascript:alert(1)")).toBe("/account");
});

test("auth cookies are httpOnly, same-site lax, scoped, and expire with token policy", () => {
  expect(authCookieOptions).toMatchObject({ httpOnly: true, path: "/", sameSite: "lax" });
  expect(ACCESS_TOKEN_MAX_AGE_SECONDS).toBe(60 * 60);
  expect(REFRESH_TOKEN_MAX_AGE_SECONDS).toBe(7 * 24 * 60 * 60);
});

test("failed refresh clears the local session without replaying the original request", async () => {
  const paths: string[] = [];
  let cleared = 0;
  const mutate = createActionMutationAdapter({
    parse: async <T>(response: Response) => {
      if (!response.ok) throw new Error(`status ${response.status}`);
      return response.json() as Promise<T>;
    },
    persistSession: async () => {},
    clearSession: async () => { cleared += 1; },
    readSession: async () => ({ accessToken: "expired", refreshToken: "expired-refresh" }),
    request: async (path) => {
      paths.push(path);
      return json({ message: "expired" }, 401);
    },
  });

  await expect(mutate("/api/v1/users/me", { method: "GET" })).rejects.toThrow("status 401");
  expect(paths).toEqual(["/api/v1/users/me", "/api/v1/auth/refresh"]);
  expect(cleared).toBe(1);
});

test("a failed retry keeps successfully rotated session cookies", async () => {
  let cleared = 0;
  let originalAttempts = 0;
  const mutate = createActionMutationAdapter({
    parse: async <T>(response: Response) => {
      if (!response.ok) throw new Error(`status ${response.status}`);
      return response.json() as Promise<T>;
    },
    persistSession: async () => {},
    clearSession: async () => { cleared += 1; },
    readSession: async () => ({ accessToken: "expired", refreshToken: "valid-refresh" }),
    request: async (path) => {
      if (path === "/api/v1/auth/refresh") return json({ accessToken: "fresh", refreshToken: "rotated" });
      originalAttempts += 1;
      return json({ message: "backend unavailable" }, originalAttempts === 1 ? 401 : 500);
    },
  });

  await expect(mutate("/api/v1/users/me", { method: "GET" })).rejects.toThrow("status 500");
  expect(cleared).toBe(0);
});

test("client components contain no token storage or direct backend configuration", async () => {
  const clientSource = await readFile(join(projectRoot, "src/components/auth-form.tsx"), "utf8");
  const authAction = await readFile(join(projectRoot, "src/app/actions/auth.ts"), "utf8");

  expect(clientSource).not.toMatch(/localStorage|sessionStorage|BOOK_API_BASE_URL|authorization|accessToken/i);
  expect(clientSource).toContain("minLength={6}");
  expect(authAction).toContain("password.length < 6");
  expect(authAction).not.toMatch(/return\s+.*(?:accessToken|refreshToken)/i);
});

test("auth routes isolate an inert preview behind a focus-managed dialog", async ({ page }) => {
  for (const route of ["/login", "/register"]) {
    await page.goto(route);
    const dialog = page.getByRole("dialog", { name: route === "/register" ? "Tạo tài khoản" : "Đăng nhập" });
    const preview = page.getByTestId("auth-backdrop");

    await expect(page.getByRole("banner")).toBeVisible();
    await expect(page.getByRole("navigation")).toBeVisible();
    await expect(page.getByRole("contentinfo")).toBeVisible();
    await expect(page.getByRole("main")).toHaveCount(1);
    await expect(dialog).toHaveAttribute("aria-modal", "true");
    await expect(dialog).toBeFocused();
    await expect(preview).toHaveAttribute("aria-hidden", "true");
    await expect(preview).toHaveAttribute("inert", "");
    await expect(preview.locator("a, button, input")).toHaveCount(0);
    await expect(dialog.getByRole("img", { name: "Sách Nhà" })).toHaveCount(1);

    const homeLink = dialog.getByRole("link", { name: "Về trang chủ" });
    const switchLink = dialog.getByRole("link", { name: route === "/register" ? "Đăng nhập" : "Tạo tài khoản" });
    await homeLink.focus();
    await page.keyboard.press("Shift+Tab");
    await expect(switchLink).toBeFocused();
    await page.keyboard.press("Tab");
    await expect(homeLink).toBeFocused();
    await page.keyboard.press("Escape");
    await expect(page).toHaveURL(/\/$/);
  }
});

test("auth dialog closes on empty layer clicks and stays open for form clicks", async ({ page }) => {
  await page.goto("/login");

  const dialog = page.getByRole("dialog", { name: "Đăng nhập" });
  await expect(dialog).toBeVisible();

  await dialog.getByLabel("Tên đăng nhập/Email").click();
  await expect(page).toHaveURL(/\/login$/);
  await expect(dialog).toBeVisible();

  await page.locator(".auth-layer").click({ position: { x: 16, y: 16 } });
  await expect(page).toHaveURL(/\/$/);
});

test("protected skeleton pages recheck the backend session", async () => {
  const pages = await Promise.all(["account", "checkout"].map((route) => (
    readFile(join(projectRoot, `src/app/(site)/${route}/page.tsx`), "utf8")
  )));

  pages.forEach((source) => {
    expect(source).toContain("getCurrentUser");
    expect(source).toContain('redirect("/login?next=');
  });
});
