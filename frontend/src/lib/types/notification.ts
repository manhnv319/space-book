export type NotificationType = "CHAT" | "PAYMENT" | "ORDER" | "RENTAL" | "SYSTEM";

export type UserNotification = {
  id: number;
  type: NotificationType;
  title: string;
  body: string;
  targetPath: string;
  readAt: string | null;
  createdAt: string;
};

export type NotificationPage = { content: UserNotification[]; totalElements: number; totalPages: number; page: number; size: number };
