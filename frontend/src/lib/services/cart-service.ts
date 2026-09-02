import "server-only";

import { readGuestCart } from "@/lib/bff/guest-cart-cookie";
import { apiRead } from "@/lib/bff/server-fetch";
import { getCurrentUser } from "@/lib/bff/current-user";
import { countGuestItems } from "@/lib/cart/guest-cart";
import type { CartResponse } from "@/lib/types/cart";

/** Chỉ gọi khi đã đăng nhập — BE trả 401 nếu chưa có `currentUserId`. */
export async function getCart(): Promise<CartResponse> {
  return apiRead<CartResponse>("/api/v1/cart");
}

/**
 * Đã login: tổng từ BE (`GET /cart`). Chưa login: đếm cookie giỏ khách.
 * Bọc try/catch — badge số lượng ở header không được làm sập trang khi BE lỗi.
 */
export async function getCartItemCount(): Promise<number> {
  try {
    const user = await getCurrentUser();
    if (user) return (await getCart()).totalItems;
    return countGuestItems(await readGuestCart());
  } catch (error) {
    console.error("Failed to resolve cart item count:", error);
    return 0;
  }
}
