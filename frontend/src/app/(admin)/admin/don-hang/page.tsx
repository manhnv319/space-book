import { Pagination } from "@/components/ui/pagination";
import { updateAdminOrderStatusAction } from "@/app/actions/admin-operations";
import { hasPermission, PERMISSION_HANDLE_SUPPORT, PERMISSION_UPDATE_ORDERS } from "@/lib/auth/permissions";
import { getCurrentUser } from "@/lib/bff/current-user";
import { formatVnd } from "@/lib/format/currency";
import { getAdminOrders } from "@/lib/services/admin-order-service";
import type { OrderStatus } from "@/lib/types/checkout";

const STATUSES: OrderStatus[] = ["PENDING", "CONFIRMED", "PROCESSING", "SHIPPING", "COMPLETED", "CANCELLED", "REFUNDED"];

interface AdminOrdersPageProps { searchParams: Promise<{ page?: string; status?: OrderStatus; paymentStatus?: string; q?: string }> }

export default async function AdminOrdersPage({ searchParams }: AdminOrdersPageProps) {
  const params = await searchParams;
  const page = /^\d+$/.test(params.page ?? "") ? Number(params.page) : 0;
  const user = await getCurrentUser();
  const canUpdate = hasPermission(user, PERMISSION_UPDATE_ORDERS);
  const canRead = canUpdate || hasPermission(user, PERMISSION_HANDLE_SUPPORT);
  const orders = canRead ? await getAdminOrders({ status: params.status, paymentStatus: params.paymentStatus, search: params.q }, page).catch(() => null) : null;
  const href = (target: number) => {
    const query = new URLSearchParams();
    if (target) query.set("page", String(target));
    if (params.status) query.set("status", params.status);
    if (params.paymentStatus) query.set("paymentStatus", params.paymentStatus);
    if (params.q) query.set("q", params.q);
    return `/admin/don-hang${query.size ? `?${query}` : ""}`;
  };

  return <section className="admin-page"><div className="admin-page-header"><div><p className="eyebrow">Vận hành</p><h1>Đơn hàng</h1><p className="section-subtitle">Theo dõi thanh toán và tiến độ xử lý đơn.</p></div></div>
    <form className="admin-filter-form" action="/admin/don-hang"><input name="q" placeholder="Mã đơn..." defaultValue={params.q} /><select name="status" defaultValue={params.status ?? ""}><option value="">Tất cả trạng thái</option>{STATUSES.map((status) => <option key={status}>{status}</option>)}</select><select name="paymentStatus" defaultValue={params.paymentStatus ?? ""}><option value="">Tất cả thanh toán</option><option>PAID</option><option>PENDING</option><option>FAILED</option><option>REFUNDED</option></select><button className="button button-small button-secondary">Lọc</button></form>
    {orders === null ? <p className="admin-empty">Không tải được đơn hàng hoặc bạn không có quyền xem.</p> : <><div className="admin-table-scroll"><table className="admin-table"><thead><tr><th>Mã đơn</th><th>Loại</th><th>Thanh toán</th><th>Tổng tiền</th><th>Trạng thái</th><th>Ngày tạo</th><th>Cập nhật</th></tr></thead><tbody>{orders.content.map((order) => <tr key={order.id}><td><strong>{order.orderCode}</strong><br /><span className="admin-table-muted">{order.items.map((item) => item.title).filter(Boolean).join(", ") || "Không có sách"}</span></td><td>{order.orderType}</td><td><span className="admin-status-badge">{order.paymentStatus}</span></td><td>{formatVnd(order.finalAmount)}</td><td><span className="admin-status-badge">{order.status}</span></td><td>{new Intl.DateTimeFormat("vi-VN", { dateStyle: "short" }).format(new Date(order.createdAt))}</td><td>{canUpdate ? <form action={updateAdminOrderStatusAction} className="admin-inline-form"><input name="orderId" type="hidden" value={order.id} /><select name="newStatus" defaultValue={order.status}>{STATUSES.map((status) => <option key={status}>{status}</option>)}</select><button className="button button-small button-secondary">Lưu</button></form> : "Chỉ xem"}</td></tr>)}</tbody></table></div><Pagination currentPage={page} totalPages={orders.totalPages} hrefForPage={href} ariaLabel="Phân trang đơn hàng quản trị" /></>}</section>;
}
