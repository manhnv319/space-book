import "server-only";

import { resolveBackendUrl } from "@/lib/bff/backend-url";

type BackendRequestOptions = RequestInit & { accessToken?: string };

export async function backendRequest(
  path: string,
  { accessToken, headers, ...init }: BackendRequestOptions = {},
): Promise<Response> {
  const requestHeaders = new Headers(headers);
  // Default, not override: almost every endpoint answers JSON, but a caller that
  // asks for something else means it. Forcing application/json here made the
  // backend reject the transfer-QR endpoint with 406 Not Acceptable, because it
  // only produces image/png.
  if (!requestHeaders.has("accept")) requestHeaders.set("accept", "application/json");
  if (accessToken) requestHeaders.set("authorization", `Bearer ${accessToken}`);

  return fetch(resolveBackendUrl(path), {
    ...init,
    headers: requestHeaders,
    cache: "no-store",
  });
}
