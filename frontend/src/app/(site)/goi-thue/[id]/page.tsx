import Link from "next/link";
import { notFound, redirect } from "next/navigation";

import { CopyField } from "@/components/checkout/copy-field";
import { PaymentWatcher } from "@/components/checkout/payment-watcher";
import { getCurrentUser } from "@/lib/bff/current-user";
import { formatVnd } from "@/lib/format/currency";
import { getBankTransferQrDataUri } from "@/lib/services/checkout-service";
import { getMySubscription, getSubscriptionPayment } from "@/lib/services/subscription-service";

export const metadata = { title: "Thanh toán gói thuê" };

export default async function SubscriptionPaymentPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  if (!/^\d+$/.test(id)) notFound();
  const subscriptionId = Number(id);

  const user = await getCurrentUser();
  if (!user) redirect(`/login?next=%2Fgoi-thue%2F${subscriptionId}`);

  const mine = await getMySubscription();
  if (mine?.status === "ACTIVE" && mine.id === subscriptionId) {
    return (
      <section className="payment-page payment-done">
        <p className="eyebrow">Gói #{subscriptionId}</p>
        <h1>Gói đã được kích hoạt</h1>
        <p>Nhà sách đã nhận được thanh toán. Bạn có thể bắt đầu thuê sách theo gói.</p>
        <div className="hero-actions">
          <Link className="button" href="/sach">Chọn sách để thuê</Link>
          <Link className="button button-secondary" href="/goi-thue">Xem gói của tôi</Link>
        </div>
      </section>
    );
  }

  // Backend chỉ trả gói của chính người gọi, nên đoán id chỉ nhận được lỗi.
  const payment = await getSubscriptionPayment(subscriptionId).catch(() => null);
  if (!payment) notFound();

  const qr = await getBankTransferQrDataUri(
    `/api/v1/subscriptions/me/${subscriptionId}/payment/qr?size=320`,
  );

  return (
    <section className="payment-page">
      <p className="eyebrow">Gói #{subscriptionId}</p>
      <h1>Chuyển khoản để kích hoạt gói</h1>

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
        Thời hạn gói tính từ ngày nhà sách nhận được tiền, nên bạn không mất ngày nào vì thời gian chờ chuyển khoản.
      </p>
    </section>
  );
}
