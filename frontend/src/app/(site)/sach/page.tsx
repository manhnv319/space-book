import Link from "next/link";
import { unstable_rethrow } from "next/navigation";
import { getBestsellers, getBooks, getBooksByCategory, getNewArrivals, searchBooks } from "@/lib/services/book-service";
import { BookCard } from "@/components/book-card";
import { Reveal } from "@/components/ui/reveal";
import { Pagination } from "@/components/ui/pagination";

interface CatalogPageProps {
  searchParams: Promise<{ q?: string; page?: string; categoryId?: string; collection?: string }>;
}

export default async function CatalogPage({ searchParams }: CatalogPageProps) {
  const { q: searchQuery, page: pageStr, categoryId: categoryIdStr, collection } = await searchParams;
  const currentPage = pageStr && /^\d+$/.test(pageStr) ? Number(pageStr) : 0;
  const pageSize = 12;
  // Validate before it ever reaches a query string sent to the backend.
  const categoryId = categoryIdStr && /^\d+$/.test(categoryIdStr) ? Number(categoryIdStr) : undefined;

  let booksData;
  try {
    if (searchQuery) {
      booksData = await searchBooks(searchQuery, currentPage, pageSize);
    } else if (categoryId) {
      booksData = await getBooksByCategory(categoryId, currentPage, pageSize);
    } else if (collection === "new") {
      booksData = await getNewArrivals(currentPage, pageSize);
    } else if (collection === "bestseller") {
      booksData = await getBestsellers(currentPage, pageSize);
    } else {
      booksData = await getBooks(currentPage, pageSize);
    }
  } catch (error) {
    unstable_rethrow(error);
    console.error("Failed to load catalog books:", error);
    booksData = { content: [], page: 0, size: pageSize, totalElements: 0, totalPages: 0 };
  }

  const books = booksData.content || [];

  return (
    <div className="catalog-container">
      <div className="catalog-header">
        <h1>{searchQuery ? `Tìm kiếm: "${searchQuery}"` : collection === "new" ? "Sách mới về" : collection === "bestseller" ? "Sách bán chạy" : "Danh Mục Sách"}</h1>
        <p className="lead">
          {searchQuery
            ? `Tìm thấy ${booksData.totalElements} kết quả phù hợp`
            : collection ? "Những tựa sách được VelstrongBook chọn cho bạn." : "Tất cả các đầu sách sẵn có để Mua hoặc Thuê với giá tốt nhất."}
        </p>
      </div>

      {books.length > 0 ? (
        <>
          <div className="book-grid">
            {books.map((book, index) => (
              <Reveal key={book.id} index={index % 8}>
                <BookCard book={book} />
              </Reveal>
            ))}
          </div>

        </>
      ) : (
        <div className="empty-state page-section">
          <h2>Không tìm thấy cuốn sách nào</h2>
          <p>Rất tiếc, hiện tại không có sản phẩm nào phù hợp với yêu cầu của bạn.</p>
          <Link href="/sach" className="button style-button-secondary" style={{ marginTop: "1rem" }}>
            Quay lại tất cả sách
          </Link>
          <Pagination
            currentPage={currentPage}
            totalPages={booksData.totalPages}
            hrefForPage={(page) => `/sach?page=${page}${searchQuery ? `&q=${encodeURIComponent(searchQuery)}` : ""}${categoryId ? `&categoryId=${categoryId}` : ""}${collection ? `&collection=${collection}` : ""}`}
            ariaLabel="Phân trang danh mục sách"
          />
        </div>
      )}
      {books.length > 0 && (
        <Pagination
          currentPage={currentPage}
          totalPages={booksData.totalPages}
          hrefForPage={(page) => `/sach?page=${page}${searchQuery ? `&q=${encodeURIComponent(searchQuery)}` : ""}${categoryId ? `&categoryId=${categoryId}` : ""}${collection ? `&collection=${collection}` : ""}`}
          ariaLabel="Phân trang danh mục sách"
        />
      )}
    </div>
  );
}
