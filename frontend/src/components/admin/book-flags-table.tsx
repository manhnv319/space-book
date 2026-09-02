import { updateBookFlagsAction } from "@/app/actions/admin-books";
import { BookCoverUploadForm } from "@/components/admin/book-cover-upload-form";
import type { Book } from "@/lib/types/book";

/** Each toggle is its own `<form>` submitting BOTH flags to preserve the other one. */
export function BookFlagsTable({ books }: Readonly<{ books: Book[] }>) {
  if (books.length === 0) return null;

  return (
    <table className="admin-table">
      <thead>
        <tr>
          <th>Sách</th>
          <th>Bìa</th>
          <th>Nổi bật</th>
          <th>Bán chạy</th>
        </tr>
      </thead>
      <tbody>
        {books.map((book) => (
          <tr key={book.id}>
            <td>{book.title}</td>
            <td><BookCoverUploadForm bookId={book.id} /></td>
            <td>
              <form action={updateBookFlagsAction}>
                <input type="hidden" name="bookId" value={book.id} />
                <input type="hidden" name="isFeatured" value={String(!book.isFeatured)} />
                <input type="hidden" name="isBestseller" value={String(book.isBestseller)} />
                <button className={`button button-small${book.isFeatured ? "" : " button-secondary"}`} type="submit">
                  {book.isFeatured ? "Bỏ nổi bật" : "Đánh dấu nổi bật"}
                </button>
              </form>
            </td>
            <td>
              <form action={updateBookFlagsAction}>
                <input type="hidden" name="bookId" value={book.id} />
                <input type="hidden" name="isFeatured" value={String(book.isFeatured)} />
                <input type="hidden" name="isBestseller" value={String(!book.isBestseller)} />
                <button className={`button button-small${book.isBestseller ? "" : " button-secondary"}`} type="submit">
                  {book.isBestseller ? "Bỏ bán chạy" : "Đánh dấu bán chạy"}
                </button>
              </form>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
