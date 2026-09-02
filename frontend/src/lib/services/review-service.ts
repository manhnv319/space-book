import { apiRead } from "@/lib/bff/server-fetch";
import type { ReviewPage, ReviewTransaction } from "@/lib/types/review";

export async function getBookReviews(bookId: number): Promise<ReviewPage> {
  try {
    return await apiRead<ReviewPage>(`/api/v1/books/${bookId}/reviews?page=0&size=20`);
  } catch (error) {
    console.error(`Failed to fetch reviews for book ${bookId}:`, error);
    return { content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 };
  }
}

export async function getMyBookReviewOptions(bookId: number): Promise<ReviewTransaction[]> {
  try { return await apiRead<ReviewTransaction[]>(`/api/v1/reviews/books/${bookId}/me`); }
  catch { return []; }
}
