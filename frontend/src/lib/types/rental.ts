export type RentalStatus = "PENDING" | "RENTED" | "RETURNED" | "LATE" | "LOST" | "CANCELLED";
export type RentalTermUnit = "DAY" | "WEEK" | "MONTH";

/**
 * Khớp `RentalResponse`. `bookId`/`bookTitle` được BE nạp thêm từ bản sao sách —
 * null khi bản sao hoặc đầu sách đã bị xoá, phiếu vẫn giữ để không giấu mất cọc.
 */
export interface Rental {
  id: number;
  bookCopyId: number | null;
  bookId: number | null;
  bookTitle: string | null;
  userId?: number | null;
  rentalTermUnit: RentalTermUnit | null;
  rentalTermValue: number | null;
  depositAmount: number | null;
  rentalStartDate: string | null;
  plannedReturnDate: string | null;
  actualReturnDate: string | null;
  status: RentalStatus;
  lateDays: number | null;
  lateFeeAmount: number | null;
  damageFeeAmount: number | null;
}

export interface RentalPage {
  content: Rental[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
