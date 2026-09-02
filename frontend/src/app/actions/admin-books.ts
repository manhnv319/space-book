"use server";

import { revalidatePath } from "next/cache";

import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";

/**
 * Zero-JS `<form action={...}>` toggle — each row form always submits BOTH
 * flags (PUT replaces the whole pair), preserving the one it isn't toggling
 * via a hidden input set to the book's current value. No client feedback:
 * failures are logged server-side, matching `admin-blog.ts` table actions.
 */
export async function updateBookFlagsAction(data: FormData): Promise<void> {
  const bookId = Number(data.get("bookId"));
  if (!Number.isInteger(bookId) || bookId <= 0) return;
  const isFeatured = data.get("isFeatured") === "true";
  const isBestseller = data.get("isBestseller") === "true";

  try {
    await executeAuthenticatedMutation(`/api/v1/books/${bookId}/flags`, {
      method: "PUT",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ isFeatured, isBestseller }),
    });
  } catch (error) {
    console.error("Failed to update book flags", {
      bookId,
      error: error instanceof Error ? error.message : String(error),
    });
    return;
  }

  revalidatePath("/admin/sach");
  // Featured/bestseller flags drive the homepage shelves — revalidate them too.
  revalidatePath("/", "layout");
}

export type CoverUploadResult = { status: "ok" | "error"; message: string };

export async function uploadBookCoverAction(_previous: CoverUploadResult, data: FormData): Promise<CoverUploadResult> {
  const bookId = Number(data.get("bookId"));
  const file = data.get("file");
  if (!Number.isInteger(bookId) || !(file instanceof File) || file.size === 0) return { status: "error", message: "Chọn một file ảnh." };
  if (file.size > 10 * 1024 * 1024) return { status: "error", message: "Ảnh tối đa 10 MB." };
  try {
    const payload = new FormData();
    payload.set("file", file);
    await executeAuthenticatedMutation(`/api/v1/books/${bookId}/cover`, { method: "POST", body: payload });
    revalidatePath("/admin/sach");
    revalidatePath(`/sach/${bookId}`);
    revalidatePath("/", "layout");
    return { status: "ok", message: "Đã cập nhật bìa sách." };
  } catch (error) {
    console.error("Failed to upload book cover", { bookId, error: error instanceof Error ? error.message : String(error) });
    return { status: "error", message: "Không thể tải bìa sách lên." };
  }
}

export async function createBookCopyAction(data: FormData): Promise<void> {
  const bookId = Number(data.get("bookId")); const condition = String(data.get("condition") ?? "");
  if (!Number.isInteger(bookId) || !condition) return;
  await executeAuthenticatedMutation(`/api/v1/books/${bookId}/copies`, { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify({ condition }) });
  revalidatePath("/admin/sach");
}

export async function updateBookCopyAction(data: FormData): Promise<void> {
  const copyId = Number(data.get("copyId")); const status = String(data.get("status") ?? ""); const condition = String(data.get("condition") ?? ""); const notes = String(data.get("notes") ?? "");
  if (!Number.isInteger(copyId) || !status || !condition) return;
  await executeAuthenticatedMutation(`/api/v1/book-copies/${copyId}`, { method: "PATCH", headers: { "content-type": "application/json" }, body: JSON.stringify({ status, condition, notes }) });
  revalidatePath("/admin/sach");
}
