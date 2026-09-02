import { BackendError } from "@/lib/bff/backend-error";

export function resolveBackendUrl(path: string, baseUrl = process.env.BOOK_API_BASE_URL): URL {
  if (!baseUrl) throw new BackendError(500, "Dịch vụ sách chưa được cấu hình.");
  if (!path.startsWith("/") || path.startsWith("//")) {
    throw new BackendError(500, "Đường dẫn BFF không hợp lệ.");
  }

  const base = new URL(baseUrl);
  const target = new URL(path, base);
  if (target.origin !== base.origin) {
    throw new BackendError(500, "Đường dẫn BFF không hợp lệ.");
  }

  return target;
}
