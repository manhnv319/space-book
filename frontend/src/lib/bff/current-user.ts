import "server-only";

import { readSessionTokens } from "@/lib/bff/auth-cookies";
import { BackendError } from "@/lib/bff/backend-error";
import { apiRead } from "@/lib/bff/server-fetch";

export type CurrentUser = {
  id: number;
  email: string;
  fullname?: string;
  username?: string;
  phone?: string;
  birthday?: string;
  /** From `UserResponse.roles` — role codes, e.g. `["ADMIN"]`. */
  roles: string[];
  /** From `UserResponse.permissions` (Phase 02) — e.g. `["book:manage"]`. Never decoded from the JWT client-side (D13). */
  permissions: string[];
};

export async function getCurrentUser(): Promise<CurrentUser | null> {
  const session = await readSessionTokens();
  if (!session.accessToken) return null;

  try {
    return await apiRead<CurrentUser>("/api/v1/users/me");
  } catch (error) {
    if (error instanceof BackendError && [401, 403].includes(error.status)) return null;
    return null;
  }
}
