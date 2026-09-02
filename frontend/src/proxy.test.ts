import { afterEach, describe, expect, it, vi } from "vitest";
import { NextRequest } from "next/server";

import { AUTH_COOKIE_NAMES } from "@/lib/bff/auth-cookie-names";
import { proxy } from "@/proxy";

function tokenWithExpiry(exp: number): string {
  const payload = Buffer.from(JSON.stringify({ exp })).toString("base64url");
  return `header.${payload}.signature`;
}

function request(pathname: string, cookie: string): NextRequest {
  return new NextRequest(`https://books.example${pathname}`, { headers: { cookie } });
}

describe("proxy session refresh", () => {
  afterEach(() => {
    vi.unstubAllEnvs();
    vi.unstubAllGlobals();
  });

  it("refreshes an expired access session before a protected route runs", async () => {
    vi.stubEnv("BOOK_API_BASE_URL", "https://api.example");
    const fetchMock = vi.fn(async () => new Response(JSON.stringify({ data: {
      accessToken: "fresh-access", refreshToken: "fresh-refresh",
    } }), { headers: { "content-type": "application/json" } }));
    vi.stubGlobal("fetch", fetchMock);

    const response = await proxy(request("/account", `${AUTH_COOKIE_NAMES.access}=${tokenWithExpiry(1)}; ${AUTH_COOKIE_NAMES.refresh}=valid-refresh`));

    expect(fetchMock).toHaveBeenCalledWith(new URL("/api/v1/auth/refresh", "https://api.example"), expect.objectContaining({ method: "POST" }));
    expect(response.cookies.get(AUTH_COOKIE_NAMES.access)?.value).toBe("fresh-access");
    expect(response.cookies.get(AUTH_COOKIE_NAMES.refresh)?.value).toBe("fresh-refresh");
  });

  it("redirects protected routes only when a session cannot be refreshed", async () => {
    const response = await proxy(request("/account?tab=open", ""));

    expect(response.status).toBe(307);
    expect(response.headers.get("location")).toBe("https://books.example/login?next=%2Faccount%3Ftab%3Dopen");
  });

  it("keeps public routes public when stale session cookies cannot be refreshed", async () => {
    const response = await proxy(request("/", `${AUTH_COOKIE_NAMES.refresh}=expired-refresh`));

    expect(response.status).toBe(200);
    expect(response.cookies.get(AUTH_COOKIE_NAMES.refresh)?.value).toBe("");
  });

  it("does not emit cookie changes for anonymous public routes", async () => {
    const response = await proxy(request("/", ""));

    expect(response.status).toBe(200);
    expect(response.headers.get("set-cookie")).toBeNull();
  });
});
