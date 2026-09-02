import "server-only";

import { apiRead } from "@/lib/bff/server-fetch";
import type { OrderPage, OrderStatus } from "@/lib/types/checkout";

interface AdminOrderFilters {
  status?: OrderStatus;
  paymentStatus?: string;
  search?: string;
}

export async function getAdminOrders(filters: AdminOrderFilters, page = 0, size = 20): Promise<OrderPage> {
  const query = new URLSearchParams({ page: String(page), size: String(size) });
  if (filters.status) query.set("status", filters.status);
  if (filters.paymentStatus) query.set("paymentStatus", filters.paymentStatus);
  if (filters.search) query.set("search", filters.search);
  return apiRead<OrderPage>(`/api/v1/orders?${query}`);
}
