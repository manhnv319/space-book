import type { CurrentUser } from "@/lib/bff/current-user";

/**
 * The only permission code the admin route group cares about (D8 — reused,
 * no new backend permission). Kept as a named constant so call sites never
 * hardcode the string.
 */
export const PERMISSION_MANAGE_CONTENT = "book:manage";

/**
 * Guards the bank-transfer reconciliation view. Distinct from
 * PERMISSION_MANAGE_CONTENT on purpose: someone who writes blog posts has no
 * reason to see which customers' money arrived. Matches the backend rule on
 * `GET /api/v1/bank-transfers/unmatched`.
 */
export const PERMISSION_HANDLE_PAYMENTS = "payment:refund";

/**
 * Guards the support queue. Reuses the permission the staff who already field
 * order questions hold, so no new grant had to be seeded server-side.
 */
export const PERMISSION_HANDLE_SUPPORT = "order:read:all";
export const PERMISSION_UPDATE_ORDERS = "order:update-status";
export const PERMISSION_READ_RENTALS = "rental:read:all";
export const PERMISSION_CHECKIN_RENTALS = "rental:checkin";

export const ADMIN_PERMISSIONS = [
  PERMISSION_MANAGE_CONTENT,
  PERMISSION_HANDLE_PAYMENTS,
  PERMISSION_HANDLE_SUPPORT,
  PERMISSION_UPDATE_ORDERS,
  PERMISSION_READ_RENTALS,
  PERMISSION_CHECKIN_RENTALS,
];

/**
 * Pure, no I/O — safe to unit test without touching cookies/BFF. This is a
 * UX-only check: the real gate is the backend's `hasAuthority(...)` rule in
 * `security-endpoints.yml`. Never treat a `true` here as authorization.
 */
export function hasPermission(user: CurrentUser | null, code: string): boolean {
  return Boolean(user?.permissions?.includes(code));
}
