import Link from "next/link";

import { OrderTabIcon } from "@/components/account/order-status-icons";
import { countForTab, ORDER_TABS, type OrderTab } from "@/lib/orders/status";

function href(tab: OrderTab): string {
  return tab.key === "all" ? "/account/don-hang" : `/account/don-hang?tab=${tab.key}`;
}

/**
 * Server component: hàng tab lọc.
 *
 * Nằm NGOÀI ranh giới Suspense của danh sách. Nhờ vậy khi bấm sang tab khác,
 * hàng tab giữ nguyên tại chỗ và chỉ vùng danh sách đổi — trước đây cả trang
 * trắng đi rồi vẽ lại nên nhìn như nhấp nháy.
 */
export function OrderTabs({ current, counts }: { current: string; counts: Record<string, number> }) {
  return (
    <nav className="order-tabs" aria-label="Lọc đơn theo trạng thái">
      {ORDER_TABS.map((tab) => {
        const count = countForTab(tab, counts);
        return (
          <Link
            key={tab.key}
            href={href(tab)}
            className={tab.key === current ? "order-tab is-current" : "order-tab"}
            aria-current={tab.key === current ? "page" : undefined}
          >
            <OrderTabIcon tabKey={tab.key} className="order-tab-icon" />
            <span className="order-tab-label">{tab.label}</span>
            {/* Bỏ hẳn khi bằng 0. Số đếm không đổi lúc bấm tab nên bề rộng vốn
                đã cố định — để lại ô rỗng chỉ tạo khoảng hở giữa các tab. */}
            {count > 0 && <span className="order-tab-count">{count}</span>}
          </Link>
        );
      })}
    </nav>
  );
}
