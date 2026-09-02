import { expect, test } from "@playwright/test";

import { executeAuthenticatedMutation } from "../src/app/actions/authenticated-mutation";
import {
  configureActionMutationForTest,
  resetActionMutationForTest,
} from "../src/lib/bff/action-mutation-boundary";
import {
  apiRead,
  configureApiReadForTest,
  resetApiReadForTest,
} from "../src/lib/bff/server-fetch";

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

test.afterEach(() => {
  resetApiReadForTest();
  resetActionMutationForTest();
});

test("actual apiRead export sends one 401 read and has no persistence binding", async () => {
  const paths: string[] = [];
  configureApiReadForTest({
    parse,
    readSession: async () => ({ accessToken: "expired", refreshToken: "refresh" }),
    request: async (path) => {
      paths.push(path);
      return json({ message: "expired" }, 401);
    },
  });

  await expect(apiRead("/api/v1/books")).rejects.toThrow("backend status 401");
  expect(paths).toEqual(["/api/v1/books"]);
});

test("actual action export persists once only through eligible refresh flow", async () => {
  const requests: string[] = [];
  let persisted = 0;
  configureActionMutationForTest({
    parse,
    persistSession: async () => { persisted += 1; },
    readSession: async () => ({ accessToken: "expired", refreshToken: "refresh" }),
    request: async (path) => {
      requests.push(path);
      if (path === "/api/v1/auth/refresh") return json({ accessToken: "fresh", refreshToken: "rotated" });
      return requests.length === 1 ? json({ message: "expired" }, 401) : json({ ok: true });
    },
  });

  await expect(
    executeAuthenticatedMutation<{ ok: boolean }>("/api/v1/cart", {
      method: "POST",
      headers: { "idempotency-key": "cart-1" },
    }),
  ).resolves.toEqual({ ok: true });
  expect(requests).toEqual(["/api/v1/cart", "/api/v1/auth/refresh", "/api/v1/cart"]);
  expect(persisted).toBe(1);
});
