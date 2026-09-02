const REFRESH_LEEWAY_SECONDS = 60;

type JwtPayload = { exp?: unknown };

function decodeBase64Url(value: string): string {
  const padded = value.replace(/-/g, "+").replace(/_/g, "/").padEnd(Math.ceil(value.length / 4) * 4, "=");
  const binary = atob(padded);
  return new TextDecoder().decode(Uint8Array.from(binary, (character) => character.charCodeAt(0)));
}

/** Use the JWT expiry only to decide whether the backend should refresh the session. */
export function accessTokenNeedsRefresh(token: string | undefined, nowSeconds = Math.floor(Date.now() / 1000)): boolean {
  if (!token) return true;

  try {
    const payload = JSON.parse(decodeBase64Url(token.split(".")[1] ?? "")) as JwtPayload;
    return typeof payload.exp !== "number" || payload.exp <= nowSeconds + REFRESH_LEEWAY_SECONDS;
  } catch {
    return true;
  }
}
