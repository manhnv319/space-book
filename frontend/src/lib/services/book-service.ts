import { apiRead } from "@/lib/bff/server-fetch";
import { Book, BookPageResponse } from "@/lib/types/book";

export async function getBooks(page = 0, size = 12): Promise<BookPageResponse> {
  return apiRead<BookPageResponse>(`/api/v1/books?page=${page}&size=${size}`);
}

export async function searchBooks(keyword: string, page = 0, size = 12): Promise<BookPageResponse> {
  const url = `/api/v1/books/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`;
  return apiRead<BookPageResponse>(url);
}

export async function getBooksByCategory(categoryId: number, page = 0, size = 12): Promise<BookPageResponse> {
  const url = `/api/v1/books/categories?categoryIds=${categoryId}&page=${page}&size=${size}`;
  return apiRead<BookPageResponse>(url);
}

export async function getFeaturedBooks(page = 0, size = 12): Promise<BookPageResponse> {
  return apiRead<BookPageResponse>(`/api/v1/books/featured?page=${page}&size=${size}`);
}

export async function getBestsellers(page = 0, size = 12): Promise<BookPageResponse> {
  return apiRead<BookPageResponse>(`/api/v1/books/bestsellers?page=${page}&size=${size}`);
}

export async function getNewArrivals(page = 0, size = 12): Promise<BookPageResponse> {
  return apiRead<BookPageResponse>(`/api/v1/books/new-arrivals?page=${page}&size=${size}`);
}

export async function getBookById(id: string | number): Promise<Book | null> {
  try {
    return await apiRead<Book>(`/api/v1/books/${id}`);
  } catch (error) {
    console.error(`Failed to fetch book ${id}:`, error);
    return null;
  }
}

const MAX_BATCH_BOOK_IDS = 20;

/**
 * BE has no batch-by-ids endpoint, so this fans out `getBookById` in
 * parallel (capped at 20 — same cap as the guest cart). Used to enrich cart
 * rows (cover/title/price) for both the guest cookie cart and, for cover
 * images, the logged-in cart (`CartItemDetail` has no `imageUrl`).
 */
export async function getBooksByIds(ids: number[]): Promise<Map<number, Book>> {
  const uniqueIds = Array.from(new Set(ids)).slice(0, MAX_BATCH_BOOK_IDS);
  const books = await Promise.all(uniqueIds.map((id) => getBookById(id)));

  const byId = new Map<number, Book>();
  uniqueIds.forEach((id, index) => {
    const book = books[index];
    if (book) byId.set(id, book);
  });
  return byId;
}
