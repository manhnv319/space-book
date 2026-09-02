import { Suspense } from "react";
import Link from "next/link";
import { redirect } from "next/navigation";

import { OrderCard } from "@/components/account/order-card";
import { OrderListSkeleton } from "@/components/account/order-list-skeleton";
import { OrderTabs } from "@/components/account/order-tabs";
import { getCurrentUser } from "@/lib/bff/current-user";
import { tabByKey } from "@/lib/orders/status";
import { getMyOrdersByStatuses, getMyOrderSummary } from "@/lib/services/checkout-service";
import { Pagination } from "@/components/ui/pagination";

export const metadata = { title: "Đơn hàng của tôi" };

const PAGE_SIZE = 10;

interface OrderHistoryPageProps {
  searchParams: Promise<{ tab?: string; page?: string }>;
}

/**
 * Danh sách đơn, tách khỏi phần khung.
 *
 * Chỉ phần này nằm trong Suspense — hàng tab và tiêu đề vẫn đứng yên khi đổi
 * tab, nên không còn hiện tượng cả trang trắng đi rồi vẽ lại.
 */
async function OrderList({ tabKey, page }: { tabKey: string; page: number }) {
  const tab = tabByKey(tabKey);
  const orders = await getMyOrdersByStatuses(tab.statuses, page, PAGE_SIZE).catch(() => null);

  if (orders === null) {
    return <p className="form-status" role="alert">Không tải được danh sách đơn hàng.</p>;
  }

  const query = (target: number) => {
    const params = new URLSearchParams();
    if (tab.key !== "all") params.set("tab", tab.key);
    if (target > 0) params.set("page", String(target));
    const search = params.toString();
    return search ? `/account/don-hang?${search}` : "/account/don-hang";
  };

  if (orders.content.length === 0) {
    return (
      <div className="account-empty">
        <p>{tab.key === "all" ? "Bạn chưa có đơn hàng nào." : `Không có đơn nào ở mục "${tab.label}".`}</p>
        <Link className="button" href="/sach">Bắt đầu chọn sách</Link>
        <Pagination currentPage={page} totalPages={orders.totalPages} hrefForPage={query} ariaLabel="Phân trang đơn hàng" />
      </div>
    );
  }

  return (
    <>
      <ul className="order-list">
        {orders.content.map((order) => <OrderCard key={order.id} order={order} />)}
      </ul>

      <Pagination currentPage={page} totalPages={orders.totalPages} hrefForPage={query} ariaLabel="Phân trang đơn hàng" />
    </>
  );
}

export default async function OrderHistoryPage({ searchParams }: OrderHistoryPageProps) {
  const user = await getCurrentUser();
  if (!user) redirect("/login?next=%2Faccount%2Fdon-hang");

  const { tab: tabKey, page: pageParam } = await searchParams;
  const tab = tabByKey(tabKey);
  const page = /^\d+$/.test(pageParam ?? "") ? Number(pageParam) : 0;

  // Số đếm lấy ngoài Suspense: nó rẻ (một GROUP BY) và cần có ngay cùng hàng tab.
  const counts = await getMyOrderSummary();

  return (
    <section className="account-page">
      <h1>Đơn hàng của tôi</h1>
      <OrderTabs current={tab.key} counts={counts} />

      {/* key đổi theo tab/trang để Suspense hiện lại khung chờ ở mỗi lần chuyển */}
      <Suspense key={`${tab.key}-${page}`} fallback={<OrderListSkeleton />}>
        <OrderList tabKey={tab.key} page={page} />
      </Suspense>
    </section>
  );
}
