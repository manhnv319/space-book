export type ReviewSource = "PURCHASE" | "RENTAL";

export interface BookReview {
  id: number;
  bookId: number;
  orderItemId: number;
  source: ReviewSource;
  rating: number;
  comment: string;
  createdAt: string;
  modifiedAt: string | null;
}

export interface ReviewPage {
  content: BookReview[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ReviewTransaction {
  orderItemId: number;
  source: ReviewSource;
  review: BookReview | null;
}
