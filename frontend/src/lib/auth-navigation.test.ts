import { describe, expect, it } from "vitest";

import { safeAuthDestination } from "@/lib/auth-navigation";

describe("safeAuthDestination", () => {
  it("returns default /account when value is missing", () => {
    expect(safeAuthDestination(null)).toBe("/account");
    expect(safeAuthDestination(undefined)).toBe("/account");
  });

  it("returns default when value is not a string (File from FormData)", () => {
    const file = new File(["x"], "x.txt");
    expect(safeAuthDestination(file)).toBe("/account");
  });

  it("returns default when value does not start with /", () => {
    expect(safeAuthDestination("account")).toBe("/account");
    expect(safeAuthDestination("https://evil.example.com/")).toBe("/account");
  });

  it("rejects protocol-relative URLs (open redirect via //host)", () => {
    expect(safeAuthDestination("//evil.example.com")).toBe("/account");
    expect(safeAuthDestination("//evil.example.com/steal")).toBe("/account");
  });

  it("rejects a backslash-prefixed path used to smuggle a different host (WHATWG treats \\ as /)", () => {
    expect(safeAuthDestination("/\\evil.example.com")).toBe("/account");
  });

  it("keeps a same-origin relative path with query and hash intact", () => {
    expect(safeAuthDestination("/gio-hang")).toBe("/gio-hang");
    expect(safeAuthDestination("/admin/bai-viet?status=DRAFT#top")).toBe("/admin/bai-viet?status=DRAFT#top");
  });

  it("tolerates a lone % without throwing, still same-origin so path passes through", () => {
    expect(safeAuthDestination("/%")).toBe("/%");
  });
});
