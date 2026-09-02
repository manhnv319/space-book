"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";

import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";
import { BackendError } from "@/lib/bff/backend-error";
import { getCurrentUser } from "@/lib/bff/current-user";
import { clearGuestCart, readGuestCart, writeGuestCart } from "@/lib/bff/guest-cart-cookie";
import { addGuestItem, removeGuestItem, updateGuestQuantity, type GuestCartItem } from "@/lib/cart/guest-cart";
import type { AddToCartInput, ItemType, RentalTermUnit } from "@/lib/types/cart";

export type ActionResult = { status: "ok" | "error"; message: string };

const ITEM_TYPES: ItemType[] = ["PURCHASE", "RENTAL"];
const RENTAL_TERM_UNITS: RentalTermUnit[] = ["DAY", "WEEK", "MONTH"];

function revalidateCartViews(): void {
  revalidatePath("/gio-hang");
  revalidatePath("/", "layout");
}

function cartMutationError(error: unknown): string {
  if (error instanceof BackendError) {
    if (error.status === 404) return "Sách không tồn tại.";
    // Backend rejects unavailable books with InvalidOperationException -> 400 and reports
    // stock shortfalls with InsufficientStockException -> 422. Its messages are English, so
    // map the status instead of forwarding them. Input is validated before the call, which
    // leaves the availability check as the only 400 this action can produce.
    if (error.status === 400) return "Sách hiện không khả dụng.";
    if (error.status === 422) return "Sách không đủ số lượng.";
  }
  return "Không thể cập nhật giỏ hàng lúc này.";
}

function parseAddToCartInput(data: FormData): { input: AddToCartInput } | { error: string } {
  const bookId = Number(data.get("bookId"));
  const itemType = String(data.get("itemType") ?? "");
  if (!Number.isInteger(bookId) || bookId <= 0) return { error: "Sách không hợp lệ." };
  if (!ITEM_TYPES.includes(itemType as ItemType)) return { error: "Loại giỏ hàng không hợp lệ." };

  const quantityRaw = data.get("quantity");
  const quantity = quantityRaw !== null && quantityRaw !== "" ? Number(quantityRaw) : undefined;
  if (quantity !== undefined && (!Number.isInteger(quantity) || quantity < 1 || quantity > 99)) {
    return { error: "Số lượng không hợp lệ." };
  }

  if (itemType === "RENTAL") {
    const rentalTermValue = Number(data.get("rentalTermValue"));
    const rentalTermUnit = String(data.get("rentalTermUnit") ?? "");
    if (!Number.isInteger(rentalTermValue) || rentalTermValue < 1) return { error: "Thời hạn thuê không hợp lệ." };
    if (!RENTAL_TERM_UNITS.includes(rentalTermUnit as RentalTermUnit)) {
      return { error: "Đơn vị thời hạn thuê không hợp lệ." };
    }
    return {
      input: { bookId, itemType: "RENTAL", quantity, rentalTermValue, rentalTermUnit: rentalTermUnit as RentalTermUnit },
    };
  }

  return { input: { bookId, itemType: "PURCHASE", quantity } };
}

export async function addToCartAction(_prev: ActionResult, data: FormData): Promise<ActionResult> {
  const parsed = parseAddToCartInput(data);
  if ("error" in parsed) return { status: "error", message: parsed.error };

  const user = await getCurrentUser();
  if (user) {
    try {
      await executeAuthenticatedMutation("/api/v1/cart/items", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify(parsed.input),
      });
    } catch (error) {
      return { status: "error", message: cartMutationError(error) };
    }
    revalidateCartViews();
    return { status: "ok", message: "Đã thêm vào giỏ hàng." };
  }

  const result = addGuestItem(await readGuestCart(), parsed.input as GuestCartItem);
  if (result.rejected) {
    return { status: "error", message: "Giỏ hàng khách đã đầy (tối đa 20 sản phẩm). Đăng nhập để tiếp tục thêm." };
  }
  await writeGuestCart(result.items);
  revalidateCartViews();
  return { status: "ok", message: "Đã thêm vào giỏ. Đăng nhập để thanh toán." };
}

/** Compact server-form action for product shelves. The detailed page keeps the
 * interactive action state; shelf quick-add goes directly to the cart so it
 * has no client state or duplicated price logic. */
export async function quickAddPurchaseAction(data: FormData): Promise<void> {
  const result = await addToCartAction({ status: "ok", message: "" }, data);
  redirect(result.status === "ok" ? "/gio-hang?added=1" : "/gio-hang?addError=1");
}

export async function updateCartItemAction(_prev: ActionResult, data: FormData): Promise<ActionResult> {
  const quantity = Number(data.get("quantity"));
  if (!Number.isInteger(quantity) || quantity < 1 || quantity > 99) {
    return { status: "error", message: "Số lượng không hợp lệ." };
  }

  const user = await getCurrentUser();
  if (user) {
    const itemId = Number(data.get("itemId"));
    if (!Number.isInteger(itemId) || itemId <= 0) return { status: "error", message: "Sản phẩm không hợp lệ." };
    try {
      await executeAuthenticatedMutation(`/api/v1/cart/items/${itemId}`, {
        method: "PUT",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ quantity }),
      });
    } catch (error) {
      return { status: "error", message: cartMutationError(error) };
    }
    revalidateCartViews();
    return { status: "ok", message: "Đã cập nhật số lượng." };
  }

  const index = Number(data.get("index"));
  if (!Number.isInteger(index) || index < 0) return { status: "error", message: "Sản phẩm không hợp lệ." };
  await writeGuestCart(updateGuestQuantity(await readGuestCart(), index, quantity));
  revalidateCartViews();
  return { status: "ok", message: "Đã cập nhật số lượng." };
}

export async function removeCartItemAction(_prev: ActionResult, data: FormData): Promise<ActionResult> {
  const user = await getCurrentUser();
  if (user) {
    const itemId = Number(data.get("itemId"));
    if (!Number.isInteger(itemId) || itemId <= 0) return { status: "error", message: "Sản phẩm không hợp lệ." };
    try {
      await executeAuthenticatedMutation(`/api/v1/cart/items/${itemId}`, { method: "DELETE" });
    } catch (error) {
      return { status: "error", message: cartMutationError(error) };
    }
    revalidateCartViews();
    return { status: "ok", message: "Đã xoá khỏi giỏ hàng." };
  }

  const index = Number(data.get("index"));
  if (!Number.isInteger(index) || index < 0) return { status: "error", message: "Sản phẩm không hợp lệ." };
  await writeGuestCart(removeGuestItem(await readGuestCart(), index));
  revalidateCartViews();
  return { status: "ok", message: "Đã xoá khỏi giỏ hàng." };
}

/**
 * Replay TUẦN TỰ (for...of + await) — CẤM Promise.all. `AddCartItemService.java`
 * dùng `findByUserId().orElseGet(save(...))` không atomic; gọi song song sẽ tạo
 * nhiều bản ghi `carts` cho cùng 1 user. Lỗi từng item chỉ log, không chặn login.
 * Luôn `clearGuestCart()` sau vòng lặp — kể cả khi có item lỗi, tránh replay vô hạn.
 */
export async function mergeGuestCartOnLogin(): Promise<void> {
  const items = await readGuestCart();
  for (const item of items) {
    try {
      await executeAuthenticatedMutation("/api/v1/cart/items", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify(item),
      });
    } catch (error) {
      console.error("Failed to merge guest cart item", {
        bookId: item.bookId,
        error: error instanceof Error ? error.message : String(error),
      });
    }
  }
  await clearGuestCart();
  revalidateCartViews();
}
