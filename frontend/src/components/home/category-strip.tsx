import Link from "next/link";

import { Reveal } from "@/components/ui/reveal";
import type { Category } from "@/lib/types/category";

interface CategoryStripProps {
  categories: Category[];
  index?: number;
}

/**
 * Server component: "browse by subject" strip, the equivalent of the category
 * shortcut row every major bookseller puts directly under the hero.
 *
 * Returns null when there are no categories — an empty strip of chips reads as
 * a broken section, and inventing placeholder subjects would misrepresent the
 * catalogue.
 */
export function CategoryStrip({ categories, index }: CategoryStripProps) {
  if (categories.length === 0) return null;

  return (
    <Reveal index={index} className="category-strip">
      <div className="section-header">
        <div>
          <p className="eyebrow">Duyệt theo chủ đề</p>
          <h2>Bạn đang tìm gì?</h2>
        </div>
        <Link href="/sach" className="text-link">Xem tất cả &rarr;</Link>
      </div>
      <ul className="category-strip-row stagger" aria-label="Danh mục sách">
        {categories.map((category, i) => (
          <li key={category.id} style={{ "--i": i } as React.CSSProperties}>
            <Link href={`/sach?categoryId=${category.id}`} className="category-chip">
              {category.name}
            </Link>
          </li>
        ))}
      </ul>
    </Reveal>
  );
}
