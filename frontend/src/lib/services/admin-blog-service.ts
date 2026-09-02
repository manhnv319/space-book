import "server-only";

import { apiRead } from "@/lib/bff/server-fetch";
import type { AdminBlogPostPageResponse } from "@/lib/types/admin";
import type { BlogPost, BlogPostStatus } from "@/lib/types/blog";

/**
 * Same public endpoint as `blog-service.ts` (D15 — one GET route serves both
 * audiences). BE reads `book:manage` off the caller's own session/cookies and,
 * for an authorized caller, returns every status and honors `status` as a
 * real filter — unlike the public service, which never sends it because BE
 * ignores it for non-managers anyway.
 */
export async function getAdminBlogPosts(
  status?: BlogPostStatus,
  page = 0,
  size = 20,
): Promise<AdminBlogPostPageResponse> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (status) params.set("status", status);
  return apiRead<AdminBlogPostPageResponse>(`/api/v1/blog-posts?${params.toString()}`);
}

// BE has no lookup-by-numeric-id endpoint, only by slug (see phase-03 report
// "open question" — `GetBlogPostBySlugUseCase` is the only detail read path).
// This scans one bounded page of admin summaries for the matching `id` to
// recover its `slug`, then fetches the full body (with `content`) by slug.
// Fine at this catalog's scale; would need a real by-id endpoint if the blog
// ever grows past this page size.
const ID_LOOKUP_PAGE_SIZE = 1000;

export async function getAdminBlogPostById(id: number): Promise<BlogPost | null> {
  const list = await getAdminBlogPosts(undefined, 0, ID_LOOKUP_PAGE_SIZE);
  const summary = list.content.find((post) => post.id === id);
  if (!summary) return null;
  return apiRead<BlogPost>(`/api/v1/blog-posts/${encodeURIComponent(summary.slug)}`);
}
