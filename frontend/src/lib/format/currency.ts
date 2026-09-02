const VND = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" });

/**
 * Format a VND amount for display. Returns an em-dash placeholder when the
 * value is missing so callers never need to special-case null/undefined.
 */
export function formatVnd(value: number | null | undefined): string {
  return value == null ? "—" : VND.format(value);
}
