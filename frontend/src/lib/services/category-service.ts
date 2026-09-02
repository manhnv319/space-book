import { apiRead } from "@/lib/bff/server-fetch";
import { Category } from "@/lib/types/category";

/**
 * Not cached: `"use cache"` requires enabling `cacheComponents` in
 * next.config.ts, an experimental flag with broad build/runtime implications
 * out of scope for this phase. Accepted tradeoff: 1 extra backend request per
 * `SiteShell` render (see phase-06 Risk Assessment).
 */
export async function getCategories(): Promise<Category[]> {
  return apiRead<Category[]>("/api/v1/categories");
}
