import "server-only";

import { cookies } from "next/headers";

import { AUTH_COOKIE_NAMES } from "@/lib/bff/auth-cookie-names";
import { guestCartCookieOptions } from "@/lib/bff/auth-cookie-options";
import { decodeGuestCart, encodeGuestCart, type GuestCartItem } from "@/lib/cart/guest-cart";

/** Read-only: an toàn gọi từ Server Component lẫn Server Action. */
export async function readGuestCart(): Promise<GuestCartItem[]> {
  const store = await cookies();
  return decodeGuestCart(store.get(AUTH_COOKIE_NAMES.guestCart)?.value);
}

/** Call only from a Server Action or Route Handler. */
export async function writeGuestCart(items: GuestCartItem[]): Promise<void> {
  const store = await cookies();
  store.set(AUTH_COOKIE_NAMES.guestCart, encodeGuestCart(items), guestCartCookieOptions);
}

/** Call only from a Server Action or Route Handler. */
export async function clearGuestCart(): Promise<void> {
  const store = await cookies();
  store.delete(AUTH_COOKIE_NAMES.guestCart);
}
