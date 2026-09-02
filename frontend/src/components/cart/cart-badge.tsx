import { getCartItemCount } from "@/lib/services/cart-service";

/**
 * Server component: reads the cart count (logged-in via GET /cart, guest via
 * cookie — see cart-service.ts). `getCartItemCount` already swallows errors
 * and returns 0, so a backend outage never breaks the header.
 */
export async function CartBadge() {
  const count = await getCartItemCount();
  if (count <= 0) return null;

  return (
    <span className="cart-badge" aria-label={`${count} sản phẩm trong giỏ`}>
      {count > 99 ? "99+" : count}
    </span>
  );
}
