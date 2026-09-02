import { unstable_rethrow } from "next/navigation";

import { CampaignBanner } from "@/components/home/campaign-banner";
import { CampaignShortcutGrid } from "@/components/home/campaign-shortcut-grid";
import { EditorialStrip } from "@/components/home/editorial-strip";
import { HeroCarousel } from "@/components/home/hero-carousel";
import { ProductCarouselSection } from "@/components/home/product-carousel-section";
import { QuickCategoryGrid } from "@/components/home/quick-category-grid";
import { RentalSteps } from "@/components/home/rental-steps";
import { ValueProps } from "@/components/home/value-props";
import { homepageCategories } from "@/lib/home/home-content-config";
import { articleHasContent, statusForBooks, uniqueHeroSlides, type HomeProductSection } from "@/lib/home/home-view-model";
import { getBestsellers, getBooksByCategory, getFeaturedBooks, getNewArrivals } from "@/lib/services/book-service";
import { getBlogPosts } from "@/lib/services/blog-service";
import { getCategories } from "@/lib/services/category-service";
import type { BookPageResponse } from "@/lib/types/book";
import type { BlogPostPageResponse } from "@/lib/types/blog";

const EMPTY_PAGE: BookPageResponse = { content: [], page: 0, size: 0, totalElements: 0, totalPages: 0 };
const EMPTY_POSTS: BlogPostPageResponse = { content: [], page: 0, size: 0, totalElements: 0, totalPages: 0 };

function result<T>(value: PromiseSettledResult<T>, label: string, fallback: T): { data: T; failed: boolean } {
  if (value.status === "fulfilled") return { data: value.value, failed: false };
  unstable_rethrow(value.reason);
  console.error(`Homepage section "${label}" failed to load:`, value.reason);
  return { data: fallback, failed: true };
}

export default async function HomePage() {
  const [featuredResult, bestsellersResult, newResult, categoriesResult, postsResult] = await Promise.allSettled([
    getFeaturedBooks(0, 12), getBestsellers(0, 12), getNewArrivals(0, 12), getCategories(), getBlogPosts(0, 12),
  ]);
  const featured = result(featuredResult, "featured", EMPTY_PAGE);
  const bestsellers = result(bestsellersResult, "bestsellers", EMPTY_PAGE);
  const newArrivals = result(newResult, "new-arrivals", EMPTY_PAGE);
  const categories = result(categoriesResult, "categories", []);
  const posts = result(postsResult, "blog-posts", EMPTY_POSTS);
  const selectedCategories = homepageCategories(categories.data, 2);
  const categoryResults = await Promise.allSettled(selectedCategories.map((category) => getBooksByCategory(category.id, 0, 8)));
  const categorySections: HomeProductSection[] = categoryResults.map((categoryResult, index) => {
    const category = selectedCategories[index];
    const value = result(categoryResult, `category-${category.slug ?? category.id}`, EMPTY_PAGE);
    return { id: `category-${category.id}`, eyebrow: "Theo chủ đề", title: `${category.name} nổi bật`, viewAllHref: `/sach?categoryId=${category.id}`, books: value.data.content, status: statusForBooks(value.data.content, value.failed) };
  });
  const sections: HomeProductSection[] = [
    { id: "new", eyebrow: "Vừa cập bến", title: "Sách mới về", viewAllHref: "/sach", books: newArrivals.data.content, status: statusForBooks(newArrivals.data.content, newArrivals.failed) },
    { id: "bestseller", eyebrow: "Được chọn nhiều", title: "Sách bán chạy", viewAllHref: "/sach", books: bestsellers.data.content, status: statusForBooks(bestsellers.data.content, bestsellers.failed) },
    { id: "featured", eyebrow: "Chọn lọc", title: "Sách nổi bật", viewAllHref: "/sach", books: featured.data.content, status: statusForBooks(featured.data.content, featured.failed) },
    ...categorySections,
  ];
  const heroSlides = uniqueHeroSlides([
    { books: featured.data.content, eyebrow: "Sách nổi bật" }, { books: bestsellers.data.content, eyebrow: "Bán chạy" }, { books: newArrivals.data.content, eyebrow: "Sách mới về" },
  ]);

  return <div className="home-container">
    <HeroCarousel slides={heroSlides} />
    <QuickCategoryGrid categories={categories.data} />
    {sections.slice(0, 2).map((section) => <ProductCarouselSection key={section.id} section={section} />)}
    <CampaignBanner book={heroSlides[0]?.book ?? null} />
    {sections.slice(2, 5).map((section) => <ProductCarouselSection key={section.id} section={section} />)}
    <CampaignShortcutGrid />
    <RentalSteps />
    <EditorialStrip posts={posts.data.content.filter(articleHasContent).slice(0, 4)} />
    <ValueProps />
  </div>;
}
