export type BlogPostStatus = "DRAFT" | "PUBLISHED" | "ARCHIVED";

/**
 * List view — matches `BlogPostSummaryResponse` (BE excludes `content` from
 * list payloads on purpose). Public callers only ever receive PUBLISHED
 * posts regardless of query params (enforced server-side), so `status` is
 * deliberately not surfaced here — the FE never needs to branch on it.
 */
export interface BlogPostSummary {
  id: number;
  slug: string;
  title: string;
  excerpt: string | null;
  coverImageUrl: string | null;
  bookId: number | null;
  publishedAt: string | null;
}

export interface BlogPost extends BlogPostSummary {
  content: string;
  status: BlogPostStatus;
  createdAt: string;
  updatedAt: string;
}

export interface BlogPostPageResponse {
  content: BlogPostSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
