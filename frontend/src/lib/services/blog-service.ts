import { apiRead } from "@/lib/bff/server-fetch";
import { BackendError } from "@/lib/bff/backend-error";
import { BlogPost, BlogPostPageResponse } from "@/lib/types/blog";

/**
 * No `status` query param on purpose: BE ignores it for non-manager callers
 * anyway (forces PUBLISHED), so sending it would only be misleading.
 */
export async function getBlogPosts(page = 0, size = 9): Promise<BlogPostPageResponse> {
  return apiRead<BlogPostPageResponse>(`/api/v1/blog-posts?page=${page}&size=${size}`);
}

/**
 * A missing slug or a DRAFT post both come back as 404 from BE — mapped to
 * `null` here so the caller can call `notFound()`. Any other failure (5xx,
 * network) is deliberately rethrown instead of swallowed: showing a false
 * "post not found" for a real backend outage would hide the actual problem.
 */
export async function getBlogPostBySlug(slug: string): Promise<BlogPost | null> {
  try {
    return await apiRead<BlogPost>(`/api/v1/blog-posts/${encodeURIComponent(slug)}`);
  } catch (error) {
    if (error instanceof BackendError && error.status === 404) return null;
    throw error;
  }
}
