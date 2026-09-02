import Link from "next/link";

import { BlogPostTable } from "@/components/admin/blog-post-table";
import { BlogPostCreateDialog } from "@/components/admin/blog-post-create-dialog";
import { getAdminBlogPosts } from "@/lib/services/admin-blog-service";
import type { AdminBlogPostPageResponse } from "@/lib/types/admin";
import type { BlogPostStatus } from "@/lib/types/blog";
import { Pagination } from "@/components/ui/pagination";

interface AdminBlogListPageProps {
  searchParams: Promise<{ status?: string; page?: string }>;
}

const PAGE_SIZE = 20;
const STATUS_FILTERS: Array<{ value: BlogPostStatus | undefined; label: string }> = [
  { value: undefined, label: "Tất cả" },
  { value: "DRAFT", label: "Nháp" },
  { value: "PUBLISHED", label: "Đã đăng" },
  { value: "ARCHIVED", label: "Lưu trữ" },
];
const KNOWN_STATUSES: BlogPostStatus[] = ["DRAFT", "PUBLISHED", "ARCHIVED"];

export default async function AdminBlogListPage({ searchParams }: AdminBlogListPageProps) {
  const { status: statusStr, page: pageStr } = await searchParams;
  const status = KNOWN_STATUSES.includes(statusStr as BlogPostStatus) ? (statusStr as BlogPostStatus) : undefined;
  const currentPage = pageStr && /^\d+$/.test(pageStr) ? Number(pageStr) : 0;

  let postsData: AdminBlogPostPageResponse | null = null;
  try {
    postsData = await getAdminBlogPosts(status, currentPage, PAGE_SIZE);
  } catch (error) {
    console.error("Failed to load admin blog posts:", error);
  }

  return (
    <div className="admin-page">
      <div className="admin-page-header">
        <h1>Bài viết</h1>
        <BlogPostCreateDialog />
      </div>

      <nav aria-label="Lọc theo trạng thái" className="admin-filter-tabs">
        {STATUS_FILTERS.map((filter) => (
          <Link
            key={filter.label}
            className={`admin-filter-tab${status === filter.value ? " active" : ""}`}
            href={filter.value ? `/admin/bai-viet?status=${filter.value}` : "/admin/bai-viet"}
          >
            {filter.label}
          </Link>
        ))}
      </nav>

      {postsData === null ? (
        <p className="admin-empty">Không tải được danh sách bài viết.</p>
      ) : (
        <>
          <BlogPostTable posts={postsData.content} />
          <Pagination
            currentPage={currentPage}
            totalPages={postsData.totalPages}
            hrefForPage={(page) => `/admin/bai-viet?page=${page}${status ? `&status=${status}` : ""}`}
            ariaLabel="Phân trang bài viết quản trị"
          />
        </>
      )}
    </div>
  );
}
