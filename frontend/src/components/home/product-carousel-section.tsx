import Link from "next/link";

import { BookCard } from "@/components/book-card";
import { ScrollCarousel } from "@/components/ui/scroll-carousel";
import type { HomeProductSection } from "@/lib/home/home-view-model";

export function ProductCarouselSection({ section }: { section: HomeProductSection }) {
  if (section.status === "empty") return null;
  if (section.status === "error") {
    return <section className="home-section section-state" aria-labelledby={`${section.id}-title`}><h2 id={`${section.id}-title`}>{section.title}</h2><p>Chưa thể tải mục này. Vui lòng thử lại sau.</p></section>;
  }

  return (
    <section className="home-section product-carousel-section" aria-labelledby={`${section.id}-title`}>
      <ScrollCarousel
        header={<div><p className="eyebrow">{section.eyebrow}</p><h2 id={`${section.id}-title`}>{section.title}</h2></div>}
        headerAction={<Link className="text-link" href={section.viewAllHref}>Xem thêm <span aria-hidden="true">→</span></Link>}
        label={section.title}
      >
        {section.books.map((book) => <li className="book-shelf-item" key={book.id}><BookCard badge={section.id === "new" ? "Mới về" : undefined} book={book} /></li>)}
      </ScrollCarousel>
    </section>
  );
}
