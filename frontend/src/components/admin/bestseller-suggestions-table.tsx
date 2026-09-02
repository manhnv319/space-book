import { updateBookFlagsAction } from "@/app/actions/admin-books";
import type { BestsellerSuggestion } from "@/lib/types/admin";

/** Order is already `soldQuantity DESC` from BE (Phase 02 aggregate query). */
export function BestsellerSuggestionsTable({ suggestions }: Readonly<{ suggestions: BestsellerSuggestion[] }>) {
  if (suggestions.length === 0) {
    return <p className="admin-empty">Chưa có dữ liệu bán hàng trong khoảng thời gian này.</p>;
  }

  return (
    <table className="admin-table">
      <thead>
        <tr>
          <th>#</th>
          <th>Sách</th>
          <th>Số lượng bán</th>
          <th>Cờ hiện tại</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        {suggestions.map((item, index) => (
          <tr key={item.bookId}>
            <td>{index + 1}</td>
            <td>{item.title}</td>
            <td>{item.soldQuantity}</td>
            <td className="admin-table-muted">{item.isBestseller ? "Đã gắn bán chạy" : "—"}</td>
            <td>
              {item.isBestseller ? (
                <span className="admin-table-muted">Đã gắn cờ</span>
              ) : (
                <form action={updateBookFlagsAction}>
                  <input type="hidden" name="bookId" value={item.bookId} />
                  <input type="hidden" name="isFeatured" value={String(item.isFeatured)} />
                  <input type="hidden" name="isBestseller" value="true" />
                  <button className="button button-small button-secondary" type="submit">
                    Đánh dấu bán chạy
                  </button>
                </form>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
