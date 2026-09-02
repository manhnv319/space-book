import Link from "next/link";
import { redirect } from "next/navigation";

import { Badge } from "@/components/ui/badge";
import { getCurrentUser } from "@/lib/bff/current-user";
import { formatVnd } from "@/lib/format/currency";
import { describeDue, isActiveRental, rentalStatusLabel } from "@/lib/rentals/due";
import { getMyRentals } from "@/lib/services/rental-service";
import type { Rental } from "@/lib/types/rental";

export const metadata = { title: "Sách đang thuê" };

const DATE = new Intl.DateTimeFormat("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric" });

function formatDate(value: string | null): string {
  if (!value) return "—";
  const parsed = new Date(`${value}T00:00:00`);
  return Number.isNaN(parsed.getTime()) ? "—" : DATE.format(parsed);
}

function RentalCard({ rental, today }: { rental: Rental; today: Date }) {
  const due = describeDue(rental, today);
  return (
    <li className={due.overdue ? "rental-card is-overdue" : "rental-card"}>
      <div className="rental-card-body">
        <p className="rental-card-title">
          {rental.bookId ? (
            <Link href={`/sach/${rental.bookId}`}>{rental.bookTitle ?? `Sách #${rental.bookId}`}</Link>
          ) : (
            // Bản sao hoặc đầu sách đã bị xoá — vẫn hiện phiếu vì cọc còn treo ở đây.
            <span>{rental.bookTitle ?? "Sách không còn trong hệ thống"}</span>
          )}
          <Badge tone={due.overdue ? "default" : "muted"}>{rentalStatusLabel(rental.status)}</Badge>
        </p>
        <p className="rental-card-dates">
          Thuê từ {formatDate(rental.rentalStartDate)} · Hạn trả {formatDate(rental.plannedReturnDate)}
          {rental.actualReturnDate && ` · Đã trả ${formatDate(rental.actualReturnDate)}`}
        </p>
      </div>
      <div className="rental-card-side">
        <span className={due.overdue ? "rental-due is-overdue" : "rental-due"}>{due.text}</span>
        <span className="rental-deposit">Cọc {formatVnd(rental.depositAmount)}</span>
        {/* Phí trễ và phí hỏng do máy chủ tính, chỉ hiển thị lại. */}
        {(rental.lateFeeAmount ?? 0) > 0 && (
          <span className="rental-fee">Phí trễ {formatVnd(rental.lateFeeAmount)}</span>
        )}
        {(rental.damageFeeAmount ?? 0) > 0 && (
          <span className="rental-fee">Phí hỏng {formatVnd(rental.damageFeeAmount)}</span>
        )}
      </div>
    </li>
  );
}

export default async function MyRentalsPage() {
  const user = await getCurrentUser();
  if (!user) redirect("/login?next=%2Faccount%2Fsach-thue");

  const rentals = await getMyRentals().catch(() => null);
  const today = new Date();

  const active = rentals?.content.filter(isActiveRental) ?? [];
  const finished = rentals?.content.filter((rental) => !isActiveRental(rental)) ?? [];

  return (
    <section className="account-page">
      <h1>Sách đang thuê</h1>

      {rentals === null ? (
        <p className="form-status" role="alert">Không tải được danh sách sách thuê.</p>
      ) : rentals.content.length === 0 ? (
        <div className="account-empty">
          <p>Bạn chưa thuê cuốn sách nào.</p>
          <Link className="button" href="/sach">Xem sách cho thuê</Link>
        </div>
      ) : (
        <>
          <div className="account-section">
            <h2>Đang giữ ({active.length})</h2>
            {active.length === 0 ? (
              <p className="account-empty">Bạn không còn giữ cuốn nào.</p>
            ) : (
              <ul className="rental-list">
                {active.map((rental) => <RentalCard key={rental.id} rental={rental} today={today} />)}
              </ul>
            )}
          </div>

          {finished.length > 0 && (
            <div className="account-section">
              <h2>Đã kết thúc</h2>
              <ul className="rental-list">
                {finished.map((rental) => <RentalCard key={rental.id} rental={rental} today={today} />)}
              </ul>
            </div>
          )}
        </>
      )}

      <p className="section-subtitle rental-return-note">
        Trả sách tại nhà sách hoặc liên hệ để được hướng dẫn. Tiền cọc được hoàn khi sách về đúng hạn và nguyên vẹn.
      </p>
    </section>
  );
}
