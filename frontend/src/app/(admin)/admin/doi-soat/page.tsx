import { redirect } from "next/navigation";

import { hasPermission, PERMISSION_HANDLE_PAYMENTS } from "@/lib/auth/permissions";
import { getCurrentUser } from "@/lib/bff/current-user";
import { formatVnd } from "@/lib/format/currency";
import { getUnmatchedTransfers } from "@/lib/services/admin-book-service";
import { resolveUnmatchedTransferAction } from "@/app/actions/admin-operations";

export const metadata = { title: "Đối soát chuyển khoản" };

const DATE_TIME = new Intl.DateTimeFormat("vi-VN", {
  day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit",
});

function formatMoment(value: string | null): string {
  if (!value) return "—";
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? "—" : DATE_TIME.format(parsed);
}

export default async function ReconciliationPage() {
  // The admin layout only checks book:manage. Handling money is a separate
  // duty, so this page re-checks — and the backend enforces it regardless.
  const user = await getCurrentUser();
  if (!hasPermission(user, PERMISSION_HANDLE_PAYMENTS)) redirect("/admin");

  const transfers = await getUnmatchedTransfers().catch(() => null);

  return (
    <section className="admin-page">
      <div className="admin-section-header">
        <div>
          <h1>Đối soát chuyển khoản</h1>
          <p className="section-subtitle">
            Tiền đã vào tài khoản nhưng hệ thống không gắn được vào đơn nào — thường do khách ghi sai nội dung
            hoặc chuyển sai số tiền.
          </p>
        </div>
      </div>

      {!transfers ? (
        <p className="form-status" role="alert">Không tải được danh sách đối soát.</p>
      ) : transfers.content.length === 0 ? (
        <p className="admin-empty">Không có giao dịch nào cần đối soát.</p>
      ) : (
        <div className="admin-table-scroll">
          <table className="admin-table">
            <thead>
              <tr>
                <th scope="col">Nhận lúc</th>
                <th scope="col">Số tiền</th>
                <th scope="col">Nội dung nhận được</th>
                <th scope="col">Lý do không khớp</th>
                <th scope="col">Gán vào đơn</th>
              </tr>
            </thead>
            <tbody>
              {transfers.content.map((transfer) => (
                <tr key={transfer.id}>
                  <td>{formatMoment(transfer.receivedAt)}</td>
                  <td>{transfer.amount === null ? "—" : formatVnd(transfer.amount)}</td>
                  <td>{transfer.paymentReference ?? "(không có)"}</td>
                  <td>{transfer.reason}</td>
                  <td><form action={resolveUnmatchedTransferAction} className="admin-inline-form"><input name="transferId" type="hidden" value={transfer.id} /><input name="orderId" inputMode="numeric" min="1" placeholder="Mã đơn" required type="number" /><button className="button button-small button-secondary">Xác nhận</button></form></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <p className="section-subtitle">
        Đối chiếu số tiền và thời điểm với ứng dụng ngân hàng để tìm giao dịch, rồi xử lý thủ công với khách.
        Việc gán giao dịch vào đơn ngay trên màn này chưa được triển khai.
      </p>
    </section>
  );
}
