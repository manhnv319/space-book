import Link from "next/link";
import { notFound, redirect } from "next/navigation";

import { CopyField } from "@/components/checkout/copy-field";
import { PaymentWatcher } from "@/components/checkout/payment-watcher";
import { getCurrentUser } from "@/lib/bff/current-user";
import { formatVnd } from "@/lib/format/currency";
import { getBankTransfer, getBankTransferQrDataUri } from "@/lib/services/checkout-service";

export const metadata = { title: "Chuyển khoản thanh toán" };

interface PaymentPageProps {
  params: Promise<{ orderId: string }>;
}

export default async function PaymentPage({ params }: PaymentPageProps) {
  const { orderId: raw } = await params;
  if (!/^\d+$/.test(raw)) notFound();
  const orderId = Number(raw);

  const user = await getCurrentUser();
  if (!user) redirect(`/login?next=%2Fcheckout%2F${orderId}`);

  // Ownership is enforced by the backend: it rejects an order that is not the
  // caller's, so a guessed id yields an error rather than someone else's data.
  const payment = await getBankTransfer(orderId).catch(() => null);
  if (!payment) notFound();

  if (payment.status === "SUCCESS") {
    return (
      <section className="payment-page payment-done">
        <p className="eyebrow">Đơn #{payment.orderId}</p>
        <h1>Đã nhận được thanh toán</h1>
        <p>Cảm ơn bạn. Nhà sách đã ghi nhận khoản chuyển khoản và đang chuẩn bị đơn hàng.</p>
        <div className="hero-actions">
          <Link className="button" href="/account">Xem đơn của tôi</Link>
          <Link className="button button-secondary" href="/sach">Tiếp tục chọn sách</Link>
        </div>
      </section>
    );
  }

  const qr = await getBankTransferQrDataUri(`/api/v1/payment/bank-transfer/${orderId}/qr?size=320`);

  return (
    <section className="payment-page">
      <p className="eyebrow">Đơn #{payment.orderId}</p>
      <h1>Chuyển khoản để hoàn tất</h1>

      <div className="payment-layout">
        <div className="payment-qr">
          {qr ? (
            <img src={qr} alt="Mã QR chuyển khoản" width={320} height={320} />
          ) : (
            <div className="payment-qr-fallback">
              <p>Không tạo được mã QR lúc này.</p>
              <p>Bạn vẫn chuyển khoản được bằng thông tin bên cạnh.</p>
            </div>
          )}
          <p className="payment-qr-note">Quét bằng ứng dụng ngân hàng — số tiền và nội dung đã điền sẵn.</p>
        </div>

        <div className="payment-details">
          <CopyField label="Ngân hàng" value={payment.bankName} />
          <CopyField label="Chủ tài khoản" value={payment.accountName} />
          <CopyField label="Số tài khoản" value={payment.accountNumber} />
          <CopyField label="Số tiền" value={formatVnd(payment.amount)} hint="Phải chuyển đúng số tiền này." />
          <CopyField
            label="Nội dung chuyển khoản"
            value={payment.paymentReference}
            hint="Bắt buộc ghi đúng. Đây là thứ hệ thống dùng để nhận ra khoản tiền của bạn."
          />

          <PaymentWatcher expiresAt={payment.expiresAt} />
        </div>
      </div>

      <p className="payment-help">
        Nếu chuyển sai nội dung hoặc sai số tiền, tiền vẫn về tài khoản nhà sách nhưng đơn sẽ không tự xác nhận —
        hãy liên hệ để được đối soát thủ công.
      </p>
    </section>
  );
}
