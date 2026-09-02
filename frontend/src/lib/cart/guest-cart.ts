/**
 * Logic thuần cho giỏ hàng khách — KHÔNG import "server-only" để unit test được (Vitest).
 * Chỉ lưu Ý ĐỊNH mua/thuê (bookId, itemType, quantity, term) — TUYỆT ĐỐI không lưu giá.
 * Giá luôn do BE tính khi merge (POST /cart/items) và khi FE gọi GET /cart.
 */
import type { AddToCartInput, ItemType, RentalTermUnit } from "@/lib/types/cart";

export type GuestCartItem = AddToCartInput;

export const MAX_GUEST_ITEMS = 20;
export const MAX_COOKIE_BYTES = 3500;

const ITEM_TYPES: ItemType[] = ["PURCHASE", "RENTAL"];
const RENTAL_TERM_UNITS: RentalTermUnit[] = ["DAY", "WEEK", "MONTH"];

export interface GuestCartMutationResult {
  items: GuestCartItem[];
  /** Vượt giới hạn item hoặc kích thước cookie — không âm thầm cắt bớt. */
  rejected?: "full";
}

function isPositiveInt(value: unknown, min = 1, max = Number.MAX_SAFE_INTEGER): value is number {
  return typeof value === "number" && Number.isInteger(value) && value >= min && value <= max;
}

/** Dữ liệu đọc từ cookie là untrusted input (client giữ) — validate nghiêm từng phần tử. */
function isValidItem(value: unknown): value is GuestCartItem {
  if (typeof value !== "object" || value === null) return false;
  const item = value as Record<string, unknown>;
  if (!isPositiveInt(item.bookId)) return false;
  if (typeof item.itemType !== "string" || !ITEM_TYPES.includes(item.itemType as ItemType)) return false;
  if (!isPositiveInt(item.quantity, 1, 99)) return false;
  if (item.itemType === "RENTAL") {
    if (!isPositiveInt(item.rentalTermValue)) return false;
    if (typeof item.rentalTermUnit !== "string" || !RENTAL_TERM_UNITS.includes(item.rentalTermUnit as RentalTermUnit)) {
      return false;
    }
  }
  return true;
}

function sameRentalTerm(a: GuestCartItem, b: GuestCartItem): boolean {
  return a.rentalTermValue === b.rentalTermValue && a.rentalTermUnit === b.rentalTermUnit;
}

function clampQuantity(quantity: number): number {
  return Math.min(99, Math.max(1, Math.trunc(quantity)));
}

function withinCookieBudget(items: GuestCartItem[]): boolean {
  return encodeGuestCart(items).length <= MAX_COOKIE_BYTES;
}

export function encodeGuestCart(items: GuestCartItem[]): string {
  return Buffer.from(JSON.stringify(items)).toString("base64url");
}

export function decodeGuestCart(raw: string | undefined): GuestCartItem[] {
  if (!raw) return [];
  try {
    const parsed: unknown = JSON.parse(Buffer.from(raw, "base64url").toString("utf-8"));
    if (!Array.isArray(parsed)) return [];
    return parsed.filter(isValidItem).slice(0, MAX_GUEST_ITEMS);
  } catch {
    return [];
  }
}

/**
 * Merge trùng: `PURCHASE` cùng `bookId` → cộng dồn quantity (cap 99).
 * `RENTAL` cùng `bookId` + cùng term → giữ nguyên (không cộng dồn, tránh nhân đôi ý định thuê).
 */
export function addGuestItem(items: GuestCartItem[], input: GuestCartItem): GuestCartMutationResult {
  const quantity = clampQuantity(input.quantity ?? 1);

  if (input.itemType === "PURCHASE") {
    const index = items.findIndex((item) => item.itemType === "PURCHASE" && item.bookId === input.bookId);
    if (index >= 0) {
      const next = [...items];
      const existing = next[index];
      next[index] = { ...existing, quantity: clampQuantity((existing.quantity ?? 1) + quantity) };
      return withinCookieBudget(next) ? { items: next } : { items, rejected: "full" };
    }
  } else {
    const exists = items.some(
      (item) => item.itemType === "RENTAL" && item.bookId === input.bookId && sameRentalTerm(item, input),
    );
    if (exists) return { items };
  }

  if (items.length >= MAX_GUEST_ITEMS) return { items, rejected: "full" };
  const next = [...items, { ...input, quantity }];
  return withinCookieBudget(next) ? { items: next } : { items, rejected: "full" };
}

export function updateGuestQuantity(items: GuestCartItem[], index: number, quantity: number): GuestCartItem[] {
  if (index < 0 || index >= items.length) return items;
  const next = [...items];
  next[index] = { ...next[index], quantity: clampQuantity(quantity) };
  return next;
}

export function removeGuestItem(items: GuestCartItem[], index: number): GuestCartItem[] {
  if (index < 0 || index >= items.length) return items;
  return items.filter((_, i) => i !== index);
}

export function countGuestItems(items: GuestCartItem[]): number {
  return items.reduce((sum, item) => sum + (item.quantity ?? 1), 0);
}
