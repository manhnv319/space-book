import { describe, expect, it } from "vitest";

import { formatVnd } from "@/lib/format/currency";

describe("formatVnd", () => {
  it("formats a positive amount as VND currency", () => {
    expect(formatVnd(150000)).toContain("150.000");
  });

  it("formats zero", () => {
    expect(formatVnd(0)).toContain("0");
  });

  it("returns an em-dash placeholder for null/undefined", () => {
    expect(formatVnd(null)).toBe("—");
    expect(formatVnd(undefined)).toBe("—");
  });
});
