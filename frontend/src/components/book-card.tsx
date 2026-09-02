import Link from "next/link";

import { ProductCardActions } from "@/components/product/product-card-actions";
import { Badge } from "@/components/ui/badge";
import { BookCover } from "@/components/ui/book-cover";
import { formatVnd } from "@/lib/format/currency";
import type { Book } from "@/lib/types/book";

interface BookCardProps {
  book: Book;
  badge?: string;
}

export function BookCard({ book, badge }: BookCardProps) {
  const authorOrPublisher = book.authors?.filter(Boolean).join(", ") || book.publisher;
  const canBuy = book.listPrice > 0;
  const canRent = book.rentalPriceDay > 0;
  const displayBadge = badge ?? (book.isFeatured ? "Nổi bật" : book.isBestseller ? "Bán chạy" : book.categories?.[0]);

  return (
    <article className="book-card">
      <Link aria-label={`Xem ${book.title}`} className="book-card-image-wrapper" href={`/sach/${book.id}`}>
        <BookCover alt={`Bìa sách ${book.title}`} src={book.imageUrl} variant="card" />
        {displayBadge ? <Badge overlay>{displayBadge}</Badge> : null}
      </Link>
      <div className="book-card-content">
        <p className="book-card-format">{book.format}</p>
        <h3 className="book-card-title"><Link href={`/sach/${book.id}`}>{book.title}</Link></h3>
        {authorOrPublisher ? <p className="book-card-publisher">{authorOrPublisher}</p> : null}
        <div className="book-card-pricing">
          {canBuy ? <p><span>Mua</span><strong>{formatVnd(book.listPrice)}</strong></p> : null}
          {canRent ? <p className="book-card-rental-price"><span>Thuê từ</span><strong>{formatVnd(book.rentalPriceDay)}<small>/ngày</small></strong></p> : null}
        </div>
        <ProductCardActions bookId={book.id} canBuy={canBuy} canRent={canRent} />
      </div>
    </article>
  );
}
