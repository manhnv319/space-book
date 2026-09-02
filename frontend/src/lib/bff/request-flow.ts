export type SessionTokens = {
  accessToken?: string;
  refreshToken?: string;
};

export type RefreshedTokens = {
  accessToken: string;
  refreshToken: string;
};

type BackendRequest = (
  path: string,
  init?: RequestInit & { accessToken?: string },
) => Promise<Response>;

type ParseResponse = <T>(response: Response) => Promise<T>;
type ReadSession = () => Promise<SessionTokens>;
type PersistSession = (tokens: RefreshedTokens) => Promise<void>;

type ReadDependencies = {
  request: BackendRequest;
  parse: ParseResponse;
  readSession: ReadSession;
};

type MutationDependencies = ReadDependencies & {
  persistSession: PersistSession;
  clearSession?: () => Promise<void>;
};

function canRetryAfterRefresh(init: RequestInit): boolean {
  const method = (init.method ?? "GET").toUpperCase();
  if (["GET", "HEAD", "OPTIONS"].includes(method)) return true;
  return new Headers(init.headers).has("idempotency-key");
}

export function createReadAdapter({ request, parse, readSession }: ReadDependencies) {
  return async function read<T>(path: string, init?: RequestInit): Promise<T> {
    const session = await readSession();
    const response = await request(path, { ...init, accessToken: session.accessToken });
    return parse<T>(response);
  };
}

/** This adapter cannot access cookies; only its caller may inject persistence. */
export function createActionMutationAdapter({
  request,
  parse,
  readSession,
  persistSession,
  clearSession,
}: MutationDependencies) {
  return async function mutate<T>(path: string, init: RequestInit): Promise<T> {
    const session = await readSession();
    let response = await request(path, { ...init, accessToken: session.accessToken });
    if (response.status !== 401 || !session.refreshToken || !canRetryAfterRefresh(init)) {
      return parse<T>(response);
    }

    let refreshed: RefreshedTokens;
    try {
      const refreshResponse = await request("/api/v1/auth/refresh", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ refreshToken: session.refreshToken }),
      });
      refreshed = await parse<RefreshedTokens>(refreshResponse);
      if (!refreshed.accessToken || !refreshed.refreshToken) throw new Error("Invalid refresh response.");
      await persistSession(refreshed);
    } catch (error) {
      await clearSession?.();
      throw error;
    }

    response = await request(path, { ...init, accessToken: refreshed.accessToken });
    return parse<T>(response);
  };
}
