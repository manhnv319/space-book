import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./e2e",
  forbidOnly: Boolean(process.env.CI),
  retries: process.env.CI ? 2 : 0,
  use: {
    baseURL: "http://127.0.0.1:3100",
    trace: "on-first-retry",
  },
  projects: [{ name: "chromium", use: { ...devices["Desktop Chrome"] } }],
  webServer: {
    command: "PORT=3100 node ./node_modules/next/dist/bin/next dev",
    env: { ...process.env, NODE_ENV: "development", NODE_OPTIONS: "" },
    url: "http://127.0.0.1:3100",
    reuseExistingServer: !process.env.CI,
  },
});
