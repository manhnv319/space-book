"use client";

import { useActionState } from "react";
import { uploadBookCoverAction, type CoverUploadResult } from "@/app/actions/admin-books";

const INITIAL: CoverUploadResult = { status: "ok", message: "" };

export function BookCoverUploadForm({ bookId }: { bookId: number }) {
  const [state, action, pending] = useActionState(uploadBookCoverAction, INITIAL);
  return <form action={action} className="book-cover-upload-form" encType="multipart/form-data">
    <input name="bookId" type="hidden" value={bookId} />
    <input accept="image/jpeg,image/png,image/webp" name="file" type="file" required />
    <button className="button button-small button-secondary" disabled={pending} type="submit">{pending ? "Đang tải…" : "Đổi bìa"}</button>
    {state.message ? <span className={state.status === "error" ? "form-status-error" : "form-status"}>{state.message}</span> : null}
  </form>;
}
