/**
 * Chuẩn hoá chuỗi tiếng Việt để so khớp khi gõ tìm.
 *
 * Người dùng gõ "ha noi" phải ra "Thành phố Hà Nội", và gõ "da nang" phải ra
 * "Đà Nẵng". NFD tách được dấu thanh và dấu mũ ra khỏi nguyên âm, nhưng **không**
 * tách được chữ đ/Đ — nó là một ký tự riêng, không phải d + dấu — nên phải thay
 * tay trước khi normalize.
 *
 * Thuần tuý, không I/O: chạy được cả ở client lẫn server và test được trực tiếp.
 */
export function normalizeVietnamese(value: string): string {
  return value
    .replace(/đ/g, "d")
    .replace(/Đ/g, "D")
    .normalize("NFD")
    // U+0300–U+036F là khối dấu kết hợp sinh ra sau khi tách
    .replace(/[̀-ͯ]/g, "")
    .toLowerCase()
    .trim();
}

/**
 * Khớp khi mọi từ trong truy vấn đều xuất hiện trong nhãn, không cần liền nhau.
 *
 * Nhờ vậy gõ "hoa cau giay" vẫn ra "Phường Yên Hoà" nếu tên đầy đủ có cả hai
 * phần, và không bắt người dùng phải nhớ tiền tố "Thành phố"/"Phường".
 */
export function matchesQuery(label: string, query: string): boolean {
  const normalizedQuery = normalizeVietnamese(query);
  if (!normalizedQuery) return true;
  const haystack = normalizeVietnamese(label);
  return normalizedQuery.split(/\s+/).every((token) => haystack.includes(token));
}
