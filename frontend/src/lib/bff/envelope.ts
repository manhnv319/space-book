import "server-only";

import { BackendError } from "@/lib/bff/backend-error";

type BackendEnvelope<T> = {
  data?: T;
  message?: string | string[];
  statusCode?: number;
};

function isEnvelope(value: unknown): value is BackendEnvelope<unknown> {
  return typeof value === "object" && value !== null;
}

function errorMessage(payload: unknown, fallback: string): string {
  if (!isEnvelope(payload)) return fallback;
  const { message } = payload;
  if (Array.isArray(message)) return message.join(", ");
  return typeof message === "string" ? message : fallback;
}

export async function parseBackendResponse<T>(response: Response): Promise<T> {
  const contentType = response.headers.get("content-type") ?? "";
  const payload: unknown = contentType.includes("application/json")
    ? await response.json()
    : null;

  if (!response.ok) {
    throw new BackendError(response.status, errorMessage(payload, "Yêu cầu không thành công."));
  }

  if (isEnvelope(payload) && "data" in payload) return payload.data as T;
  return payload as T;
}
