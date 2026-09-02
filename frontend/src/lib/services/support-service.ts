import "server-only";

import { apiRead } from "@/lib/bff/server-fetch";
import type { SupportConversation, SupportConversationPage } from "@/lib/types/support";

export async function getMyConversation(): Promise<SupportConversation | null> {
  return apiRead<SupportConversation>("/api/v1/support/conversation").catch(() => null);
}

export async function getSupportQueue(page = 0, size = 20): Promise<SupportConversationPage> {
  return apiRead<SupportConversationPage>(`/api/v1/support/conversations?page=${page}&size=${size}`);
}

export async function getConversation(id: number): Promise<SupportConversation> {
  return apiRead<SupportConversation>(`/api/v1/support/conversations/${id}`);
}

export async function getSupportUnreadCount(): Promise<number> {
  return apiRead<number>("/api/v1/support/conversations/unread-count");
}
