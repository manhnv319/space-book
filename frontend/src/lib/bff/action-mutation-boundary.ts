import "server-only";

import {
  clearSessionTokens,
  persistSessionTokens,
  readSessionTokens,
} from "@/lib/bff/auth-cookies";
import { backendRequest } from "@/lib/bff/backend-request";
import { parseBackendResponse } from "@/lib/bff/envelope";
import { createActionMutationAdapter } from "@/lib/bff/request-flow";

type MutationAdapter = ReturnType<typeof createActionMutationAdapter>;
type MutationAdapterDependencies = Parameters<typeof createActionMutationAdapter>[0];

const productionMutationAdapter = createActionMutationAdapter({
  request: backendRequest,
  parse: parseBackendResponse,
  readSession: readSessionTokens,
  persistSession: persistSessionTokens,
  clearSession: clearSessionTokens,
});
let mutationAdapter: MutationAdapter = productionMutationAdapter;

export async function executeAtActionBoundary<T>(path: string, init: RequestInit): Promise<T> {
  return mutationAdapter<T>(path, init);
}

export function configureActionMutationForTest(dependencies: MutationAdapterDependencies): void {
  if (process.env.NODE_ENV !== "test") throw new Error("Test adapter unavailable.");
  mutationAdapter = createActionMutationAdapter(dependencies);
}

export function resetActionMutationForTest(): void {
  if (process.env.NODE_ENV !== "test") throw new Error("Test adapter unavailable.");
  mutationAdapter = productionMutationAdapter;
}
