import "server-only";

import { apiRead } from "@/lib/bff/server-fetch";
import type { RentalPage, RentalStatus } from "@/lib/types/rental";

export async function getAdminRentals(status: RentalStatus | undefined, overdue: boolean, page = 0, size = 20): Promise<RentalPage> {
  const query = new URLSearchParams({ page: String(page), size: String(size) });
  if (overdue) return apiRead<RentalPage>(`/api/v1/rentals/overdue?${query}`);
  if (status) query.set("status", status);
  return apiRead<RentalPage>(`/api/v1/rentals?${query}`);
}
