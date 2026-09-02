/** Khớp `AddressResponse` của BE (`GET /api/v1/addresses`). */
export interface Address {
  id: number;
  fullName: string;
  phone: string;
  province: string;
  district: string;
  ward: string;
  addressDetail: string;
  isDefault: boolean;
}

/** Khớp `OrderResponse`. `finalAmount` do BE tính — FE không bao giờ tự cộng. */
/** Vài sản phẩm đầu, để danh sách đơn nhận ra được bằng bìa và tên sách. */
export interface OrderItemPreview {
  bookId: number | null;
  title: string | null;
  imageUrl: string | null;
  itemType: "PURCHASE" | "RENTAL";
  quantity: number | null;
}

export interface OrderSummary {
  id: number;
  orderCode: string;
  orderType: string;
  status: string;
  paymentStatus: string;
  paymentMethod: string;
  totalItems: number;
  totalAmount: number;
  totalDeposit: number;
  totalDiscount: number;
  finalAmount: number;
  createdAt: string;
  items: OrderItemPreview[];
}

export type BankTransferStatus = "PENDING" | "SUCCESS" | "FAILED" | "REFUNDED";

/**
 * Khớp `BankTransferPaymentResponse`.
 *
 * `paymentReference` là nội dung chuyển khoản khách phải ghi — đây chính là thứ
 * bộ đối soát dùng để khớp email báo có với đơn hàng, nên không được sửa đổi hay
 * rút gọn khi hiển thị.
 */
export interface BankTransferPayment {
  orderId: number;
  paymentReference: string;
  amount: number;
  bankName: string;
  accountName: string;
  accountNumber: string;
  qrPayload: string;
  expiresAt: string | null;
  status: BankTransferStatus;
}

export type OrderStatus =
  | "PENDING" | "CONFIRMED" | "PROCESSING" | "SHIPPING" | "COMPLETED" | "CANCELLED" | "REFUNDED";

/** Một mốc trong lộ trình. `source`: PAYMENT | STAFF | AUTO. */
export interface OrderStatusStep {
  status: OrderStatus;
  source: string;
  changedAt: string;
}

export interface OrderItemDetail {
  bookId: number;
  itemType: "PURCHASE" | "RENTAL";
  quantity: number | null;
  unitPrice: number | null;
  depositAmount: number | null;
  rentalTermValue: number | null;
  rentalTermUnit: string | null;
  subtotal: number | null;
}

/**
 * Chi tiết đơn mang `items` đầy đủ (giá, kỳ thuê), khác với bản rút gọn dùng cho
 * danh sách — nên loại trường đó ra khỏi phần kế thừa thay vì ép hai kiểu làm một.
 */
export interface OrderDetail extends Omit<OrderSummary, "items"> {
  shippingAddressId: number | null;
  notes: string | null;
  items: OrderItemDetail[];
  /** Rỗng với đơn tạo trước khi có bảng lịch sử. */
  timeline: OrderStatusStep[];
}

export interface OrderPage {
  content: OrderSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
