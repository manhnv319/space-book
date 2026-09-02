import Link from "next/link";

import { BlogCover } from "@/components/blog/blog-cover";
import { formatArticleDate } from "@/lib/home/home-view-model";
import type { BlogPostSummary } from "@/lib/types/blog";

export function EditorialStrip({ posts }: { posts: BlogPostSummary[] }) {
  if (!posts.length) return null;
  return <section className="editorial-strip home-section" aria-labelledby="editorial-title"><div className="section-header"><div><p className="eyebrow">Từ VelstrongBook</p><h2 id="editorial-title">Gợi ý đọc từ nhà sách</h2></div><Link className="text-link" href="/bai-viet">Xem tất cả <span aria-hidden="true">→</span></Link></div><ul className="editorial-row">{posts.map((post) => { const date = formatArticleDate(post.publishedAt); return <li className="editorial-card" key={post.id}><Link className="editorial-card-link" href={`/bai-viet/${post.slug}`}><div className="editorial-card-cover"><BlogCover alt="" src={post.coverImageUrl} variant="card" /></div><div className="editorial-card-body">{date ? <time dateTime={post.publishedAt ?? undefined}>{date}</time> : null}<h3>{post.title}</h3>{post.excerpt ? <p>{post.excerpt}</p> : null}</div></Link></li>; })}</ul></section>;
}
