"use client";

import { useState } from "react";

import { BlogPostForm } from "@/components/admin/blog-post-form";

export function BlogPostCreateDialog() {
  const [open, setOpen] = useState(false);

  return <><button className="button" onClick={() => setOpen(true)} type="button">Viết bài mới</button>
    {open ? <div aria-modal="true" className="admin-dialog-backdrop" onMouseDown={() => setOpen(false)} role="dialog">
      <section className="admin-dialog-card" onMouseDown={(event) => event.stopPropagation()}>
        <header className="admin-dialog-header"><div><p className="eyebrow">Nội dung</p><h2>Viết bài mới</h2></div><button aria-label="Đóng biểu mẫu" className="admin-dialog-close" onClick={() => setOpen(false)} type="button"><svg aria-hidden="true" viewBox="0 0 24 24"><path d="m6 6 12 12M18 6 6 18" fill="none" stroke="currentColor" strokeLinecap="round" strokeWidth="1.8" /></svg></button></header>
        <BlogPostForm />
      </section>
    </div> : null}
  </>;
}
