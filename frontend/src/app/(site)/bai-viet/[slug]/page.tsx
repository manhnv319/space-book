import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";

import { getBlogPostBySlug } from "@/lib/services/blog-service";
import { getBookById } from "@/lib/services/book-service";
import { parseMarkdown } from "@/lib/markdown/parse-markdown";
import { MarkdownContent } from "@/components/blog/markdown-content";
import { BlogCover } from "@/components/blog/blog-cover";
import { BlogMeta } from "@/components/blog/blog-meta";
import { BookCard } from "@/components/book-card";

interface BlogDetailPageProps {
  params: Promise<{ slug: string }>;
}

const EXCERPT_MAX_LENGTH = 160;

function truncateExcerpt(excerpt: string | null): string | undefined {
  if (!excerpt) return undefined;
  return excerpt.length > EXCERPT_MAX_LENGTH ? `${excerpt.slice(0, EXCERPT_MAX_LENGTH - 1)}…` : excerpt;
}

export async function generateMetadata({ params }: BlogDetailPageProps): Promise<Metadata> {
  const { slug } = await params;
  const post = await getBlogPostBySlug(slug);
  if (!post) return { title: "Bài viết không tồn tại" };

  return {
    title: post.title,
    description: truncateExcerpt(post.excerpt),
  };
}

export default async function BlogDetailPage({ params }: BlogDetailPageProps) {
  const { slug } = await params;
  const post = await getBlogPostBySlug(slug);
  if (!post) notFound();

  const nodes = parseMarkdown(post.content);
  const relatedBook = post.bookId ? await getBookById(post.bookId) : null;

  return (
    <div className="book-detail-container blog-detail-container">
      <nav className="breadcrumb">
        <Link href="/">Trang chủ</Link>
        <span className="breadcrumb-separator">/</span>
        <Link href="/bai-viet">Bài viết</Link>
        <span className="breadcrumb-separator">/</span>
        <span className="breadcrumb-current">{post.title}</span>
      </nav>

      <article className="blog-article">
        <div className="blog-article-cover">
          <BlogCover src={post.coverImageUrl} alt={post.title} variant="detail" />
        </div>

        <h1 className="blog-article-title">{post.title}</h1>
        <BlogMeta publishedAt={post.publishedAt} content={post.content} className="blog-article-meta" />

        <MarkdownContent nodes={nodes} />

        {relatedBook && (
          <div className="blog-related-book page-section">
            <h2>Sách được nhắc tới</h2>
            <BookCard book={relatedBook} />
          </div>
        )}

        <Link href="/bai-viet" className="text-link blog-back-link">
          &larr; Tất cả bài viết
        </Link>
      </article>
    </div>
  );
}
