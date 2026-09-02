import { Skeleton } from "@/components/ui/skeleton";

export default function CartLoading() {
  return (
    <div className="cart-page" aria-busy="true" aria-live="polite">
      <Skeleton variant="text" width="12rem" />
      <div className="cart-layout">
        <ul className="cart-items">
          {Array.from({ length: 3 }).map((_, i) => (
            <li key={i} className="cart-item-row">
              <Skeleton variant="cover" className="cart-item-cover-skeleton" />
              <div className="cart-item-info">
                <Skeleton variant="text" width="70%" />
                <Skeleton variant="text" width="40%" />
              </div>
            </li>
          ))}
        </ul>
        <Skeleton variant="card" className="cart-summary-skeleton" />
      </div>
    </div>
  );
}
