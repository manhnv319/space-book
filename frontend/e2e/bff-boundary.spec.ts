import { expect, test } from "@playwright/test";

import {
  createActionMutationAdapter,
  createReadAdapter,
  type RefreshedTokens,
} from "../src/lib/bff/request-flow";

async function parse<T>(response: Response): Promise<T> {
  if (!response.ok) throw new Error(`backend status ${response.status}`);
  return response.json() as Promise<T>;
}

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "content-type": "application/json" },
  });
}

test("RSC production adapter receives no persistence dependency", async () => {
  const read = createReadAdapter({
    parse,
    readSession: async () => ({ accessToken: "expired" }),
    request: async () => json({ message: "expired" }, 401),
  });

  await expect(read("/api/v1/books")).rejects.toThrow("backend status 401");
});

test("action adapter refreshes once, persists once, then retries once", async () => {
  const requests: Array<{ path: string; accessToken?: string }> = [];
  const persisted: RefreshedTokens[] = [];
  const mutate = createActionMutationAdapter({
    parse,
    persistSession: async (tokens) => { persisted.push(tokens); },
    readSession: async () => ({ accessToken: "expired", refreshToken: "refresh" }),
    request: async (path, init) => {
      requests.push({ path, accessToken: init?.accessToken });
      if (path === "/api/v1/auth/refresh") return json({ accessToken: "fresh", refreshToken: "rotated" });
      return requests.length === 1 ? json({ message: "expired" }, 401) : json({ ok: true });
    },
  });

  await expect(mutate<{ ok: boolean }>("/api/v1/cart", { method: "GET" })).resolves.toEqual({ ok: true });
  expect(persisted).toEqual([{ accessToken: "fresh", refreshToken: "rotated" }]);
  expect(requests).toEqual([
    { path: "/api/v1/cart", accessToken: "expired" },
    { path: "/api/v1/auth/refresh", accessToken: undefined },
    { path: "/api/v1/cart", accessToken: "fresh" },
  ]);
});

test("unsafe POST without idempotency key neither refreshes nor replays", async () => {
  const requests: string[] = [];
  let persisted = 0;
  const mutate = createActionMutationAdapter({
    parse,
    persistSession: async () => { persisted += 1; },
    readSession: async () => ({ accessToken: "expired", refreshToken: "refresh" }),
    request: async (path) => {
      requests.push(path);
      return json({ message: "expired" }, 401);
    },
  });

  await expect(mutate("/api/v1/cart", { method: "POST" })).rejects.toThrow("backend status 401");
  expect(requests).toEqual(["/api/v1/cart"]);
  expect(persisted).toBe(0);
});

test("idempotent unsafe mutation refreshes once and does not refresh after retry 401", async () => {
  const requests: string[] = [];
  let persisted = 0;
  let originalAttempts = 0;
  const mutate = createActionMutationAdapter({
    parse,
    persistSession: async () => { persisted += 1; },
    readSession: async () => ({ accessToken: "expired", refreshToken: "refresh" }),
    request: async (path) => {
      requests.push(path);
      if (path === "/api/v1/auth/refresh") return json({ accessToken: "fresh", refreshToken: "rotated" });
      originalAttempts += 1;
      return json({ message: "still unauthorized" }, 401);
    },
  });

  await expect(
    mutate("/api/v1/cart", { method: "POST", headers: { "idempotency-key": "cart-1" } }),
  ).rejects.toThrow("backend status 401");
  expect(originalAttempts).toBe(2);
  expect(requests).toEqual(["/api/v1/cart", "/api/v1/auth/refresh", "/api/v1/cart"]);
  expect(persisted).toBe(1);
});

test("refresh failure does not persist or retry the original request", async () => {
  let originalRequests = 0;
  let persisted = 0;
  const mutate = createActionMutationAdapter({
    parse,
    persistSession: async () => { persisted += 1; },
    readSession: async () => ({ accessToken: "expired", refreshToken: "refresh" }),
    request: async (path) => {
      if (path === "/api/v1/auth/refresh") return json({ token: "secret" }, 401);
      originalRequests += 1;
      return json({ message: "expired" }, 401);
    },
  });

  await expect(mutate("/api/v1/cart", { method: "GET" })).rejects.toThrow("backend status 401");
  expect(persisted).toBe(0);
  expect(originalRequests).toBe(1);
});
