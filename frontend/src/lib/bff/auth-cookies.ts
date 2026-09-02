import "server-only";

import { cookies } from "next/headers";

import { AUTH_COOKIE_NAMES } from "@/lib/bff/auth-cookie-names";
import {
  ACCESS_TOKEN_MAX_AGE_SECONDS,
  authCookieOptions,
  REFRESH_TOKEN_MAX_AGE_SECONDS,
} from "@/lib/bff/auth-cookie-options";
import type { RefreshedTokens, SessionTokens } from "@/lib/bff/request-flow";

export type { SessionTokens } from "@/lib/bff/request-flow";

/** Read-only: Server Components can safely retrieve the current request session. */
export async function readSessionTokens(): Promise<SessionTokens> {
  const store = await cookies();
  return {
    accessToken: store.get(AUTH_COOKIE_NAMES.access)?.value,
    refreshToken: store.get(AUTH_COOKIE_NAMES.refresh)?.value,
  };
}

/** Call only from a Server Action or Route Handler. */
export async function persistSessionTokens(tokens: RefreshedTokens): Promise<void> {
  const store = await cookies();
  store.set(AUTH_COOKIE_NAMES.access, tokens.accessToken, {
    ...authCookieOptions,
    maxAge: ACCESS_TOKEN_MAX_AGE_SECONDS,
  });
  store.set(AUTH_COOKIE_NAMES.refresh, tokens.refreshToken, {
    ...authCookieOptions,
    maxAge: REFRESH_TOKEN_MAX_AGE_SECONDS,
  });
}

/** Call only from a Server Action or Route Handler. */
export async function clearSessionTokens(): Promise<void> {
  const store = await cookies();
  store.set(AUTH_COOKIE_NAMES.access, "", { ...authCookieOptions, maxAge: 0 });
  store.set(AUTH_COOKIE_NAMES.refresh, "", { ...authCookieOptions, maxAge: 0 });
}
