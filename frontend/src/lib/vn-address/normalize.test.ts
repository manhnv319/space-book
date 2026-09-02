import { describe, expect, it } from "vitest";

import { matchesQuery, normalizeVietnamese } from "@/lib/vn-address/normalize";

describe("normalizeVietnamese", () => {
  it("strips tone and vowel marks", () => {
    expect(normalizeVietnamese("Hà Nội")).toBe("ha noi");
    expect(normalizeVietnamese("Đà Nẵng")).toBe("da nang");
    expect(normalizeVietnamese("Thừa Thiên Huế")).toBe("thua thien hue");
  });

  it("handles đ, which NFD does not decompose", () => {
    expect(normalizeVietnamese("đường")).toBe("duong");
    expect(normalizeVietnamese("ĐỒNG NAI")).toBe("dong nai");
  });

  it("leaves plain ascii alone", () => {
    expect(normalizeVietnamese("  Ha Noi ")).toBe("ha noi");
  });
});

describe("matchesQuery", () => {
  it("matches without diacritics", () => {
    expect(matchesQuery("Thành phố Hà Nội", "ha noi")).toBe(true);
    expect(matchesQuery("Thành phố Đà Nẵng", "da nang")).toBe(true);
  });

  it("matches with diacritics typed in full", () => {
    expect(matchesQuery("Thành phố Hà Nội", "Hà Nội")).toBe(true);
  });

  it("matches words that are not adjacent, so the prefix can be skipped", () => {
    expect(matchesQuery("Thành phố Hồ Chí Minh", "ho chi minh")).toBe(true);
    expect(matchesQuery("Thành phố Hồ Chí Minh", "minh thanh")).toBe(true);
  });

  it("rejects a query whose words are not all present", () => {
    expect(matchesQuery("Thành phố Hà Nội", "hai phong")).toBe(false);
    expect(matchesQuery("Phường Ba Đình", "ba dinh cau giay")).toBe(false);
  });

  it("treats an empty query as matching everything so the list stays open", () => {
    expect(matchesQuery("Thành phố Hà Nội", "")).toBe(true);
    expect(matchesQuery("Thành phố Hà Nội", "   ")).toBe(true);
  });
});
