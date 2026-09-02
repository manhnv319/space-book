"use server";

import { revalidatePath } from "next/cache";

import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";
import { BackendError } from "@/lib/bff/backend-error";

export type SupportState = { status: "idle" | "error"; message: string };
const MAX_BODY = 2000;
const MAX_ATTACHMENTS = 3;
const MAX_IMAGE_BYTES = 10 * 1024 * 1024;
const IMAGE_TYPES = new Set(["image/jpeg", "image/png", "image/webp"]);

function parseMessage(data: FormData): { body: string; files: File[] } | { error: string } {
  const body = String(data.get("body") ?? "").trim();
  const files = data.getAll("attachments").filter((value): value is File => value instanceof File && value.size > 0);
  if (!body && files.length === 0) return { error: "Nhập nội dung hoặc chọn ảnh." };
  if (body.length > MAX_BODY) return { error: `Nội dung tối đa ${MAX_BODY} ký tự.` };
  if (files.length > MAX_ATTACHMENTS) return { error: `Mỗi tin nhắn tối đa ${MAX_ATTACHMENTS} ảnh.` };
  if (files.some((file) => file.size > MAX_IMAGE_BYTES || !IMAGE_TYPES.has(file.type))) return { error: "Ảnh phải là JPEG, PNG hoặc WebP và không quá 10 MB." };
  return { body, files };
}

function supportError(error: unknown): string {
  if (error instanceof BackendError && error.status === 400) return "Nội dung hoặc ảnh đính kèm không hợp lệ.";
  return "Không gửi được tin nhắn lúc này. Vui lòng thử lại.";
}

async function send(path: string, data: FormData): Promise<SupportState> {
  const parsed = parseMessage(data);
  if ("error" in parsed) return { status: "error", message: parsed.error };
  const payload = new FormData();
  payload.set("body", parsed.body);
  for (const file of parsed.files) payload.append("attachments", file);
  try {
    await executeAuthenticatedMutation(path, { method: "POST", body: payload });
    return { status: "idle", message: "" };
  } catch (error) {
    console.error("Failed to send support message:", error);
    return { status: "error", message: supportError(error) };
  }
}

export async function sendSupportMessageAction(_state: SupportState, data: FormData): Promise<SupportState> {
  const state = await send("/api/v1/support/conversation/messages", data);
  if (state.status === "idle") revalidatePath("/");
  return state;
}

export async function replySupportMessageAction(_state: SupportState, data: FormData): Promise<SupportState> {
  const rawId = String(data.get("conversationId") ?? "");
  if (!/^\d+$/.test(rawId)) return { status: "error", message: "Hội thoại không hợp lệ." };
  const state = await send(`/api/v1/support/conversations/${rawId}/messages`, data);
  if (state.status === "idle") {
    revalidatePath(`/admin/ho-tro/${rawId}`);
    revalidatePath("/admin/ho-tro");
    revalidatePath("/admin", "layout");
  }
  return state;
}
