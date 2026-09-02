export type ItemType = "PURCHASE" | "RENTAL";
export type RentalTermUnit = "DAY" | "WEEK" | "MONTH";

/**
 * BE trả `CartItemDetail` rỗng ở mọi endpoint mutation (`POST/PUT /cart/items`) —
 * chỉ `GET /cart` mới enrich đủ title/giá/subtotal. Vì vậy nhiều field ở đây là
 * nullable đúng theo thực tế BE, không đánh lừa bằng kiểu `number` không nullable.
 */
export interface CartItemDetail {
  id: number;
  bookId: number;
  bookTitle: string | null;
  itemType: ItemType;
  quantity: number | null;
  rentalTermValue: number | null;
  rentalTermUnit: RentalTermUnit | null;
  unitPrice: number | null;
  depositAmount: number | null;
  subtotal: number | null;
}

export interface CartResponse {
  id: number | null;
  userId: number | null;
  items: CartItemDetail[];
  totalItems: number;
  totalAmount: number;
  totalDeposit: number;
}

export interface AddToCartInput {
  bookId: number;
  itemType: ItemType;
  quantity?: number;
  rentalTermValue?: number;
  rentalTermUnit?: RentalTermUnit;
}
