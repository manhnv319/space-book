"use server";

import { executeAtActionBoundary } from "@/lib/bff/action-mutation-boundary";

/** Server Action boundary for future authenticated mutations. */
export async function executeAuthenticatedMutation<T>(
  path: string,
  init: RequestInit,
): Promise<T> {
  return executeAtActionBoundary<T>(path, init);
}
