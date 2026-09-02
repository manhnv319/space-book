import type { BlogPostStatus, BlogPostSummary } from "@/lib/types/blog";

/**
 * Admin list view of blog posts. Same JSON shape as the public
 * `BlogPostSummary` plus `status` — BE always includes it, the public type
 * just omits declaring it (see phase-09 report). Admins need it to render
 * the DRAFT/PUBLISHED/ARCHIVED badge in the table.
 */
export interface AdminBlogPostSummary extends BlogPostSummary {
  status: BlogPostStatus;
}

export interface AdminBlogPostPageResponse {
  content: AdminBlogPostSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

/** Matches `BestsellerSuggestionResponse.java`. */
export interface BestsellerSuggestion {
  bookId: number;
  title: string;
  soldQuantity: number;
  isFeatured: boolean;
  isBestseller: boolean;
}

/** Khớp `UnmatchedTransferResponse`. Không có mô tả chuyển khoản — BE cố tình không lưu. */
export interface UnmatchedTransfer {
  id: number;
  paymentReference: string | null;
  amount: number | null;
  receivedAt: string | null;
  reason: string;
  createdAt: string;
}

export interface UnmatchedTransferPage {
  content: UnmatchedTransfer[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type BookCopyStatus = "AVAILABLE" | "RENTED" | "SOLD" | "DAMAGED" | "LOST" | "MAINTENANCE";
export type BookCopyCondition = "NEW" | "LIKE_NEW" | "GOOD" | "FAIR" | "POOR";
export interface BookCopy { id: number; bookId: number; status: BookCopyStatus; condition: BookCopyCondition; notes: string | null; }
