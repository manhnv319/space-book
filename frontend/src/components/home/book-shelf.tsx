import type { CSSProperties } from "react";
import Link from "next/link";

import { BookCard } from "@/components/book-card";
import { Reveal } from "@/components/ui/reveal";
import type { Book } from "@/lib/types/book";

interface BookShelfProps {
  eyebrow: string;
  title: string;
  viewAllHref: string;
  books: Book[];
  /** Scroll-reveal stagger slot among sibling shelves (see motion.css `--i`). */
  index?: number;
}

/**
 * Server component: horizontal-scrolling shelf of books. Pure CSS scroll
 * (overflow-x + scroll-snap), no carousel JS. Returns null when `books` is
 * empty — callers must never render an orphaned shelf title (no fake data).
 */
export function BookShelf({ eyebrow, title, viewAllHref, books, index }: BookShelfProps) {
  if (books.length === 0) return null;

  return (
    <Reveal index={index} className="book-shelf">
      <div className="section-header book-shelf-header">
        <div>
          <p className="eyebrow">{eyebrow}</p>
          <h2>{title}</h2>
        </div>
        <Link href={viewAllHref} className="text-link">
          Xem tất cả &rarr;
        </Link>
      </div>
      <ul className="book-shelf-row stagger" tabIndex={0} aria-label={`Cuộn ngang: ${title}`}>
        {books.map((book, i) => (
          <li key={book.id} className="book-shelf-item" style={{ "--i": i } as CSSProperties}>
            <BookCard book={book} />
          </li>
        ))}
      </ul>
    </Reveal>
  );
}
