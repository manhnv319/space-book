import { forceReturnRentalAction } from "@/app/actions/admin-operations";
import { Pagination } from "@/components/ui/pagination";
import { hasPermission, PERMISSION_CHECKIN_RENTALS, PERMISSION_READ_RENTALS } from "@/lib/auth/permissions";
import { getCurrentUser } from "@/lib/bff/current-user";
import { formatVnd } from "@/lib/format/currency";
import { getAdminRentals } from "@/lib/services/admin-rental-service";
import type { RentalStatus } from "@/lib/types/rental";

const STATUSES: RentalStatus[] = ["PENDING", "RENTED", "RETURNED", "LATE", "LOST", "CANCELLED"];
interface AdminRentalsPageProps { searchParams: Promise<{ page?: string; status?: RentalStatus; overdue?: string }> }

export default async function AdminRentalsPage({ searchParams }: AdminRentalsPageProps) {
  const params = await searchParams; const page = /^\d+$/.test(params.page ?? "") ? Number(params.page) : 0; const overdue = params.overdue === "true";
  const user = await getCurrentUser(); const canRead = hasPermission(user, PERMISSION_READ_RENTALS); const canCheckin = hasPermission(user, PERMISSION_CHECKIN_RENTALS);
  const rentals = canRead ? await getAdminRentals(params.status, overdue, page).catch(() => null) : null;
  const href = (target: number) => { const q = new URLSearchParams(); if (target) q.set("page", String(target)); if (params.status) q.set("status", params.status); if (overdue) q.set("overdue", "true"); return `/admin/thue-sach${q.size ? `?${q}` : ""}`; };
  return <section className="admin-page"><div><p className="eyebrow">Vận hành</p><h1>Thuê sách</h1><p className="section-subtitle">Theo dõi sách đang thuê, quá hạn và nhận trả tại quầy.</p></div><form className="admin-filter-form" action="/admin/thue-sach"><select name="status" defaultValue={params.status ?? ""}><option value="">Tất cả trạng thái</option>{STATUSES.map((status) => <option key={status}>{status}</option>)}</select><label><input type="checkbox" name="overdue" value="true" defaultChecked={overdue} /> Chỉ quá hạn</label><button className="button button-small button-secondary">Lọc</button></form>{rentals === null ? <p className="admin-empty">Không tải được danh sách thuê hoặc bạn không có quyền xem.</p> : <><div className="admin-table-scroll"><table className="admin-table"><thead><tr><th>Sách</th><th>Khách</th><th>Hạn trả</th><th>Tiền cọc</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>{rentals.content.map((rental) => <tr key={rental.id}><td><strong>{rental.bookTitle ?? `Bản sao #${rental.bookCopyId ?? "—"}`}</strong></td><td>#{rental.userId ?? "—"}</td><td>{rental.plannedReturnDate ?? "—"}{rental.lateDays ? <><br /><span className="admin-table-muted">Trễ {rental.lateDays} ngày</span></> : null}</td><td>{rental.depositAmount === null ? "—" : formatVnd(rental.depositAmount)}</td><td><span className="admin-status-badge">{rental.status}</span></td><td>{canCheckin && ["RENTED", "LATE"].includes(rental.status) ? <form action={forceReturnRentalAction}><input name="rentalId" type="hidden" value={rental.id} /><button className="button button-small button-secondary">Nhận trả</button></form> : "—"}</td></tr>)}</tbody></table></div><Pagination currentPage={page} totalPages={rentals.totalPages} hrefForPage={href} ariaLabel="Phân trang thuê sách quản trị" /></>}</section>;
}
