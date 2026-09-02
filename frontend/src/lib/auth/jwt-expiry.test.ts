import { describe, expect, it } from "vitest";

import { accessTokenNeedsRefresh } from "@/lib/auth/jwt-expiry";

function tokenWithExpiry(exp: number): string {
  const payload = Buffer.from(JSON.stringify({ exp })).toString("base64url");
  return `header.${payload}.signature`;
}

describe("accessTokenNeedsRefresh", () => {
  it("refreshes missing, malformed, and expired tokens", () => {
    expect(accessTokenNeedsRefresh(undefined, 1_000)).toBe(true);
    expect(accessTokenNeedsRefresh("not-a-jwt", 1_000)).toBe(true);
    expect(accessTokenNeedsRefresh(tokenWithExpiry(999), 1_000)).toBe(true);
  });

  it("refreshes tokens that are about to expire", () => {
    expect(accessTokenNeedsRefresh(tokenWithExpiry(1_060), 1_000)).toBe(true);
    expect(accessTokenNeedsRefresh(tokenWithExpiry(1_061), 1_000)).toBe(false);
  });
});
