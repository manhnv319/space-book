import "server-only";

import { apiRead } from "@/lib/bff/server-fetch";
import type { RentalPage, RentalStatus } from "@/lib/types/rental";

/** `GET /api/v1/rentals/me` — chỉ trả phiếu của chính người gọi. */
export async function getMyRentals(status?: RentalStatus, page = 0, size = 20): Promise<RentalPage> {
  const query = new URLSearchParams({ page: String(page), size: String(size) });
  if (status) query.set("status", status);
  return apiRead<RentalPage>(`/api/v1/rentals/me?${query}`);
}
