import "server-only";

import { backendRequest } from "@/lib/bff/backend-request";
import { parseBackendResponse } from "@/lib/bff/envelope";
import type { RefreshedTokens } from "@/lib/bff/request-flow";

export type LoginCredentials = { username: string; password: string };
export type RegistrationInput = LoginCredentials & { email: string; fullname: string };

type AuthDependencies = {
  request: typeof backendRequest;
  parse: typeof parseBackendResponse;
  persistSession: (tokens: RefreshedTokens) => Promise<void>;
};

type TokenPayload = { accessToken?: string; refreshToken?: string };

function tokensFrom(payload: TokenPayload): RefreshedTokens {
  if (!payload.accessToken || !payload.refreshToken) throw new Error("Invalid token response.");
  return { accessToken: payload.accessToken, refreshToken: payload.refreshToken };
}

export function createAuthService({ request, parse, persistSession }: AuthDependencies) {
  async function exchange(credentials: LoginCredentials): Promise<void> {
    const response = await request("/api/v1/auth/token", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify(credentials),
    });
    await persistSession(tokensFrom(await parse<TokenPayload>(response)));
  }

  return {
    async login(credentials: LoginCredentials): Promise<void> {
      await exchange(credentials);
    },
    async register(input: RegistrationInput): Promise<void> {
      const response = await request("/api/v1/users/register", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify(input),
      });
      await parse<unknown>(response);
    },
  };
}
