import Link from "next/link";

import { BookCover } from "@/components/ui/book-cover";
import { formatVnd } from "@/lib/format/currency";
import type { Book } from "@/lib/types/book";

interface HomeHeroProps {
  /** Book to spotlight, or null when no shelf returned anything. */
  book: Book | null;
  /** Honest label for where `book` came from — never claim "nổi bật" for a fallback pick. */
  eyebrow: string;
}

/**
 * Server component: homepage hero.
 *
 * Follows the editorial pattern used by international booksellers (Waterstones,
 * Blackwell's): one static spotlight on a single real book — cover, title,
 * author, blurb, price, one primary action. Deliberately not a carousel; those
 * measure worse and would need a promo-banner CMS this project does not have.
 *
 * Every value comes straight from the backend payload. Prices are printed as
 * returned (backend is the pricing authority) and each line is hidden when its
 * price is absent, so nothing is implied about a book that cannot be bought or
 * rented.
 */
export function HomeHero({ book, eyebrow }: HomeHeroProps) {
  if (!book) return <HeroFallback />;

  const author = book.authors?.filter(Boolean).join(", ");
  const canBuy = book.listPrice > 0;
  const canRent = book.rentalPriceDay > 0;

  return (
    <section className="hero-spotlight" aria-labelledby="hero-title">
      <div className="hero-spotlight-body">
        <p className="eyebrow">{eyebrow}</p>
        <h1 id="hero-title" className="hero-spotlight-title">{book.title}</h1>
        {author && <p className="hero-spotlight-author">{author}</p>}
        {book.description && <p className="hero-spotlight-blurb">{book.description}</p>}

        {(canBuy || canRent) && (
          <dl className="hero-spotlight-prices">
            {canBuy && (
              <div>
                <dt>Mua sở hữu</dt>
                <dd>{formatVnd(book.listPrice)}</dd>
              </div>
            )}
            {canRent && (
              <div>
                <dt>Thuê từ</dt>
                <dd>{formatVnd(book.rentalPriceDay)}<span className="hero-spotlight-unit">/ngày</span></dd>
              </div>
            )}
          </dl>
        )}

        <div className="hero-actions">
          <Link className="button" href={`/sach/${book.id}`}>Xem cuốn này</Link>
          <Link className="button button-secondary" href="/sach">Khám phá toàn bộ sách</Link>
        </div>
      </div>

      <div className="hero-spotlight-cover" aria-hidden="true">
        <BookCover src={book.imageUrl} alt="" variant="detail" className="hero-spotlight-image" />
      </div>
    </section>
  );
}

/** Shown when every shelf is empty or unreachable — states the offer without inventing a book. */
function HeroFallback() {
  return (
    <section className="hero-spotlight hero-spotlight--empty" aria-labelledby="hero-title">
      <div className="hero-spotlight-body">
        <p className="eyebrow">Sách Nhà</p>
        <h1 id="hero-title" className="hero-spotlight-title">Nơi những trang sách tìm được người đọc.</h1>
        <p className="hero-spotlight-blurb">
          Khám phá hàng ngàn cuốn sách hay với hình thức <strong>mua sở hữu</strong> hoặc{" "}
          <strong>thuê linh hoạt</strong> tiết kiệm chi phí.
        </p>
        <div className="hero-actions">
          <Link className="button" href="/sach">Khám phá toàn bộ sách</Link>
        </div>
      </div>
    </section>
  );
}
