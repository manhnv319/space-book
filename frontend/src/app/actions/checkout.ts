"use server";

import { redirect } from "next/navigation";
import { revalidatePath } from "next/cache";

import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";
import { BackendError } from "@/lib/bff/backend-error";
import { getCart } from "@/lib/services/cart-service";
import { getOrderDetail } from "@/lib/services/checkout-service";
import type { CartItemDetail } from "@/lib/types/cart";
import type { OrderSummary } from "@/lib/types/checkout";

export type CheckoutState = { status: "idle" | "error"; message: string };

/** Chỉ hỗ trợ chuyển khoản — xem `CheckoutPage`. */
const PAYMENT_METHOD = "BANK_TRANSFER";

/**
 * Dựng payload đơn hàng từ giỏ **đọc lại ở server**, không nhận item từ form.
 *
 * Nếu tin form, khách sửa được bookId/số lượng/kỳ thuê trong DevTools. Giá thì
 * BE tự tính nên không giả được, nhưng danh sách sách thì có. Đọc lại giỏ là
 * cách duy nhất để đơn khớp đúng những gì khách thật sự đã thêm.
 */
function toOrderItems(items: CartItemDetail[]) {
  return items.map((item) => ({
    bookId: item.bookId,
    itemType: item.itemType,
    quantity: item.quantity ?? 1,
    rentalTermValue: item.rentalTermValue ?? undefined,
    rentalTermUnit: item.rentalTermUnit ?? undefined,
  }));
}

function checkoutError(error: unknown): string {
  if (error instanceof BackendError) {
    if (error.status === 404) return "Không tìm thấy sách hoặc địa chỉ trong đơn.";
    if (error.status === 400) return "Có sách trong giỏ hiện không còn khả dụng. Vui lòng kiểm tra lại giỏ hàng.";
    if (error.status === 422) return "Có sách trong giỏ không đủ số lượng.";
  }
  return "Không thể tạo đơn hàng lúc này. Vui lòng thử lại.";
}

export async function placeOrderAction(_state: CheckoutState, data: FormData): Promise<CheckoutState> {
  const shippingAddressId = Number(data.get("shippingAddressId"));
  if (!Number.isInteger(shippingAddressId) || shippingAddressId <= 0) {
    return { status: "error", message: "Vui lòng chọn địa chỉ nhận hàng." };
  }

  const notes = String(data.get("notes") ?? "").trim().slice(0, 500);

  let orderId: number;
  try {
    const cart = await getCart();
    if (cart.items.length === 0) return { status: "error", message: "Giỏ hàng của bạn đang trống." };

    const order = await executeAuthenticatedMutation<OrderSummary>("/api/v1/orders", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({
        items: toOrderItems(cart.items),
        paymentMethod: PAYMENT_METHOD,
        shippingAddressId,
        notes: notes || undefined,
      }),
    });
    orderId = order.id;
  } catch (error) {
    console.error("Failed to place order:", error);
    return { status: "error", message: checkoutError(error) };
  }

  revalidatePath("/gio-hang");
  revalidatePath("/", "layout");
  // Ngoài try/catch: `redirect` hoạt động bằng cách ném lỗi, bắt lại sẽ thành
  // "không tạo được đơn" trong khi đơn đã tạo thành công.
  redirect(`/checkout/${orderId}`);
}

/**
 * Mua lại: đẩy sách của một đơn cũ trở lại giỏ.
 *
 * Đọc lại đơn từ máy chủ chứ không nhận danh sách sách từ form — nếu tin form,
 * khách sửa được `bookId` trong DevTools và "mua lại" thành đặt sách bất kỳ.
 *
 * Thêm **tuần tự**: `AddCartItemService` lấy-hoặc-tạo giỏ không nguyên tử, gọi
 * song song có thể tạo nhiều giỏ cho cùng một người.
 */
export async function reorderAction(_state: CheckoutState, data: FormData): Promise<CheckoutState> {
  const rawId = String(data.get("orderId") ?? "");
  if (!/^\d+$/.test(rawId)) return { status: "error", message: "Đơn hàng không hợp lệ." };

  let added = 0;
  let skipped = 0;
  try {
    const order = await getOrderDetail(Number(rawId));
    for (const item of order.items) {
      try {
        await executeAuthenticatedMutation("/api/v1/cart/items", {
          method: "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            bookId: item.bookId,
            itemType: item.itemType,
            quantity: item.quantity ?? 1,
            rentalTermValue: item.rentalTermValue ?? undefined,
            rentalTermUnit: item.rentalTermUnit ?? undefined,
          }),
        });
        added += 1;
      } catch (error) {
        // Sách hết hàng hoặc đã gỡ bán thì bỏ qua cuốn đó, vẫn thêm những cuốn
        // còn lại — huỷ cả lượt vì một cuốn là bắt khách làm lại từ đầu.
        console.error("Reorder skipped an item:", error);
        skipped += 1;
      }
    }
  } catch (error) {
    console.error("Failed to reorder:", error);
    return { status: "error", message: checkoutError(error) };
  }

  if (added === 0) {
    return { status: "error", message: "Không còn cuốn nào trong đơn này có thể mua lại." };
  }

  revalidatePath("/gio-hang");
  revalidatePath("/", "layout");
  if (skipped > 0) {
    return { status: "error", message: `Đã thêm ${added} cuốn vào giỏ, ${skipped} cuốn không còn khả dụng.` };
  }
  redirect("/gio-hang");
}
