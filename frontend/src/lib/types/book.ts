export interface Book {
  id: number;
  isbn: string;
  title: string;
  description: string;
  imageUrl: string | null;
  format: string;
  listPrice: number;
  rentalPriceDay: number;
  rentalPriceWeek: number;
  rentalPriceMonth: number;
  depositAmount: number;
  publishYear: number;
  publisher: string;
  language: string;
  pageCount: number;
  authors: string[];
  categories: string[];
  createdAt: string;
  isFeatured: boolean;
  isBestseller: boolean;
}

export interface BookPageResponse {
  content: Book[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
