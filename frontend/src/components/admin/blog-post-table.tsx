import Link from "next/link";

import { deleteBlogPostAction, publishBlogPostAction, unpublishBlogPostAction } from "@/app/actions/admin-blog";
import { Badge } from "@/components/ui/badge";
import type { AdminBlogPostSummary } from "@/lib/types/admin";

const STATUS_LABEL: Record<AdminBlogPostSummary["status"], string> = {
  DRAFT: "Nháp",
  PUBLISHED: "Đã đăng",
  ARCHIVED: "Lưu trữ",
};

function formatDate(value: string | null): string {
  if (!value) return "—";
  return new Intl.DateTimeFormat("vi-VN", { dateStyle: "medium" }).format(new Date(value));
}

/** Every action below is a plain `<form action>` — 0 client JS to publish/unpublish/delete. */
export function BlogPostTable({ posts }: Readonly<{ posts: AdminBlogPostSummary[] }>) {
  if (posts.length === 0) {
    return <p className="admin-empty">Chưa có bài viết nào khớp bộ lọc.</p>;
  }

  return (
    <table className="admin-table">
      <thead>
        <tr>
          <th>Tiêu đề</th>
          <th>Slug</th>
          <th>Trạng thái</th>
          <th>Ngày đăng</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        {posts.map((post) => (
          <tr key={post.id}>
            <td>{post.title}</td>
            <td className="admin-table-muted">{post.slug}</td>
            <td>
              <Badge tone={post.status === "PUBLISHED" ? "default" : "muted"}>{STATUS_LABEL[post.status]}</Badge>
            </td>
            <td>{formatDate(post.publishedAt)}</td>
            <td className="admin-table-actions">
              <Link className="button button-small button-secondary" href={`/admin/bai-viet/${post.id}`}>
                Sửa
              </Link>
              {post.status === "PUBLISHED" ? (
                <form action={unpublishBlogPostAction}>
                  <input type="hidden" name="id" value={post.id} />
                  <button className="button button-small button-secondary" type="submit">
                    Gỡ đăng
                  </button>
                </form>
              ) : (
                <form action={publishBlogPostAction}>
                  <input type="hidden" name="id" value={post.id} />
                  <button className="button button-small button-secondary" type="submit">
                    Đăng bài
                  </button>
                </form>
              )}
              <form action={deleteBlogPostAction}>
                <input type="hidden" name="id" value={post.id} />
                <button className="button button-small button-danger" type="submit">
                  Xoá vĩnh viễn
                </button>
              </form>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
