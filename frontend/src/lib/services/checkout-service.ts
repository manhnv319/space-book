import "server-only";

import { readSessionTokens } from "@/lib/bff/auth-cookies";
import { backendRequest } from "@/lib/bff/backend-request";
import { apiRead } from "@/lib/bff/server-fetch";
import type { Address, BankTransferPayment, OrderDetail, OrderPage } from "@/lib/types/checkout";

export async function getAddresses(): Promise<Address[]> {
  return apiRead<Address[]>("/api/v1/addresses");
}

/**
 * Trạng thái chuyển khoản của một đơn.
 *
 * BE dùng chung endpoint này cho cả "khởi tạo" lẫn "đọc lại": lần gọi đầu sinh
 * mã và hạn thanh toán, các lần sau trả về đúng bản ghi đó. Nhờ vậy trang thanh
 * toán reload bao nhiêu lần cũng không sinh mã mới — mã đã in trên QR khách đang
 * quét vẫn còn hiệu lực.
 */
export async function getBankTransfer(orderId: number): Promise<BankTransferPayment> {
  return apiRead<BankTransferPayment>(`/api/v1/payment/bank-transfer/${orderId}`);
}

/**
 * Ảnh QR dưới dạng `data:` URI để nhúng thẳng vào `<img>`.
 *
 * Nhận đường dẫn thay vì id vì QR dùng chung cho đơn hàng và gói thuê tháng.
 *
 * Endpoint QR yêu cầu Bearer token, mà thẻ `<img>` của trình duyệt thì không
 * gắn được header — và token phải nằm server-only. Nên server tự tải PNG rồi
 * nhúng vào HTML. Ảnh QR chỉ khoảng vài KB nên chi phí không đáng kể, đổi lại
 * không phải mở thêm route handler proxy nào.
 *
 * Trả null khi lỗi: màn thanh toán vẫn dùng được nhờ số tài khoản và nội dung
 * chuyển khoản hiển thị dạng chữ, mất QR không phải lý do để chặn khách trả tiền.
 */
export async function getBankTransferQrDataUri(path: string): Promise<string | null> {
  try {
    const { accessToken } = await readSessionTokens();
    if (!accessToken) return null;

    const response = await backendRequest(path, {
      accessToken,
      headers: { accept: "image/png" },
    });
    if (!response.ok) return null;

    const base64 = Buffer.from(await response.arrayBuffer()).toString("base64");
    return `data:image/png;base64,${base64}`;
  } catch (error) {
    console.error("Failed to render the transfer QR:", error);
    return null;
  }
}

export async function getMyOrders(page = 0, size = 10): Promise<OrderPage> {
  return apiRead<OrderPage>(`/api/v1/orders/me?page=${page}&size=${size}`);
}

/**
 * Lọc theo nhiều trạng thái cùng lúc — một tab của khách gộp nhiều trạng thái
 * nội bộ, và lọc từng cái một thì số trang trả về sẽ sai.
 */
export async function getMyOrdersByStatuses(
  statuses: string[], page = 0, size = 10,
): Promise<OrderPage> {
  const query = new URLSearchParams({ page: String(page), size: String(size) });
  for (const status of statuses) query.append("statuses", status);
  return apiRead<OrderPage>(`/api/v1/orders/me/by-status?${query}`);
}

/** Số đơn theo trạng thái, cho số đếm trên tab. */
export async function getMyOrderSummary(): Promise<Record<string, number>> {
  return apiRead<Record<string, number>>("/api/v1/orders/me/summary").catch(() => ({}));
}

export async function getOrderDetail(orderId: number): Promise<OrderDetail> {
  return apiRead<OrderDetail>(`/api/v1/orders/${orderId}`);
}
