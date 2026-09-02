import Link from "next/link";
import { unstable_rethrow } from "next/navigation";

import { AdminDashboardCharts } from "@/components/admin/admin-dashboard-charts";
import { getAdminBlogPosts } from "@/lib/services/admin-blog-service";
import { getUnmatchedTransfers } from "@/lib/services/admin-book-service";
import { getAdminOrders } from "@/lib/services/admin-order-service";
import { getAdminRentals } from "@/lib/services/admin-rental-service";
import { getBestsellers, getFeaturedBooks } from "@/lib/services/book-service";
import { getSupportQueue } from "@/lib/services/support-service";
import { getCurrentUser } from "@/lib/bff/current-user";
import { hasPermission, PERMISSION_HANDLE_PAYMENTS, PERMISSION_HANDLE_SUPPORT, PERMISSION_READ_RENTALS } from "@/lib/auth/permissions";

// `null` = the count failed to load (show "—"); a real 0 is shown as "0" —
// never fake a number, see phase constraint "chỉ hiện số liệu lấy được thật".
async function safeCount(load: (() => Promise<{ totalElements: number }>) | null): Promise<number | null> {
  if (!load) return null;
  try {
    return (await load()).totalElements;
  } catch (error) {
    unstable_rethrow(error);
    console.error("Admin dashboard count failed:", error);
    return null;
  }
}

export default async function AdminDashboardPage() {
  const user = await getCurrentUser();
  const canReadOrders = hasPermission(user, PERMISSION_HANDLE_SUPPORT);
  const canReadRentals = hasPermission(user, PERMISSION_READ_RENTALS);
  const canReadTransfers = hasPermission(user, PERMISSION_HANDLE_PAYMENTS);

  const [draftCount, publishedCount, featuredCount, bestsellerCount, orderCount, overdueCount, transferCount, supportCount] = await Promise.all([
    safeCount(() => getAdminBlogPosts("DRAFT", 0, 1)),
    safeCount(() => getAdminBlogPosts("PUBLISHED", 0, 1)),
    safeCount(() => getFeaturedBooks(0, 1)),
    safeCount(() => getBestsellers(0, 1)),
    safeCount(canReadOrders ? () => getAdminOrders({}, 0, 1) : null),
    safeCount(canReadRentals ? () => getAdminRentals(undefined, true, 0, 1) : null),
    safeCount(canReadTransfers ? () => getUnmatchedTransfers(0, 1) : null),
    safeCount(canReadOrders ? () => getSupportQueue(0, 1) : null),
  ]);

  return (
    <div className="admin-page">
      <div className="admin-page-header"><div><p className="eyebrow">Vận hành hôm nay</p><h1>Tổng quan</h1><p className="section-subtitle">Ưu tiên các việc cần xử lý trước, sau đó đến nội dung và trưng bày.</p></div><Link className="button" href="/admin/bai-viet/new">Viết bài mới</Link></div>

      <div className="admin-alert-grid">
        {transferCount !== null && <Link href="/admin/doi-soat" className="admin-alert-card"><strong>{transferCount}</strong><span>Giao dịch cần đối soát</span></Link>}
        {overdueCount !== null && <Link href="/admin/thue-sach?overdue=true" className="admin-alert-card"><strong>{overdueCount}</strong><span>Phiếu thuê quá hạn</span></Link>}
        {supportCount !== null && <Link href="/admin/ho-tro" className="admin-alert-card"><strong>{supportCount}</strong><span>Hội thoại hỗ trợ</span></Link>}
      </div>

      <div className="admin-stat-grid">
        <div className="admin-stat-card">
          <span className="admin-stat-value">{draftCount ?? "—"}</span>
          <span className="admin-stat-label">Bài viết nháp</span>
        </div>
        <Link href="/admin/don-hang" className="admin-stat-card admin-stat-card-link"><span className="admin-stat-value">{orderCount ?? "—"}</span><span className="admin-stat-label">Tổng đơn hàng</span></Link>
        <div className="admin-stat-card">
          <span className="admin-stat-value">{publishedCount ?? "—"}</span>
          <span className="admin-stat-label">Bài viết đã đăng</span>
        </div>
        <div className="admin-stat-card">
          <span className="admin-stat-value">{featuredCount ?? "—"}</span>
          <span className="admin-stat-label">Sách đang nổi bật</span>
        </div>
        <div className="admin-stat-card">
          <span className="admin-stat-value">{bestsellerCount ?? "—"}</span>
          <span className="admin-stat-label">Sách đang bán chạy</span>
        </div>
      </div>

      <AdminDashboardCharts
        bestsellerCount={bestsellerCount}
        draftCount={draftCount}
        featuredCount={featuredCount}
        overdueCount={overdueCount}
        publishedCount={publishedCount}
        supportCount={supportCount}
        transferCount={transferCount}
      />

      <div className="admin-quick-links">
        <Link className="button" href="/admin/bai-viet/new">
          Viết bài mới
        </Link>
        <Link className="button button-secondary" href="/admin/sach">
          Quản lý sách và kho
        </Link>
        <Link className="button button-secondary" href="/admin/thue-sach">Theo dõi thuê sách</Link>
      </div>
    </div>
  );
}
