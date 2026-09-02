"use server";

import { revalidatePath } from "next/cache";
import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";

export type ReviewActionResult = { status: "ok" | "error"; message: string };

export async function saveBookReviewAction(_previous: ReviewActionResult, data: FormData): Promise<ReviewActionResult> {
  const bookId = Number(data.get("bookId"));
  const orderItemId = Number(data.get("orderItemId"));
  const rating = Number(data.get("rating"));
  const comment = String(data.get("comment") ?? "").trim();
  const reviewId = Number(data.get("reviewId") ?? 0);
  if (!Number.isInteger(bookId) || !Number.isInteger(orderItemId) || rating < 1 || rating > 5 || !comment || comment.length > 2000) {
    return { status: "error", message: "Vui lòng chọn sao và nhập nhận xét tối đa 2.000 ký tự." };
  }
  try {
    await executeAuthenticatedMutation(`/api/v1/books/${bookId}/reviews${reviewId ? `/${reviewId}` : ""}`, {
      method: reviewId ? "PUT" : "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ orderItemId, rating, comment }),
    });
    revalidatePath(`/sach/${bookId}`);
    return { status: "ok", message: "Đã lưu đánh giá." };
  } catch { return { status: "error", message: "Không thể lưu đánh giá lúc này." }; }
}
