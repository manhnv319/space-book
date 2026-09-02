import { describe, expect, it } from "vitest";

import { resolveBackendUrl } from "@/lib/bff/backend-url";

const BASE = "http://localhost:8080";

describe("resolveBackendUrl", () => {
  it("throws 500 when baseUrl is not configured", () => {
    expect(() => resolveBackendUrl("/api/v1/books", "")).toThrow("Dịch vụ sách chưa được cấu hình.");
  });

  it("throws 500 when path does not start with /", () => {
    expect(() => resolveBackendUrl("api/v1/books", BASE)).toThrow("Đường dẫn BFF không hợp lệ.");
  });

  it("throws 500 for protocol-relative path (host smuggling via //)", () => {
    expect(() => resolveBackendUrl("//evil.example.com/steal", BASE)).toThrow("Đường dẫn BFF không hợp lệ.");
  });

  it("throws 500 when the resolved target escapes the backend origin", () => {
    expect(() => resolveBackendUrl("/\\evil.example.com", BASE)).toThrow("Đường dẫn BFF không hợp lệ.");
  });

  it("builds the expected absolute URL for a normal path", () => {
    const url = resolveBackendUrl("/api/v1/books?page=0", BASE);
    expect(url.toString()).toBe("http://localhost:8080/api/v1/books?page=0");
  });

  it("keeps the configured backend origin regardless of path depth", () => {
    const url = resolveBackendUrl("/api/v1/blog-posts/abc-slug", BASE);
    expect(url.origin).toBe("http://localhost:8080");
  });
});
