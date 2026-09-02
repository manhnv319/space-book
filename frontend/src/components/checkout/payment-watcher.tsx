"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface PaymentWatcherProps {
  /** ISO timestamp; null when the backend did not set an expiry. */
  expiresAt: string | null;
  pollSeconds?: number;
}

function remainingMs(expiresAt: string | null): number | null {
  if (!expiresAt) return null;
  const deadline = new Date(expiresAt).getTime();
  if (Number.isNaN(deadline)) return null;
  return Math.max(0, deadline - Date.now());
}

function formatCountdown(ms: number): string {
  const total = Math.floor(ms / 1000);
  return `${String(Math.floor(total / 60)).padStart(2, "0")}:${String(total % 60).padStart(2, "0")}`;
}

/**
 * Client island: đếm ngược hạn chuyển khoản và hỏi lại máy chủ theo chu kỳ.
 *
 * Xác nhận thanh toán đến từ email ngân hàng nên không có sự kiện nào đẩy về
 * trình duyệt được. `router.refresh()` cho server component vẽ lại — trang tự
 * chuyển sang trạng thái "đã nhận" mà không cần khách bấm gì.
 *
 * Dừng hẳn khi hết hạn: tiếp tục hỏi một đơn đã quá hạn chỉ tốn request.
 */
export function PaymentWatcher({ expiresAt, pollSeconds = 8 }: PaymentWatcherProps) {
  const router = useRouter();
  const [left, setLeft] = useState<number | null>(() => remainingMs(expiresAt));

  useEffect(() => {
    const tick = window.setInterval(() => setLeft(remainingMs(expiresAt)), 1000);
    return () => window.clearInterval(tick);
  }, [expiresAt]);

  const expired = left !== null && left <= 0;

  useEffect(() => {
    if (expired) return;
    const poll = window.setInterval(() => router.refresh(), pollSeconds * 1000);
    return () => window.clearInterval(poll);
  }, [expired, pollSeconds, router]);

  return (
    <div className="payment-watcher" aria-live="polite">
      {expired ? (
        <p className="payment-expired" role="alert">
          Đã quá hạn chuyển khoản cho đơn này. Nếu bạn đã chuyển tiền, hãy liên hệ nhà sách để được đối soát.
        </p>
      ) : (
        <>
          <p className="payment-countdown">
            {left === null ? "Đang chờ xác nhận…" : <>Còn lại <strong>{formatCountdown(left)}</strong> để hoàn tất chuyển khoản</>}
          </p>
          <p className="payment-poll-note">
            Trang sẽ tự cập nhật khi hệ thống nhận được báo có từ ngân hàng, thường trong vòng 1–2 phút.
          </p>
        </>
      )}
    </div>
  );
}
