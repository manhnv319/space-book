import Link from "next/link";

import { CategoryMenu } from "@/components/category-menu";
import { getCategories } from "@/lib/services/category-service";
import type { Category } from "@/lib/types/category";

const MAX_NAV_CATEGORIES = 6;

export async function CategoryNav() {
  let categories: Category[] = [];
  try {
    const loadedCategories = await getCategories();
    categories = Array.isArray(loadedCategories) ? loadedCategories : [];
  } catch (error) { console.error("Failed to load categories for nav:", error); }
  const navigationCategories = categories.slice(0, MAX_NAV_CATEGORIES);

  return <>
    <Link href="/">Khám phá</Link>
    {navigationCategories.length ? <CategoryMenu categories={navigationCategories} /> : null}
    <Link href="/sach">Tất cả sách</Link><Link href="/goi-thue">Gói thuê</Link><Link href="/bai-viet">Bài viết</Link>
  </>;
}
