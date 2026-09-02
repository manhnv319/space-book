import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { backendRequest } from "@/lib/bff/backend-request";

/**
 * The transfer QR only ever rendered a placeholder because this wrapper forced
 * `accept: application/json` onto every call, and the QR endpoint produces
 * image/png — the backend answered 406 and the customer got no code to scan.
 */
describe("backendRequest", () => {
  // Kiểu khai báo trên vi.fn để `mock.calls` có kiểu đúng mà không cần tham số giả.
  const fetchMock = vi.fn<(input: RequestInfo | URL, init?: RequestInit) => Promise<Response>>(
    async () => new Response(null, { status: 200 }));

  beforeEach(() => {
    process.env.BOOK_API_BASE_URL = "http://backend.test";
    vi.stubGlobal("fetch", fetchMock);
    fetchMock.mockClear();
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  function sentHeaders(): Headers {
    return fetchMock.mock.calls[0][1]?.headers as Headers;
  }

  it("defaults to json when the caller says nothing", async () => {
    await backendRequest("/api/v1/cart");
    expect(sentHeaders().get("accept")).toBe("application/json");
  });

  it("keeps an accept header the caller set", async () => {
    await backendRequest("/api/v1/payment/bank-transfer/1/qr", { headers: { accept: "image/png" } });
    expect(sentHeaders().get("accept")).toBe("image/png");
  });

  it("still attaches the bearer token", async () => {
    await backendRequest("/api/v1/cart", { accessToken: "token-123" });
    expect(sentHeaders().get("authorization")).toBe("Bearer token-123");
  });
});
