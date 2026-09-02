import Link from "next/link";

import { BookCover } from "@/components/ui/book-cover";
import type { Book } from "@/lib/types/book";

export function CampaignBanner({ book }: { book: Book | null }) {
  return (
    <section className="campaign-banner" aria-labelledby="campaign-title">
      <div><p className="eyebrow">Thuê sách cùng VelstrongBook</p><h2 id="campaign-title">Đọc nhiều hơn, trả ít hơn</h2><p>Chọn sách, chọn kỳ thuê và theo dõi hành trình đọc của bạn ở một nơi.</p><Link className="button button-secondary" href="/sach">Khám phá sách có thể thuê</Link></div>
      {book ? <div aria-hidden="true" className="campaign-banner-cover"><BookCover alt="" src={book.imageUrl} variant="detail" /></div> : null}
    </section>
  );
}
