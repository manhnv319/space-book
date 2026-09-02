import { getBlogPosts } from "@/lib/services/blog-service";
import type { BlogPostPageResponse } from "@/lib/types/blog";
import { BlogCard } from "@/components/blog/blog-card";
import { Reveal } from "@/components/ui/reveal";
import { Pagination } from "@/components/ui/pagination";

interface BlogListPageProps {
  searchParams: Promise<{ page?: string }>;
}

const PAGE_SIZE = 9;

export default async function BlogListPage({ searchParams }: BlogListPageProps) {
  const { page: pageStr } = await searchParams;
  const currentPage = pageStr && /^\d+$/.test(pageStr) ? Number(pageStr) : 0;

  // `null` means "backend failed" (distinct from a real empty list) — never
  // fake an empty result on error, see phase constraints.
  let postsData: BlogPostPageResponse | null = null;
  try {
    postsData = await getBlogPosts(currentPage, PAGE_SIZE);
  } catch (error) {
    console.error("Failed to load blog posts:", error);
  }

  return (
    <div className="catalog-container blog-list-container">
      <div className="catalog-header">
        <h1>Bài viết</h1>
        <p className="lead">Câu chuyện, hướng dẫn và cập nhật từ VelstrongBook.</p>
      </div>

      {postsData === null && (
        <div className="empty-state page-section" role="alert">
          <h2>Không tải được bài viết</h2>
          <p>Đã có lỗi xảy ra khi kết nối máy chủ. Vui lòng thử lại sau.</p>
        </div>
      )}

      {postsData !== null && postsData.content.length === 0 && (
        <div className="empty-state page-section">
          <h2>Chưa có bài viết nào</h2>
          <p>Hãy quay lại sau — chúng tôi đang chuẩn bị những nội dung đầu tiên.</p>
          <Pagination
            currentPage={currentPage}
            totalPages={postsData.totalPages}
            hrefForPage={(page) => `/bai-viet?page=${page}`}
            ariaLabel="Phân trang bài viết"
          />
        </div>
      )}

      {postsData !== null && postsData.content.length > 0 && (
        <>
          <div className="blog-grid">
            {postsData.content.map((post, index) => (
              <Reveal key={post.id} index={index % 8}>
                <BlogCard post={post} />
              </Reveal>
            ))}
          </div>

          <Pagination
            currentPage={currentPage}
            totalPages={postsData.totalPages}
            hrefForPage={(page) => `/bai-viet?page=${page}`}
            ariaLabel="Phân trang bài viết"
          />
        </>
      )}
    </div>
  );
}
