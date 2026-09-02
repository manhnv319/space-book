import "server-only";

import { apiRead } from "@/lib/bff/server-fetch";
import type { BestsellerSuggestion, BookCopy, UnmatchedTransferPage } from "@/lib/types/admin";

/** `GET /api/v1/books/bestseller-suggestions` — permission `book:manage`. */
export async function getBestsellerSuggestions(limit = 20, days = 90): Promise<BestsellerSuggestion[]> {
  return apiRead<BestsellerSuggestion[]>(`/api/v1/books/bestseller-suggestions?limit=${limit}&days=${days}`);
}

/** `GET /api/v1/bank-transfers/unmatched` — permission `payment:refund`. */
export async function getUnmatchedTransfers(page = 0, size = 20): Promise<UnmatchedTransferPage> {
  return apiRead<UnmatchedTransferPage>(`/api/v1/bank-transfers/unmatched?page=${page}&size=${size}`);
}

export async function getBookCopies(bookId: number): Promise<BookCopy[]> {
  return apiRead<BookCopy[]>(`/api/v1/books/${bookId}/copies`);
}
