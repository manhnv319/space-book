import { CartItemRow } from "@/components/cart/cart-item-row";
import { CartEmptyState } from "@/components/cart/cart-empty-state";
import { CartSummary } from "@/components/cart/cart-summary";
import { GuestCartView } from "@/components/cart/guest-cart-view";
import { readGuestCart } from "@/lib/bff/guest-cart-cookie";
import { getCurrentUser } from "@/lib/bff/current-user";
import { getBooksByIds } from "@/lib/services/book-service";
import { getCart } from "@/lib/services/cart-service";
import { unstable_rethrow } from "next/navigation";

export default async function CartPage({ searchParams }: { searchParams: Promise<{ added?: string; addError?: string }> }) {
  const notice = await searchParams;
  const user = await getCurrentUser();
  const statusNote = notice.added === "1" ? "Đã thêm sách vào giỏ hàng." : notice.addError === "1" ? "Không thể thêm sách vào giỏ hàng. Vui lòng thử lại." : null;

  if (!user) {
    const items = await readGuestCart();
    return <>{statusNote ? <p className="status-note" role="status">{statusNote}</p> : null}<GuestCartView items={items} /></>;
  }

  let cart;
  try {
    cart = await getCart();
  } catch (error) {
    unstable_rethrow(error);
    console.error("Failed to load cart:", error);
    return (
      <section className="page-section cart-error" role="alert">
        <p className="eyebrow">Giỏ hàng</p>
        <h1>Không tải được giỏ hàng</h1>
        <p className="lead">Đã có lỗi xảy ra khi kết nối máy chủ. Vui lòng thử lại sau.</p>
      </section>
    );
  }

  if (!cart.items.length) {
    return <>{statusNote ? <p className="status-note" role="status">{statusNote}</p> : null}<CartEmptyState /></>;
  }

  // Batch-fetch covers only — title/price for logged-in items already come
  // enriched from GET /cart (CartItemDetail has no imageUrl though).
  const books = await getBooksByIds(cart.items.map((item) => item.bookId));

  return (
    <div className="cart-page">
      <h1>Giỏ hàng</h1>
      {statusNote ? <p className="status-note" role="status">{statusNote}</p> : null}
      <div className="cart-layout">
        <ul className="cart-items">
          {cart.items.map((item) => (
            <CartItemRow key={item.id} item={item} coverUrl={books.get(item.bookId)?.imageUrl ?? null} />
          ))}
        </ul>
        <CartSummary cart={cart} />
      </div>
    </div>
  );
}
