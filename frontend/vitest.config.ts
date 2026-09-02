import path from "node:path";

import { defineConfig } from "vitest/config";

/**
 * Chỉ chạy unit test thuần dưới src/**\/*.test.ts — tách biệt hoàn toàn khỏi
 * Playwright (thư mục e2e/*.spec.ts, chạy qua `npm run test:smoke`).
 */
export default defineConfig({
  resolve: {
    alias: { "@": path.resolve(__dirname, "src") },
  },
  // Modules under lib/bff import `server-only`, whose default export throws to
  // stop a client bundle pulling them in. Vitest runs them through its SSR
  // pipeline, so point that at the package's react-server entry — the same one
  // `test:smoke` selects via NODE_OPTIONS. Aliasing server-only away would work
  // too, but would also silence the guard these modules rely on.
  ssr: {
    resolve: { conditions: ["react-server", "node", "import"] },
  },
  test: {
    environment: "node",
    include: ["src/**/*.test.ts"],
    exclude: ["node_modules/**", "e2e/**", ".next/**"],
  },
});
