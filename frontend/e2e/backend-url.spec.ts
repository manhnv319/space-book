import { expect, test } from "@playwright/test";

import { resolveBackendUrl } from "../src/lib/bff/backend-url";

const BACKEND_ORIGIN = "https://api.example.test";

test("BFF backend URLs stay on the configured origin", () => {
  const url = resolveBackendUrl("/api/v1/books", BACKEND_ORIGIN);

  expect(url.href).toBe("https://api.example.test/api/v1/books");
});

test("BFF rejects absolute and protocol-relative backend URLs", () => {
  expect(() => resolveBackendUrl("https://attacker.example/steal", BACKEND_ORIGIN)).toThrow();
  expect(() => resolveBackendUrl("//attacker.example/steal", BACKEND_ORIGIN)).toThrow();
});
