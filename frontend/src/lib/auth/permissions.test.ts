import { describe, expect, it } from "vitest";

import { hasPermission, PERMISSION_MANAGE_CONTENT } from "@/lib/auth/permissions";
import type { CurrentUser } from "@/lib/bff/current-user";

function user(permissions: string[]): CurrentUser {
  return {
    id: 1,
    email: "reader@velstrongbook.local",
    username: "reader",
    roles: ["CUSTOMER"],
    permissions,
  };
}

describe("hasPermission", () => {
  it("returns false for a null user (not logged in)", () => {
    expect(hasPermission(null, PERMISSION_MANAGE_CONTENT)).toBe(false);
  });

  it("returns false when the permission is absent", () => {
    expect(hasPermission(user([]), PERMISSION_MANAGE_CONTENT)).toBe(false);
    expect(hasPermission(user(["order:manage"]), PERMISSION_MANAGE_CONTENT)).toBe(false);
  });

  it("returns true when the exact permission is present", () => {
    expect(hasPermission(user(["book:manage"]), PERMISSION_MANAGE_CONTENT)).toBe(true);
  });

  it("does not match by prefix/substring", () => {
    expect(hasPermission(user(["book:manage:extra"]), PERMISSION_MANAGE_CONTENT)).toBe(false);
  });
});
