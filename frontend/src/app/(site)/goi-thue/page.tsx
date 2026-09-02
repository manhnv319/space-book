import Link from "next/link";
import { redirect } from "next/navigation";

import { PlanCard } from "@/components/subscription/plan-card";
import { getCurrentUser } from "@/lib/bff/current-user";
import { getActivePlans, getMySubscription } from "@/lib/services/subscription-service";

export const metadata = { title: "Gói thuê sách" };

const DATE = new Intl.DateTimeFormat("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric" });

function formatDate(value: string | null): string {
  if (!value) return "—";
  const parsed = new Date(`${value}T00:00:00`);
  return Number.isNaN(parsed.getTime()) ? "—" : DATE.format(parsed);
}

export default async function SubscriptionPlansPage() {
  const user = await getCurrentUser();
  if (!user) redirect("/login?next=%2Fgoi-thue");

  const [plans, mine] = await Promise.all([
    getActivePlans().catch(() => null),
    getMySubscription(),
  ]);

  return (
    <section className="account-page">
      <p className="eyebrow">Thuê sách</p>
      <h1>Gói thuê theo tháng</h1>

      {mine?.status === "ACTIVE" && (
        <div className="plan-active" role="status">
          <p>
            Bạn đang dùng gói <strong>{mine.subscription?.name ?? `#${mine.subscriptionId}`}</strong>,
            hiệu lực tới {formatDate(mine.endDate)}.
          </p>
        </div>
      )}

      {mine?.status === "PENDING_PAYMENT" && (
        <div className="plan-pending" role="status">
          <p>Bạn có một gói đang chờ thanh toán.</p>
          <Link className="button" href={`/goi-thue/${mine.id}`}>Tiếp tục chuyển khoản</Link>
        </div>
      )}

      {plans === null ? (
        <p className="form-status" role="alert">Không tải được danh sách gói.</p>
      ) : plans.length === 0 ? (
        <p className="account-empty">Hiện chưa có gói nào được mở bán.</p>
      ) : (
        <ul className="plan-list">
          {plans.map((plan) => (
            <PlanCard key={plan.id} plan={plan} disabled={mine?.status === "ACTIVE"} />
          ))}
        </ul>
      )}

      <p className="section-subtitle subscription-payment-note">
        Gói chỉ có hiệu lực sau khi nhà sách nhận được chuyển khoản. Thời hạn tính từ ngày tiền về,
        không tính từ lúc bạn đặt mua.
      </p>
    </section>
  );
}
