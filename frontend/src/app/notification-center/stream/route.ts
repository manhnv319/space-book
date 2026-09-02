import { readSessionTokens } from "@/lib/bff/auth-cookies";
import { backendRequest } from "@/lib/bff/backend-request";

export async function GET() {
  const session = await readSessionTokens();
  if (!session.accessToken) return new Response(null, { status: 401 });
  const response = await backendRequest("/api/v1/notifications/stream", { accessToken: session.accessToken, headers: { accept: "text/event-stream" } });
  if (!response.ok || !response.body) return new Response(null, { status: response.status });
  return new Response(response.body, { headers: { "cache-control": "no-cache", "content-type": "text/event-stream", connection: "keep-alive" } });
}
