import "server-only";

import { readSessionTokens } from "@/lib/bff/auth-cookies";
import { backendRequest } from "@/lib/bff/backend-request";
import { parseBackendResponse } from "@/lib/bff/envelope";
import { createReadAdapter } from "@/lib/bff/request-flow";

type ReadAdapter = ReturnType<typeof createReadAdapter>;
type ReadAdapterDependencies = Parameters<typeof createReadAdapter>[0];

const productionReadAdapter = createReadAdapter({
  request: backendRequest,
  parse: parseBackendResponse,
  readSession: readSessionTokens,
});
let readAdapter: ReadAdapter = productionReadAdapter;

/** Read-only: the production RSC adapter has no persistence dependency. */
export async function apiRead<T>(path: string, init?: RequestInit): Promise<T> {
  return readAdapter<T>(path, init);
}

export function configureApiReadForTest(dependencies: ReadAdapterDependencies): void {
  if (process.env.NODE_ENV !== "test") throw new Error("Test adapter unavailable.");
  readAdapter = createReadAdapter(dependencies);
}

export function resetApiReadForTest(): void {
  if (process.env.NODE_ENV !== "test") throw new Error("Test adapter unavailable.");
  readAdapter = productionReadAdapter;
}
