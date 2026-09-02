import type { Rental, RentalStatus } from "@/lib/types/rental";

const LABELS: Record<RentalStatus, string> = {
  PENDING: "Chờ giao sách",
  RENTED: "Đang thuê",
  RETURNED: "Đã trả",
  LATE: "Quá hạn",
  LOST: "Báo mất",
  CANCELLED: "Đã huỷ",
};

export function rentalStatusLabel(status: RentalStatus): string {
  return LABELS[status] ?? status;
}

/** Phiếu còn đang giữ sách — RENTED hoặc LATE, khớp `RentalStatus.isActive()` ở BE. */
export function isActiveRental(rental: Rental): boolean {
  return rental.status === "RENTED" || rental.status === "LATE";
}

export interface DueInfo {
  /** Âm nghĩa là đã quá hạn. Null khi phiếu không có ngày hẹn trả. */
  daysLeft: number | null;
  overdue: boolean;
  text: string;
}

/**
 * Còn bao nhiêu ngày tới hạn trả.
 *
 * So sánh theo **ngày lịch**, không theo giờ: hạn trả là một ngày, nên "còn 1
 * ngày" phải đúng suốt cả ngày hôm đó chứ không đổi theo giờ mở trang.
 *
 * Số ngày quá hạn ưu tiên lấy `lateDays` do máy chủ tính — máy chủ mới là nơi
 * quyết định phí trễ, client tự đếm rồi hiện lệch thì gây tranh cãi.
 *
 * Thuần tuý, `today` truyền vào để test được.
 */
export function describeDue(rental: Rental, today: Date): DueInfo {
  if (rental.status === "RETURNED") {
    return { daysLeft: null, overdue: false, text: "Đã trả xong" };
  }
  if (!rental.plannedReturnDate) {
    return { daysLeft: null, overdue: false, text: "Chưa có hạn trả" };
  }

  const due = new Date(`${rental.plannedReturnDate}T00:00:00`);
  if (Number.isNaN(due.getTime())) {
    return { daysLeft: null, overdue: false, text: "Chưa có hạn trả" };
  }

  const startOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const daysLeft = Math.round((due.getTime() - startOfToday.getTime()) / 86_400_000);

  if (daysLeft < 0 || rental.status === "LATE") {
    const late = rental.lateDays ?? Math.abs(daysLeft);
    return { daysLeft, overdue: true, text: `Quá hạn ${late} ngày` };
  }
  if (daysLeft === 0) return { daysLeft, overdue: false, text: "Đến hạn hôm nay" };
  return { daysLeft, overdue: false, text: `Còn ${daysLeft} ngày` };
}
