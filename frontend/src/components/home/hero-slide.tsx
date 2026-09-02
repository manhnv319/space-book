"use client";

import Link from "next/link";

import { BookCover } from "@/components/ui/book-cover";
import { formatVnd } from "@/lib/format/currency";
import type { HeroSlide as HeroSlideModel } from "@/lib/home/home-view-model";

export function HeroSlide({ slide, eager, headingLevel }: { slide: HeroSlideModel; eager: boolean; headingLevel: "h1" | "h2" }) {
  const { book } = slide;
  const author = book.authors?.filter(Boolean).join(", ") || book.publisher;
  const Heading = headingLevel;
  return (
    <article className="hero-slide">
      <div className="hero-slide-copy">
        <p className="eyebrow">{slide.eyebrow}</p>
        <Heading>{book.title}</Heading>
        {author ? <p className="hero-slide-author">{author}</p> : null}
        {book.description ? <p className="hero-slide-description">{book.description}</p> : null}
        <dl className="hero-slide-prices">
          {book.listPrice > 0 ? <div><dt>Mua sở hữu</dt><dd>{formatVnd(book.listPrice)}</dd></div> : null}
          {book.rentalPriceDay > 0 ? <div><dt>Thuê từ</dt><dd>{formatVnd(book.rentalPriceDay)}<small>/ngày</small></dd></div> : null}
        </dl>
        <div className="hero-actions"><Link className="button" href={`/sach/${book.id}`}>Xem cuốn sách</Link><Link className="text-link" href="/sach">Khám phá toàn bộ <span aria-hidden="true">→</span></Link></div>
      </div>
      <div className="hero-slide-cover"><BookCover alt={`Bìa sách ${book.title}`} eager={eager} src={book.imageUrl} variant="detail" /></div>
    </article>
  );
}
