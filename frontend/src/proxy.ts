import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

import { accessTokenNeedsRefresh } from "@/lib/auth/jwt-expiry";
import { AUTH_COOKIE_NAMES } from "@/lib/bff/auth-cookie-names";
import {
  ACCESS_TOKEN_MAX_AGE_SECONDS,
  authCookieOptions,
  REFRESH_TOKEN_MAX_AGE_SECONDS,
} from "@/lib/bff/auth-cookie-options";
import { resolveBackendUrl } from "@/lib/bff/backend-url";

type RefreshedTokens = { accessToken: string; refreshToken: string };

function needsAuthentication(pathname: string): boolean {
  return pathname.startsWith("/account") || pathname.startsWith("/checkout") || pathname.startsWith("/admin");
}

function clearSession(response: NextResponse): NextResponse {
  response.cookies.set(AUTH_COOKIE_NAMES.access, "", { ...authCookieOptions, maxAge: 0 });
  response.cookies.set(AUTH_COOKIE_NAMES.refresh, "", { ...authCookieOptions, maxAge: 0 });
  return response;
}

function redirectToLogin(request: NextRequest): NextResponse {
  const loginUrl = new URL("/login", request.url);
  loginUrl.searchParams.set("next", `${request.nextUrl.pathname}${request.nextUrl.search}`);
  return clearSession(NextResponse.redirect(loginUrl));
}

function readTokens(payload: unknown): RefreshedTokens | null {
  const data = typeof payload === "object" && payload !== null && "data" in payload
    ? (payload as { data?: unknown }).data
    : payload;
  if (typeof data !== "object" || data === null) return null;
  const { accessToken, refreshToken } = data as Partial<RefreshedTokens>;
  return typeof accessToken === "string" && typeof refreshToken === "string"
    ? { accessToken, refreshToken }
    : null;
}

async function refreshSession(refreshToken: string | undefined): Promise<RefreshedTokens | null> {
  if (!refreshToken) return null;

  try {
    const response = await fetch(resolveBackendUrl("/api/v1/auth/refresh"), {
      method: "POST",
      headers: { accept: "application/json", "content-type": "application/json" },
      body: JSON.stringify({ refreshToken }),
      cache: "no-store",
    });
    return response.ok ? readTokens(await response.json()) : null;
  } catch {
    return null;
  }
}

function persistSession(response: NextResponse, tokens: RefreshedTokens): NextResponse {
  response.cookies.set(AUTH_COOKIE_NAMES.access, tokens.accessToken, {
    ...authCookieOptions,
    maxAge: ACCESS_TOKEN_MAX_AGE_SECONDS,
  });
  response.cookies.set(AUTH_COOKIE_NAMES.refresh, tokens.refreshToken, {
    ...authCookieOptions,
    maxAge: REFRESH_TOKEN_MAX_AGE_SECONDS,
  });
  return response;
}

/** Refresh an expired access cookie before the Server Component reads session state. */
export async function proxy(request: NextRequest): Promise<NextResponse> {
  const accessToken = request.cookies.get(AUTH_COOKIE_NAMES.access)?.value;
  const refreshToken = request.cookies.get(AUTH_COOKIE_NAMES.refresh)?.value;
  if (!accessToken && !refreshToken) {
    return needsAuthentication(request.nextUrl.pathname) ? redirectToLogin(request) : NextResponse.next();
  }

  if (!accessToken || accessTokenNeedsRefresh(accessToken)) {
    const tokens = await refreshSession(refreshToken);
    if (tokens) return persistSession(NextResponse.next(), tokens);
    if (needsAuthentication(request.nextUrl.pathname)) return redirectToLogin(request);
    return clearSession(NextResponse.next());
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico).*)"],
};
