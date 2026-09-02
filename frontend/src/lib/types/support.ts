export type SupportSender = "CUSTOMER" | "STAFF";

export interface SupportMessageAttachment {
  id: number;
  imageUrl: string;
  originalName: string;
  contentType: string;
}

export interface SupportMessage {
  id: number;
  sender: SupportSender;
  body: string;
  sentAt: string;
  attachments: SupportMessageAttachment[];
}

export interface SupportConversation {
  id: number | null;
  userId: number;
  customerName: string;
  customerEmail: string;
  lastMessageAt: string | null;
  staffUnreadCount: number;
  customerUnreadCount: number;
  lastMessagePreview: string;
  messages: SupportMessage[];
}

export interface SupportConversationPage {
  content: SupportConversation[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
