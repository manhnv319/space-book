import type { Category } from "@/lib/types/category";

export const ANNOUNCEMENTS = [
  "Thuê linh hoạt theo ngày, tuần hoặc tháng.",
  "Khám phá sách mới và những tựa sách được yêu thích.",
  "Hoàn cọc khi trả sách đúng hạn và nguyên vẹn.",
] as const;

const PREFERRED_CATEGORY_SLUGS = ["cong-nghe", "kinh-te", "tam-ly-hoc", "khoa-hoc", "ky-nang-song", "van-hoc"];

export function homepageCategories(categories: Category[], limit = 4): Category[] {
  const rank = new Map(PREFERRED_CATEGORY_SLUGS.map((slug, index) => [slug, index]));
  return categories
    .filter((category) => rank.has(category.slug ?? ""))
    .sort((a, b) => (rank.get(a.slug ?? "") ?? 99) - (rank.get(b.slug ?? "") ?? 99))
    .slice(0, limit);
}
