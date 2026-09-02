"use client";

import { useActionState, useState } from "react";

import { saveBlogPostAction, type ActionResult } from "@/app/actions/admin-blog";
import { MarkdownContent } from "@/components/blog/markdown-content";
import { parseMarkdown } from "@/lib/markdown/parse-markdown";
import type { BlogPost } from "@/lib/types/blog";

// Empty message = idle, matching the pattern in add-to-cart-form.tsx.
const INITIAL_STATE: ActionResult = { status: "ok", message: "" };

/**
 * Shared by `/admin/bai-viet/new` (no `post`) and `/admin/bai-viet/[id]`
 * (edit). Preview reuses `<MarkdownContent>` from phase-09 verbatim — same
 * renderer as the public `/bai-viet/{slug}` page, so preview never drifts
 * from what readers actually see.
 */
export function BlogPostForm({ post }: Readonly<{ post?: BlogPost }>) {
  const [state, formAction, pending] = useActionState(saveBlogPostAction, INITIAL_STATE);
  const [content, setContent] = useState(post?.content ?? "");
  const [showPreview, setShowPreview] = useState(false);

  return (
    <form action={formAction} className="admin-form">
      {post ? <input type="hidden" name="id" value={post.id} /> : null}

      <label>
        Tiêu đề
        <input name="title" defaultValue={post?.title} required />
      </label>
      <label>
        Slug (để trống sẽ tự sinh)
        <input name="slug" defaultValue={post?.slug} placeholder="de-trong-se-tu-sinh" />
      </label>
      <label>
        Tóm tắt
        <textarea name="excerpt" defaultValue={post?.excerpt ?? ""} rows={2} />
      </label>
      <label>
        Ảnh bìa (URL http/https)
        <input name="coverImageUrl" defaultValue={post?.coverImageUrl ?? ""} placeholder="https://..." />
      </label>
      <label>
        Mã sách liên quan (tuỳ chọn)
        <input name="bookId" defaultValue={post?.bookId ?? ""} inputMode="numeric" placeholder="Vd: 12" />
      </label>

      <label>
        Nội dung (Markdown)
        <textarea
          name="content"
          rows={20}
          value={content}
          onChange={(event) => setContent(event.target.value)}
          required
        />
      </label>
      <p className="admin-form-hint">
        Hỗ trợ: ## / ### tiêu đề, **đậm**, *nghiêng*, `code`, [chữ](url), danh sách -/1., {"> "}
        trích dẫn, --- gạch ngang.
      </p>

      <button type="button" className="button button-small button-secondary" onClick={() => setShowPreview((v) => !v)}>
        {showPreview ? "Ẩn xem trước" : "Xem trước"}
      </button>

      {showPreview ? <div className="admin-preview">{<MarkdownContent nodes={parseMarkdown(content)} />}</div> : null}

      <button type="submit" className="button button-full" disabled={pending}>
        {pending ? "Đang lưu…" : "Lưu bài viết"}
      </button>

      {state.message ? (
        <p className={state.status === "error" ? "form-status form-status-error" : "form-status"}>{state.message}</p>
      ) : null}
    </form>
  );
}
