import Link from "next/link";
import { notFound, redirect } from "next/navigation";

import { replySupportMessageAction } from "@/app/actions/support";
import { ChatComposer } from "@/components/support/chat-composer";
import { ChatThread } from "@/components/support/chat-thread";
import { hasPermission, PERMISSION_HANDLE_SUPPORT } from "@/lib/auth/permissions";
import { getCurrentUser } from "@/lib/bff/current-user";
import { getConversation } from "@/lib/services/support-service";

export const metadata = { title: "Hội thoại hỗ trợ" };

export default async function SupportConversationPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  if (!/^\d+$/.test(id)) notFound();
  const user = await getCurrentUser();
  if (!hasPermission(user, PERMISSION_HANDLE_SUPPORT)) redirect("/admin");
  const conversation = await getConversation(Number(id)).catch(() => null);
  if (!conversation) notFound();
  return <section className="admin-page admin-support-chat">
    <div className="admin-support-chat-header"><div className="admin-support-customer"><span aria-hidden="true" className="admin-support-customer-avatar">{conversation.customerName.slice(0, 1).toUpperCase()}</span><div><h1>{conversation.customerName}</h1><p className="section-subtitle">{conversation.customerEmail || `Người dùng #${conversation.userId}`}</p></div></div><Link href="/admin/ho-tro" className="text-link">&larr; Hàng đợi</Link></div>
    <div className="admin-support-chat-body"><ChatThread emptyHint="Hội thoại chưa có tin nhắn." messages={conversation.messages} participantName={conversation.customerName} showAvatars viewer="STAFF" /></div>
    <div className="admin-support-chat-composer"><ChatComposer action={replySupportMessageAction} conversationId={Number(id)} placeholder="Trả lời khách…" /></div>
  </section>;
}
