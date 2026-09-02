import type { Book } from "@/lib/types/book";
import type { BlogPostSummary } from "@/lib/types/blog";

export type HomeSectionStatus = "ready" | "empty" | "error";

export type HomeProductSection = {
  id: string;
  eyebrow: string;
  title: string;
  viewAllHref: string;
  books: Book[];
  status: HomeSectionStatus;
};

export type HeroSlide = {
  book: Book;
  eyebrow: string;
};

export function statusForBooks(books: Book[], failed: boolean): HomeSectionStatus {
  if (failed) return "error";
  return books.length > 0 ? "ready" : "empty";
}

export function uniqueHeroSlides(groups: Array<{ books: Book[]; eyebrow: string }>, limit = 3): HeroSlide[] {
  const seen = new Set<number>();
  const slides: HeroSlide[] = [];
  for (const group of groups) {
    for (const book of group.books) {
      if (seen.has(book.id)) continue;
      seen.add(book.id);
      slides.push({ book, eyebrow: group.eyebrow });
      if (slides.length === limit) return slides;
    }
  }
  return slides;
}

export function formatArticleDate(value: string | null): string | null {
  if (!value) return null;
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime())
    ? null
    : new Intl.DateTimeFormat("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric" }).format(parsed);
}

export function articleHasContent(post: BlogPostSummary): boolean {
  return Boolean(post.slug && post.excerpt && post.title && !post.title.startsWith("Live post "));
}
