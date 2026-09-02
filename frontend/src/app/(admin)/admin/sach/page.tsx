import { BestsellerSuggestionsTable } from "@/components/admin/bestseller-suggestions-table";
import { BookFlagsTable } from "@/components/admin/book-flags-table";
import { BookCopyManagement } from "@/components/admin/book-copy-management";
import { getBestsellerSuggestions, getBookCopies } from "@/lib/services/admin-book-service";
import { searchBooks } from "@/lib/services/book-service";
import type { BestsellerSuggestion } from "@/lib/types/admin";

interface AdminBooksPageProps {
  searchParams: Promise<{ q?: string; days?: string }>;
}

const DAYS_OPTIONS = [30, 90, 365];

export default async function AdminBooksPage({ searchParams }: AdminBooksPageProps) {
  const { q, days: daysStr } = await searchParams;
  const days = daysStr && DAYS_OPTIONS.includes(Number(daysStr)) ? Number(daysStr) : 90;

  let suggestions: BestsellerSuggestion[] | null = null;
  try {
    suggestions = await getBestsellerSuggestions(20, days);
  } catch (error) {
    console.error("Failed to load bestseller suggestions:", error);
  }

  let searchResults = null;
  let copies = null;
  if (q) {
    try {
      searchResults = await searchBooks(q, 0, 20);
      if (searchResults.content.length === 1) copies = await getBookCopies(searchResults.content[0].id);
    } catch (error) {
      console.error("Failed to search books for admin flags:", error);
    }
  }

  return (
    <div className="admin-page">
      <h1>Sách nổi bật &amp; bán chạy</h1>

      <section className="admin-section">
        <div className="admin-section-header">
          <h2>Gợi ý bán chạy</h2>
          <form className="admin-filter-form" action="/admin/sach" method="GET">
            <label>
              Trong
              <select name="days" defaultValue={days}>
                {DAYS_OPTIONS.map((option) => (
                  <option key={option} value={option}>
                    {option} ngày
                  </option>
                ))}
              </select>
            </label>
            {q ? <input type="hidden" name="q" value={q} /> : null}
            <button type="submit" className="button button-small button-secondary">
              Lọc
            </button>
          </form>
        </div>
        {suggestions === null ? (
          <p className="admin-empty">Không tải được gợi ý bán chạy.</p>
        ) : (
          <BestsellerSuggestionsTable suggestions={suggestions} />
        )}
      </section>

      <section className="admin-section">
        <h2>Cờ nổi bật / bán chạy</h2>
        <form className="admin-search-form" action="/admin/sach" method="GET">
          <input type="text" name="q" defaultValue={q} placeholder="Tìm sách theo tên..." />
          <input type="hidden" name="days" value={days} />
          <button type="submit" className="button button-small button-secondary">
            Tìm
          </button>
        </form>
        {searchResults && searchResults.content.length > 0 && <BookFlagsTable books={searchResults.content} />}
        {q && (!searchResults || searchResults.content.length === 0) && (
          <p className="admin-empty">Không tìm thấy sách phù hợp.</p>
        )}
        {searchResults?.content.length === 1 && copies !== null && <BookCopyManagement bookId={searchResults.content[0].id} copies={copies} />}
      </section>
    </div>
  );
}
