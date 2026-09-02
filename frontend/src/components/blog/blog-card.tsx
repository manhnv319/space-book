import Link from "next/link";

import { BlogPostSummary } from "@/lib/types/blog";
import { BlogCover } from "@/components/blog/blog-cover";
import { BlogMeta } from "@/components/blog/blog-meta";

interface BlogCardProps {
  post: BlogPostSummary;
}

export function BlogCard({ post }: BlogCardProps) {
  return (
    <Link href={`/bai-viet/${post.slug}`} className="blog-card">
      <div className="blog-card-image-wrapper">
        <BlogCover src={post.coverImageUrl} alt={post.title} variant="card" />
      </div>
      <div className="blog-card-content">
        <BlogMeta publishedAt={post.publishedAt} />
        <h3 className="blog-card-title">{post.title}</h3>
        {post.excerpt && <p className="blog-card-excerpt">{post.excerpt}</p>}
      </div>
    </Link>
  );
}
