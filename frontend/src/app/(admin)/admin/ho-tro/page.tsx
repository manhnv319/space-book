import Link from "next/link";
import { redirect } from "next/navigation";

import { hasPermission, PERMISSION_HANDLE_SUPPORT } from "@/lib/auth/permissions";
import { getCurrentUser } from "@/lib/bff/current-user";
import { getSupportQueue } from "@/lib/services/support-service";

export const metadata = { title: "Hỗ trợ khách hàng" };
const TIME = new Intl.DateTimeFormat("vi-VN", { day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit" });

function formatMoment(value: string | null): string {
  if (!value) return "—";
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? "—" : TIME.format(parsed);
}

export default async function SupportQueuePage() {
  const user = await getCurrentUser();
  if (!hasPermission(user, PERMISSION_HANDLE_SUPPORT)) redirect("/admin");
  const queue = await getSupportQueue().catch(() => null);
  return <section className="admin-page">
    <div className="admin-section-header"><div><h1>Hỗ trợ khách hàng</h1><p className="section-subtitle">Mọi nhân viên có quyền đều thấy cùng một hàng đợi.</p></div></div>
    {queue === null ? <p className="form-status" role="alert">Không tải được hàng đợi hỗ trợ.</p>
      : queue.content.length === 0 ? <p className="admin-empty">Chưa có khách nào nhắn tới.</p>
        : <div className="admin-table-scroll"><table className="admin-table support-queue-table"><thead><tr><th scope="col">Khách</th><th scope="col">Tin gần nhất</th><th scope="col">Thời điểm</th><th scope="col">Trạng thái</th></tr></thead><tbody>
          {queue.content.map((conversation) => <tr key={conversation.id} className={conversation.staffUnreadCount > 0 ? "is-unread" : ""}>
            <td><Link href={`/admin/ho-tro/${conversation.id}`}><strong>{conversation.customerName}</strong><span>{conversation.customerEmail}</span></Link></td>
            <td>{conversation.lastMessagePreview || "Ảnh đính kèm"}</td><td>{formatMoment(conversation.lastMessageAt)}</td>
            <td>{conversation.staffUnreadCount > 0 ? <span className="admin-status-badge">{conversation.staffUnreadCount} chưa đọc</span> : <span className="admin-table-muted">Đã xem</span>}</td>
          </tr>)}
        </tbody></table></div>}
  </section>;
}
