import { notFound } from "next/navigation";

import { BlogPostForm } from "@/components/admin/blog-post-form";
import { getAdminBlogPostById } from "@/lib/services/admin-blog-service";

interface EditBlogPostPageProps {
  params: Promise<{ id: string }>;
}

export default async function EditBlogPostPage({ params }: EditBlogPostPageProps) {
  const { id } = await params;
  const numericId = Number(id);
  if (!Number.isInteger(numericId) || numericId <= 0) notFound();

  const post = await getAdminBlogPostById(numericId);
  if (!post) notFound();

  return (
    <div className="admin-page">
      <h1>Sửa bài viết</h1>
      <BlogPostForm post={post} />
    </div>
  );
}
