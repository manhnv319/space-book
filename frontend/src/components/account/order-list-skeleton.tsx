import { Skeleton } from "@/components/ui/skeleton";

/**
 * Khung chờ khi đổi tab.
 *
 * Giữ đúng hình dáng thẻ đơn để vùng danh sách không co giãn khi dữ liệu về —
 * nội dung nhảy chỗ cũng khó chịu như màn hình trắng.
 */
export function OrderListSkeleton() {
  return (
    <ul className="order-list" aria-hidden="true">
      {[0, 1, 2].map((index) => (
        <li key={index} className="order-card is-loading">
          <div className="order-card-head">
            <Skeleton variant="text" width="8rem" />
            <Skeleton variant="text" width="6rem" />
          </div>
          <div className="order-card-items">
            <div className="order-line">
              <Skeleton variant="cover" className="order-line-cover" />
              <Skeleton variant="text" width="60%" />
            </div>
          </div>
        </li>
      ))}
    </ul>
  );
}
