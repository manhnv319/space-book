import Link from "next/link";

import type { Category } from "@/lib/types/category";

const FIXED_LINKS = [
  { href: "/sach", label: "Tất cả sách", detail: "Khám phá" },
  { href: "/sach?collection=new", label: "Sách mới", detail: "Vừa về" },
  { href: "/sach?collection=bestseller", label: "Bán chạy", detail: "Được chọn" },
  { href: "/goi-thue", label: "Gói thuê", detail: "Đăng nhập để xem" },
];

export function QuickCategoryGrid({ categories }: { categories: Category[] }) {
  const items = [...FIXED_LINKS, ...categories.map((category) => ({ href: `/sach?categoryId=${category.id}`, label: category.name, detail: "Theo chủ đề" }))].slice(0, 10);
  if (!items.length) return null;
  return (
    <section className="home-section quick-category-section" aria-labelledby="quick-category-title">
      <div className="section-header"><div><p className="eyebrow">Khám phá</p><h2 id="quick-category-title">Bạn có thể đang tìm…</h2></div></div>
      <ul className="quick-category-grid">
        {items.map((item) => <li key={item.href}><Link href={item.href}><span>{item.detail}</span><strong>{item.label}</strong></Link></li>)}
      </ul>
    </section>
  );
}
